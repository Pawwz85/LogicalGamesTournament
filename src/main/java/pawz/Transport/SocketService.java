package pawz.Transport;

import java.util.HashMap;
import java.util.Map;

public class SocketService {

    private static SocketService instance = null;

    private final Map<Integer, MessageDispatchingThread> socketThreads = new HashMap<>();

    private int socketHandleSequence = 0;

    public static SocketService getInstance(){
        if(instance == null)
            instance = new SocketService();

        return instance;
    }

    private int genHandle(){
        return ++socketHandleSequence;
    }

    public int registerSocket(String address, int remotePort){
        int handle = genHandle();

        SocketInfo socketInfo = new SocketInfo(address, remotePort, handle);

        try (var socketInfoService = SocketInfoService.getInstance() ){
            socketInfoService.registerSocketInfo(socketInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var thread = new MessageDispatchingThread(handle);
        socketThreads.put(handle, thread);
        thread.start();
        return handle;
    }

    public void sendMsg(int handle, byte[] msg){
        MessageDispatchingThread socketThread = socketThreads.get(handle);

        if( socketThread != null){
           socketThread.queue.add(msg);
        }
    }

    public void closeAllThreads(){
        for (var thread: socketThreads.values()){
            thread.exitFlag = true;
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
