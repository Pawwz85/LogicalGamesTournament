package pawz.Transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageDispatchingThread extends Thread {

    private Socket socket = null;
    private final int socketHandle;

    public final ConcurrentLinkedQueue<byte[]> queue = new ConcurrentLinkedQueue<>();

    public boolean exitFlag = false;


    public MessageDispatchingThread(int socketHandle) {
        this.socketHandle = socketHandle;
    }

    private Optional<Socket> createSocket(){
        Socket socket = new Socket();
        return restoreConnection(socket);
    }

    private Optional<Socket> restoreConnection(Socket socket){
        SocketInfo info;

        try(var socketInfoSession = SocketInfoService.getInstance()){
            info = socketInfoSession.getMutableInfo(socketHandle).orElse(null);

            if (info == null)
                return Optional.empty();

            info = info.clone();

        } catch (Exception e){
            return Optional.empty();
        }

        var address = new InetSocketAddress(info.remoteAddress(), info.remotePort());

        try {
            socket.connect(address, 1000);
        } catch (IOException e){
            registerError(e);
            return Optional.empty();
        }

        try(var socketInfoSession = SocketInfoService.getInstance()){
            info = socketInfoSession.getMutableInfo(socketHandle).orElse(null);

            if(info != null)
                info.setStatus(SocketStatus.active);

        }catch (Exception ignored){}

        return Optional.of(socket);

    }

    Optional<Socket> resolveSocket(){
        Socket cachedSocket = socket;

        if(cachedSocket == null){
            return createSocket();
        }

        if(!cachedSocket.isConnected()){
            return restoreConnection(cachedSocket);
        }

        return Optional.of(cachedSocket);
    }

    private void registerError(Exception e){
        try(var socketInfoService = SocketInfoService.getInstance()){
            Optional<SocketInfo> optionalSocketInfo =  socketInfoService.getMutableInfo(socketHandle);

            if(optionalSocketInfo.isPresent()){
                SocketInfo socketInfo = optionalSocketInfo.get();
                socketInfo.setStatus(SocketStatus.inactive);
                socketInfo.registerError(e.getMessage());
            }

        }catch (Exception e2){
            throw new RuntimeException();
        }
    }

    private void registerSuccessfulTransfer(byte[] msg){
        try(var socketInfoService = SocketInfoService.getInstance()){
            Optional<SocketInfo> optionalSocketInfo =  socketInfoService.getMutableInfo(socketHandle);

            if(optionalSocketInfo.isPresent()){
                SocketInfo socketInfo = optionalSocketInfo.get();
                socketInfo.registerSuccessfulMsgSend(msg);
            }

        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    private boolean isSockedValid(){
        return socket != null && socket.isConnected();
    }

    @Override
    public void run(){
        byte[] msg;

        while(!exitFlag){

            // if socket is null or unconnected, try to resolve it
            if(!isSockedValid()){
                socket = resolveSocket().orElse(null);
                continue;
            }


            msg = queue.poll();
            if( msg != null){
                serveMessage(msg);
            } else {
                shortWait();
            }
        }

    }

    private void serveMessage(byte[] msg) {
        try{
            DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
            stream.writeInt(msg.length);
            socket.getOutputStream().write(msg);
            registerSuccessfulTransfer(msg);
        } catch (IOException e){
            registerError(e);
        }
    }

    private static void shortWait() {
        try {
            sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
