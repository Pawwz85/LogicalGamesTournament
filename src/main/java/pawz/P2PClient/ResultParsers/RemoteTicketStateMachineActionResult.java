package pawz.P2PClient.ResultParsers;

import pawz.Tournament.Exceptions.MalformedResponseException;
import pawz.Tournament.Exceptions.OwnershipException;
import pawz.Tournament.Exceptions.WrongStateException;

public class RemoteTicketStateMachineActionResult {
    public final boolean success;
    public final boolean ownershipErrorOccurred;
    public final boolean wrongStateExceptionOccurred;

    public RemoteTicketStateMachineActionResult(boolean success, boolean ownershipErrorOccurred, boolean wrongStateExceptionOccurred) {
        this.success = success;
        this.ownershipErrorOccurred = ownershipErrorOccurred;
        this.wrongStateExceptionOccurred = wrongStateExceptionOccurred;
    }

    public void assertSuccessOrThrowErrors() throws OwnershipException, WrongStateException, MalformedResponseException {

        if(ownershipErrorOccurred)
            throw new OwnershipException();

        if(wrongStateExceptionOccurred)
            throw new WrongStateException();

        if(!success)
            throw new MalformedResponseException();
    }

}
