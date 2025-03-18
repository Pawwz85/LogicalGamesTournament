package pawz.Tournament.Synchronisation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SynchronisationCriticalSection<SynchronisationData> {
    final Lock lock = new ReentrantLock();

    SynchronisationStatus status = SynchronisationStatus.SYNCHRONISED;

    /*
        This field allows a replica to identify its own synchronisation request.
        See commend above `pbftThreadHanged` flag for information why is this necessary.
    */

    @NotNull String synchronisationRequestId = UUID.randomUUID().toString();

    /*
        This field exist to check when to attempt a retry of a synchronisation procedure.
    */
    long timestamp = 0;

    /*
        In order to synchronise replica properly, the pbft thread should be hanged
        right after it processed its own synchronisation request. Otherwise, there will be a serious risk
        that we might omit a valid request over a synchronised state, because synchronisation was not fully
        completed. Because of that, after pbft thread serves its own synchronisation request, the thread is hanged
        until the synchronisation is achieved.
    */
    boolean pbftThreadHanged = false;

    /*
        If client successfully fetched data needed to synchronise node, it will be stored there until the
        valid synchronisation can take place.
    */
    @Nullable SynchronisationData data = null;

    boolean shutdown = false;
}
