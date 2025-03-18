package pawz.Tournament.Synchronisation;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Exceptions.WrongStateException;

import java.io.WriteAbortedException;
import java.util.UUID;


public class SynchronisationManager<SynchronisationData> implements AutoCloseable{

    private final SynchronisationCriticalSection<SynchronisationData> criticalSection;

    public SynchronisationManager(SynchronisationCriticalSection<SynchronisationData> criticalSection) {
        this.criticalSection = criticalSection;
        criticalSection.lock.lock();
    }

    public void beginSynchronisation(){
        criticalSection.synchronisationRequestId = UUID.randomUUID().toString();
        criticalSection.status = SynchronisationStatus.SYNCHRONISATION_REQUEST_PREPARED;
        criticalSection.data = null;
        criticalSection.pbftThreadHanged = false;
    }

    public void declareRequestAsSend() throws WrongStateException {
        if ( criticalSection.status !=  SynchronisationStatus.SYNCHRONISATION_REQUEST_ACKNOWLEDGED)
            throw  new WrongStateException();
        criticalSection.status = SynchronisationStatus.SYNCHRONISATION_REQUEST_SEND;
        criticalSection.timestamp = System.currentTimeMillis();
    }

    public void acknowledgeRequest()  throws WrongStateException {
            if (criticalSection.status != SynchronisationStatus.SYNCHRONISATION_REQUEST_PREPARED)
                throw new WrongStateException();
        criticalSection.status = SynchronisationStatus.SYNCHRONISATION_REQUEST_ACKNOWLEDGED;
    }


    public boolean isRetryNeeded(){

        switch (criticalSection.status){
            case SYNCHRONISATION_REQUEST_SEND:
            case AWAITING_SYNCHRONISATION_DATA:
            case ALIGNING_SERVICE_THREAD:
                break;
            default: return false;
        }

        int retryTime = 10_000;
        return criticalSection.timestamp + retryTime < System.currentTimeMillis();
    }

    public void setSynchronisationData(@NotNull SynchronisationData data) throws WrongStateException{
        switch (criticalSection.status){
            case SYNCHRONISATION_REQUEST_SEND:
            case AWAITING_SYNCHRONISATION_DATA:
                break;
            default: throw new WrongStateException();
        }

        criticalSection.data = data;

        if(criticalSection.status == SynchronisationStatus.SYNCHRONISATION_REQUEST_SEND)
            criticalSection.status = SynchronisationStatus.ALIGNING_SERVICE_THREAD;
        else
            criticalSection.status = SynchronisationStatus.READY_TO_SYNCHRONISE;
    }

    public void hangPBFTThread() throws WrongStateException{
        switch (criticalSection.status){
            case SYNCHRONISATION_REQUEST_SEND:
            case ALIGNING_SERVICE_THREAD:
                break;
            default: throw new WrongStateException();
        }

        criticalSection.pbftThreadHanged = true;
        if(criticalSection.status == SynchronisationStatus.SYNCHRONISATION_REQUEST_SEND)
            criticalSection.status = SynchronisationStatus.AWAITING_SYNCHRONISATION_DATA;
        else
            criticalSection.status = SynchronisationStatus.READY_TO_SYNCHRONISE;
    }

    public void declareAsSynchronised() throws WrongStateException{

        if(criticalSection.status != SynchronisationStatus.READY_TO_SYNCHRONISE)
            throw new WrongStateException();

        criticalSection.status = SynchronisationStatus.SYNCHRONISED;
        criticalSection.pbftThreadHanged = false;
    }

    public boolean isPBFTThreadHang(){
        return criticalSection.pbftThreadHanged;
    }

    public SynchronisationStatus getStatus(){
        return criticalSection.status;
    }

    public String getSynchronisationRequestId(){
        return criticalSection.synchronisationRequestId;
    }

    public boolean getShutdownValue(){
        return criticalSection.shutdown;
    }

    @Override
    public void close() throws Exception {
        this.criticalSection.lock.unlock();
    }


    public void shutdown() {
        this.criticalSection.shutdown = true;
    }
}
