package pawz.Transport;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SocketInfoService implements AutoCloseable {

    private static SocketInfoService instance = null;
    private static final Lock lock = new ReentrantLock();


    private final Map<Integer, SocketInfo> socketInfoMap = new HashMap<>();

    public static SocketInfoService getInstance(){
        lock.lock();

        if(instance == null)
            instance = new SocketInfoService();

        return instance;
    }

    @Override
    public void close() throws Exception {
        lock.unlock();
    }


    // package private since it is intended socket info is updated either by SocketService
    // or by SocketTransportThreat
    Optional<SocketInfo> getMutableInfo(int handle){
        return Optional.ofNullable(socketInfoMap.get(handle));
    }

    public Optional<ReadOnlySocketInfo> getInfo(int handle){
        return Optional.ofNullable(socketInfoMap.get(handle));
    }

    public boolean exists(int handle){
        return socketInfoMap.containsKey(handle);
    }

    public void registerSocketInfo(@NotNull SocketInfo info){
        socketInfoMap.put(info.socketHandle(), info);
    }


}
