package pawz.Transport;

class MsgTransport {
    public final int socketHandle;
    public final byte[] msg;

    MsgTransport(int socketHandle, byte[] msg) {
        this.socketHandle = socketHandle;
        this.msg = msg;
    }
}
