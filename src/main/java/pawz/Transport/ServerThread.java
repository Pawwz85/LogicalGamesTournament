package pawz.Transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerThread extends Thread {

    private final ConcurrentLinkedQueue<String> msgBuffer;

    private final ServerSocket socket;


    public boolean isRunning;

    private final List<SocketListenerThread> children = new ArrayList<>();

    public ServerThread(ConcurrentLinkedQueue<String> msgBuffer, ServerSocket socket) {
        this.msgBuffer = msgBuffer;
        this.socket = socket;
    }


    @Override
    public void run(){

        while (isRunning){
            try {
                Socket incomingSocket = this.socket.accept();

                SocketListenerThread thread = new SocketListenerThread(incomingSocket, msgBuffer);
                thread.start();
                children.add(thread);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for(var child: children){
            if(child.checkRunningFlag()){
                child.shutdown();
                try {
                    child.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

}
