package pawz.Tournament.Synchronisation;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SynchronisationManagerTest {

    static class MutableServiceState{
        public final Lock lock = new ReentrantLock();
        public int state = 0;
    }

    static class SimulatedPBFTThread {
       private final SynchronisationCriticalSection<Integer> criticalSection;

       private final ConcurrentLinkedQueue<JsonObject> simulatedMessagesQueue;

       private final MutableServiceState state;


        SimulatedPBFTThread(SynchronisationCriticalSection<Integer> criticalSection, ConcurrentLinkedQueue<JsonObject> simulatedMessagesQueue, MutableServiceState state) {
            this.criticalSection = criticalSection;
            this.simulatedMessagesQueue = simulatedMessagesQueue;
            this.state = state;
        }

        void handleMSGOutOfSync(JsonObject msg) throws Exception {

            boolean hanged = true;

            while (hanged){
                try(var sync = new SynchronisationManager<Integer>(criticalSection)){
                    hanged = sync.isPBFTThreadHang();
                }
                if(hanged)
                    Thread.sleep(10);
            }


            try(var sync = new SynchronisationManager<Integer>(criticalSection)){

                if (!msg.has("id") || !Objects.equals(msg.get("id").getAsString(), sync.getSynchronisationRequestId()))
                    return;

                sync.hangPBFTThread();
            }

        }

        void handleMSGDuringSync(JsonObject msg){
            state.lock.lock();
            state.state += 1;
            state.lock.unlock();
        }

        void run() throws Exception {

            boolean isRunning = true;
            boolean outOfSync;
            while (isRunning){

                try(var sync = new SynchronisationManager<>(criticalSection)) {
                    isRunning = !sync.getShutdownValue();
                    outOfSync = sync.getStatus() != SynchronisationStatus.SYNCHRONISED;

                    if(sync.getStatus() == SynchronisationStatus.SYNCHRONISATION_REQUEST_PREPARED)
                        sync.acknowledgeRequest();


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                JsonObject msg = simulatedMessagesQueue.poll();

                if(msg != null){
                    if(outOfSync)
                        handleMSGOutOfSync(msg);
                    else
                        handleMSGDuringSync(msg);
                }

                Thread.sleep(1);
            }

        }
    }

    @Test
    public void testHelperClasses() throws Exception {
        MutableServiceState state = new MutableServiceState();
        SynchronisationCriticalSection<Integer> criticalSection = new SynchronisationCriticalSection<>();
        ConcurrentLinkedQueue<JsonObject> queue = new ConcurrentLinkedQueue<>();
        SimulatedPBFTThread simulatedPBFTThread  = new SimulatedPBFTThread(criticalSection, queue, state);

        Thread pbft = new Thread(() -> {
            try {
                simulatedPBFTThread.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        pbft.start();

        queue.add(new JsonObject());
        queue.add(new JsonObject());

        Thread.sleep(1000);

        try(var sync = new SynchronisationManager<>(criticalSection)){
            sync.shutdown();
        }

        pbft.join();

        Assertions.assertEquals(2, state.state);
    }

    @Test
    // This scenario covers the case where synchronisation data is sent before pbft threat hangs.
    public void TestSynchronisationRecovery() throws Exception {
        MutableServiceState state = new MutableServiceState();
        SynchronisationCriticalSection<Integer> criticalSection = new SynchronisationCriticalSection<>();
        ConcurrentLinkedQueue<JsonObject> queue = new ConcurrentLinkedQueue<>();
        SimulatedPBFTThread simulatedPBFTThread  = new SimulatedPBFTThread(criticalSection, queue, state);

        String id;
        try(var sync = new SynchronisationManager<>(criticalSection)){
            sync.beginSynchronisation();
            id = sync.getSynchronisationRequestId();
        }

        Thread pbft = new Thread(() -> {
            try {
                simulatedPBFTThread.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        pbft.start();

        JsonObject empty = new JsonObject();
        JsonObject syncReq = new JsonObject();
        syncReq.addProperty("id", id);

        for(int i = 0; i< 100; ++i)
            queue.add(empty);


        boolean t = true;

        while (t){
            try(var sync = new SynchronisationManager<>(criticalSection)){
                if(sync.getStatus() == SynchronisationStatus.SYNCHRONISATION_REQUEST_ACKNOWLEDGED)
                {
                    t = false;
                    queue.add(syncReq);
                    sync.declareRequestAsSend();
                    sync.setSynchronisationData(10);
                }
            }
        }

        for(int i = 0; i< 99; ++i)
            queue.add(empty);

        t = true;

        while (t){
            try(var sync = new SynchronisationManager<>(criticalSection)){
                if(sync.getStatus() == SynchronisationStatus.READY_TO_SYNCHRONISE)
                {
                    t = false;
                }
            }
        }

        // write the retrieved synchronisation value
        state.lock.lock();
        criticalSection.lock.lock();
        state.state = criticalSection.data;
        criticalSection.lock.unlock();
        state.lock.unlock();

        try(var sync = new SynchronisationManager<>(criticalSection)) {
            sync.declareAsSynchronised();
        }

        Thread.sleep(2000);
        try(var sync = new SynchronisationManager<>(criticalSection)){
            sync.shutdown();
        }
        pbft.join();
        Assertions.assertEquals(109, state.state);
    }

    @Test
    // This scenario covers the case where synchronisation data is sent after pbft threat hangs.
    public void TestSynchronisationRecovery2() throws Exception {
        MutableServiceState state = new MutableServiceState();
        SynchronisationCriticalSection<Integer> criticalSection = new SynchronisationCriticalSection<>();
        ConcurrentLinkedQueue<JsonObject> queue = new ConcurrentLinkedQueue<>();
        SimulatedPBFTThread simulatedPBFTThread  = new SimulatedPBFTThread(criticalSection, queue, state);

        String id;
        try(var sync = new SynchronisationManager<>(criticalSection)){
            sync.beginSynchronisation();
            id = sync.getSynchronisationRequestId();
        }

        Thread pbft = new Thread(() -> {
            try {
                simulatedPBFTThread.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        pbft.start();

        JsonObject empty = new JsonObject();
        JsonObject syncReq = new JsonObject();
        syncReq.addProperty("id", id);

        for(int i = 0; i< 100; ++i)
            queue.add(empty);


        boolean t = true;

        while (t){
            try(var sync = new SynchronisationManager<>(criticalSection)){
                if(sync.getStatus() == SynchronisationStatus.SYNCHRONISATION_REQUEST_ACKNOWLEDGED)
                {
                    t = false;
                    queue.add(syncReq);
                    sync.declareRequestAsSend();
                }
            }
        }



        for(int i = 0; i< 99; ++i)
            queue.add(empty);

        t = true;
        while (t){
            try(var sync = new SynchronisationManager<>(criticalSection)){
                if(sync.getStatus() == SynchronisationStatus.AWAITING_SYNCHRONISATION_DATA)
                {
                    sync.setSynchronisationData(10);
                    t = false;
                }
            }
        }

        t = true;

        while (t){
            try(var sync = new SynchronisationManager<>(criticalSection)){
                if(sync.getStatus() == SynchronisationStatus.READY_TO_SYNCHRONISE)
                {
                    t = false;
                }
            }
        }

        // write the retrieved synchronisation value
        state.lock.lock();
        criticalSection.lock.lock();
        state.state = criticalSection.data;
        criticalSection.lock.unlock();
        state.lock.unlock();

        try(var sync = new SynchronisationManager<>(criticalSection)) {
            sync.declareAsSynchronised();
        }

        Thread.sleep(2000);
        try(var sync = new SynchronisationManager<>(criticalSection)){
            sync.shutdown();
        }
        pbft.join();
        Assertions.assertEquals(109, state.state);
    }

    @Test
    public void testSynchronisationRetryNeeded() throws Exception {
        SynchronisationCriticalSection<Integer> criticalSection = new SynchronisationCriticalSection<>();
        try(var sync = new SynchronisationManager<>(criticalSection)){
            sync.beginSynchronisation();
            sync.acknowledgeRequest();
            sync.declareRequestAsSend();
            Assertions.assertFalse(sync.isRetryNeeded());
            Thread.sleep(10_050);
            Assertions.assertTrue(sync.isRetryNeeded());
        }
    }
}