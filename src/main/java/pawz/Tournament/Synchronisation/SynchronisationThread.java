package pawz.Tournament.Synchronisation;

import com.gmail.woodyc40.pbft.Client;
import com.gmail.woodyc40.pbft.ClientTicket;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pawz.Auth.SignedMessage;
import pawz.P2PClient.ResultParsers.ReplicaChecksums;
import pawz.P2PClient.ResultParsers.SynchronisationServiceResultParser;
import pawz.P2PClient.SignedMessageFactory;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Replika.ReplicaSnapshot;
import pawz.Tournament.Replika.ReplikaSynchronisationService;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SynchronisationThread<Move extends ByteEncodable, State extends ByteEncodable> extends Thread {
    private final SynchronisationCriticalSection<ReplicaSnapshot<Move, State>> criticalSection;

    private final Client<SignedMessage<byte[]>, JsonObject, String> client;

    private final SynchronisationServiceResultParser<Move, State> resultParser;

    private final ReplikaSynchronisationService<Move, State> synchronisationService;

    private final SignedMessageFactory signedMessageFactory;

    public volatile boolean isRunning = true;

    private static final int relaxedTimeInterval = 5_000;

    private static final int waitingForPBFTThreadActionInterval = 10;

    private int waitingTimeMS =  relaxedTimeInterval;

    public SynchronisationThread(SynchronisationCriticalSection<ReplicaSnapshot<Move, State>> criticalSection, Client<SignedMessage<byte[]>, JsonObject, String> client, SynchronisationServiceResultParser<Move, State> resultParser, ReplikaSynchronisationService<Move, State> synchronisationService, SignedMessageFactory signedMessageFactory) {
        this.criticalSection = criticalSection;
        this.client = client;
        this.resultParser = resultParser;
        this.synchronisationService = synchronisationService;
        this.signedMessageFactory = signedMessageFactory;
    }


    private void checkSynchronisation(){
        waitingTimeMS = 5_000;

        boolean outOfSync = synchronisationCheck();

        if(outOfSync){
            declareOutOfSync();
        }

    }

    private void declareOutOfSync() {
        try(SynchronisationManager<ReplicaSnapshot<Move, State>> synchronisationManager = new SynchronisationManager<>(criticalSection)){
            synchronisationManager.beginSynchronisation();
            waitingTimeMS = waitingForPBFTThreadActionInterval;
        } catch (Exception ignored){}
    }


    private boolean synchronisationCheck() {
        @NotNull SignedMessage<byte[]> request = signedMessageFactory.makeSignedRequest("sync/checksums", null);

        ClientTicket<SignedMessage<byte[]>, JsonObject> requestTicket = client.sendRequest(request);
        JsonObject result;
        boolean outOfSync = false;

        try{
            result = requestTicket.result().get(5000, TimeUnit.MILLISECONDS);
            Optional<ReplicaChecksums> checksums = resultParser.getChecksums(result);

            if(checksums.isEmpty()){
                outOfSync = true;
                System.out.println("Failed to fetch checksum, assuming out of sync ...");
            } else {
                var expected = checksums.get();
                byte[] ticketsChecksums = synchronisationService.calculateTicketRepositoryChecksum();
                byte[] puzzlesChecksums = synchronisationService.calculatePuzzleRepositoryChecksum();

                if(!Arrays.equals(expected.ticketRepositoryChecksums, ticketsChecksums) ||
                   !Arrays.equals(expected.puzzleRepositoryChecksums, puzzlesChecksums))
                    outOfSync = true;

            }

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            outOfSync = true;
        }

        return outOfSync;
    }

    private SignedMessage<byte[]> createFetchSnapshotRequest(){
        String requestID;
        try(SynchronisationManager<ReplicaSnapshot<Move, State>> synchronisationManager = new SynchronisationManager<>(criticalSection)) {
            requestID = synchronisationManager.getSynchronisationRequestId();
        } catch (Exception e){
            declareOutOfSync();
            return null;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("request_id", requestID);

        return signedMessageFactory.makeSignedRequest("sync/snapshot", payload);

    }

    private void fetchReplicaSnapshot() {
        var request = createFetchSnapshotRequest();

        if(request == null)
            return;

        ClientTicket<SignedMessage<byte[]>, JsonObject> requestTicket = client.sendRequest(request);

        JsonObject result;

        try {
            result = requestTicket.result().get(5000, TimeUnit.MILLISECONDS);
            Optional<ReplicaSnapshot<Move, State>> snapshot = resultParser.getSnapshot(result);

            if(snapshot.isEmpty())
                declareOutOfSync();
            else {
                setSnapshot(snapshot.get());
            }


        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            declareOutOfSync();
        }


    }

    private void setSnapshot(ReplicaSnapshot<Move, State> snapshot) {
        try(SynchronisationManager<ReplicaSnapshot<Move, State>> synchronisationManager = new SynchronisationManager<>(criticalSection)) {
            synchronisationManager.setSynchronisationData(snapshot);
        } catch (Exception e){
            declareOutOfSync();
        }
    }


    private void synchronise() {

        try(SynchronisationManager<ReplicaSnapshot<Move, State>> synchronisationManager = new SynchronisationManager<>(criticalSection)) {
            criticalSection.lock.lock();
            assert criticalSection.data != null;
            @NotNull ReplicaSnapshot<Move, State> snapshot = criticalSection.data;
            synchronisationService.forcefullySetTicketRepository(snapshot.tickets);
            synchronisationService.forcefullySetPuzzleRepository(snapshot.puzzles);
            criticalSection.lock.unlock();
            synchronisationManager.declareAsSynchronised();
            waitingTimeMS = relaxedTimeInterval;
        } catch (Exception e){
            declareOutOfSync();
        }
    }

    @Override
    public void run(){

        while (isRunning){
            SynchronisationStatus status = null;
            try(SynchronisationManager<ReplicaSnapshot<Move, State>> synchronisationManager = new SynchronisationManager<>(criticalSection)){
                status = synchronisationManager.getStatus();
            } catch (Exception ignored){}

            if(status != null)
                switch (status){
                    case SYNCHRONISED:
                        checkSynchronisation();
                        break;
                    case SYNCHRONISATION_REQUEST_PREPARED:
                        // Nothing, waiting for client acknowledgement
                        break;

                    case SYNCHRONISATION_REQUEST_ACKNOWLEDGED:
                        fetchReplicaSnapshot();
                        break;

                    case SYNCHRONISATION_REQUEST_SEND:
                    case AWAITING_SYNCHRONISATION_DATA:
                        // These states should not be reached there, so the best way to handle it is too just start over
                        // These phases should be only accessible during fetchReplicaSnapshot() call
                        declareOutOfSync();
                        break;

                    case ALIGNING_SERVICE_THREAD:
                        // Nothing, we are waiting for pbft thread to align itself with synchronisation code
                        break;

                    case READY_TO_SYNCHRONISE:
                        // We are ready to synchronise our application
                        synchronise();

                }

            shortWait();
        }

    }

    private void shortWait() {
        try {
            Thread.sleep(waitingTimeMS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
