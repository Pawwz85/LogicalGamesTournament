package pawz.Components.Internals;

import pawz.Components.NamedEvents.PuzzleDashboardEvent;
import pawz.Components.NamedEvents.SubmissionDashboardEvent;
import pawz.Components.NamedEvents.SyncStatusEvent;
import pawz.Puzzle;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Replika.ReplicaSnapshot;
import pawz.Tournament.Replika.ReplikaSynchronisationService;
import pawz.Tournament.Replika.TournamentSystem;
import pawz.Tournament.Synchronisation.SynchronisationCriticalSection;
import pawz.Tournament.Synchronisation.SynchronisationManager;
import pawz.Tournament.Synchronisation.SynchronisationStatus;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ServiceProbe<Move extends ByteEncodable, State extends ByteEncodable> implements Runnable {

    private long probingPeriodMillis = 500;

    private long lastProbe = 0;

    private byte[] puzzleRepositoryChecksum = new byte[16];
    private byte[] submissionRepositoryChecksum = new byte[16];

    private boolean lastSyncStatus = true;

    private final TournamentSystem<Move, State> tournamentSystem;

    private final SynchronisationCriticalSection<ReplicaSnapshot<Move, State>> synchronisationCriticalSection;

    private final EventLoop eventConsumer;

    public volatile boolean isRunning = false;

    public ServiceProbe(TournamentSystem<Move, State> tournamentSystem, SynchronisationCriticalSection<ReplicaSnapshot<Move, State>> synchronisationCriticalSection, EventLoop eventConsumer) {
        this.tournamentSystem = tournamentSystem;
        this.synchronisationCriticalSection = synchronisationCriticalSection;
        this.eventConsumer = eventConsumer;
    }

    public void setProbingPeriod(long value, TimeUnit timeUnit){
        probingPeriodMillis = timeUnit.toMillis(value);
    }


    boolean checkIfProbeIsReady() {
        return lastProbe + probingPeriodMillis < System.currentTimeMillis();
    }

    public void probe(){
        ReplikaSynchronisationService<Move, State> syncService =  tournamentSystem.synchronisationService;
        var currentPuzzleRepoChecksum = syncService.calculatePuzzleRepositoryChecksum();
        var currentSubmissionChecksum = syncService.calculateTicketRepositoryChecksum();
        boolean currentSyncStatus = false;

        if(!Arrays.equals(currentPuzzleRepoChecksum, puzzleRepositoryChecksum)){
            emitPuzzleEvent();
        }

        if(!Arrays.equals(currentSubmissionChecksum, submissionRepositoryChecksum)){
            emitSubmissionEvent();
        }

        try(var syncMan = new SynchronisationManager<>(synchronisationCriticalSection)){
            currentSyncStatus = syncMan.getStatus() == SynchronisationStatus.SYNCHRONISED;
        }catch (Exception ignored){}


        if(currentSyncStatus != lastSyncStatus){
            emitSyncEvent(currentSyncStatus);
        }

        this.puzzleRepositoryChecksum = currentPuzzleRepoChecksum;
        this.submissionRepositoryChecksum = currentSubmissionChecksum;
        this.lastSyncStatus = currentSyncStatus;
        lastProbe = System.currentTimeMillis();
    }

    private void emitSyncEvent(boolean currentSyncStatus) {
        SyncStatusEvent event = new SyncStatusEvent(currentSyncStatus);
        eventConsumer.addEvent(event);
    }

    private void emitPuzzleEvent() {
        Collection<Puzzle<Move, State>> data = tournamentSystem.getPuzzleService().getAllPuzzles();
        PuzzleDashboardEvent<Move, State> event = new PuzzleDashboardEvent<>(
                new ArrayList<>(data)
        );
        eventConsumer.addEvent(event);
    }

    private void emitSubmissionEvent(){
        List<PuzzleSolutionTicketDTO<Move, State>> records = new ArrayList<>(tournamentSystem.getTicketService().getAllTicketsRecords());
        SubmissionDashboardEvent<Move, State> event = new SubmissionDashboardEvent<>(
            records
        );
        eventConsumer.addEvent(event);
    }

    @Override
    public void run() {
        isRunning = true;

        while (isRunning){
            try {
                Thread.sleep(probingPeriodMillis);
                probe();
            } catch (InterruptedException e) {
                isRunning = false;
            }
        }

    }
}
