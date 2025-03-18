package pawz.Transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SocketListenerThread extends Thread {

    private final Lock lock = new ReentrantLock();

    private final Socket socket;

    private final ConcurrentLinkedQueue<String> msgBuffer;

    private boolean isRunning = true;


    public SocketListenerThread(Socket socket, ConcurrentLinkedQueue<String> msgBuffer) {
        this.socket = socket;
        this.msgBuffer = msgBuffer;
    }


    public boolean checkRunningFlag(){
        boolean result;
        lock.lock();
        result = isRunning;
        lock.unlock();
        return result;
    }

    public void shutdown(){
        lock.lock();
        isRunning = true;
        lock.unlock();
    }


    @Override
    public void run() {
        InputStream stream;
        DataInputStream dataInputStream;
        try {
            stream = socket.getInputStream();
            dataInputStream = new DataInputStream(stream);

            while (checkRunningFlag()){
                int msgLength = dataInputStream.readInt();
                byte[] msg = dataInputStream.readNBytes(msgLength);

                try{
                    String parsed = new String(msg);
                    msgBuffer.add(parsed);
                }catch (Exception ignored){}

            }

        } catch (IOException ignored) {
            shutdown();
        }


    }
}
