package pawz.Transport;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SocketInfo implements ReadOnlySocketInfo, Cloneable{

    @NotNull
    private final String remoteAddress;

    private final int remotePort;

    private final int socketHandle;

    private int msgCounter = 0;

    private int errorCounter = 0;

    private long bytesSend = 0;

    @Nullable
    private String lastErrorCause = null;


    public SocketInfo(@NotNull String address, int remotePort, int socketHandle){
        this.remoteAddress = address;
        this.remotePort = remotePort;
        this.socketHandle = socketHandle;
    }

    @NotNull
    private SocketStatus status = SocketStatus.inactive;


    @NotNull
    @Override
    public String remoteAddress() {
        return remoteAddress;
    }

    @Override
    public int remotePort() {
        return remotePort;
    }

    @Override
    public int socketHandle() {
        return socketHandle;
    }

    @Override
    public int msgCounter() {
        return msgCounter;
    }

    @Override
    public long bytesSend() {
        return bytesSend;
    }

    @Override
    public int errorCounter() {
        return errorCounter;
    }

    @NotNull
    @Override
    public SocketStatus status() {
        return status;
    }

    @Nullable
    @Override
    public String lastError() {
        return lastErrorCause;
    }


    public void registerError(@NotNull String cause){
        lastErrorCause = cause;
        errorCounter++;
    }

    public void registerSuccessfulMsgSend(byte[] msg){
        msgCounter++;
        bytesSend += msg.length;
    }

    public void setStatus(@NotNull SocketStatus status){
        this.status = status;
    }

    @Override
    public SocketInfo clone() {
        try {
            return (SocketInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
