package pawz.Transport;

import com.gmail.woodyc40.pbft.ClientTransport;
import com.gmail.woodyc40.pbft.ReplicaTransport;
import pawz.Boot.BootConfiguration;
import pawz.Boot.NodeInfo;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class SocketTransport implements ReplicaTransport<String>, ClientTransport<String> {

    private final Map<Integer, Integer> replicaSocketHandles = new HashMap<>();
    private final Map<String, Integer> clientSocketHandles = new HashMap<>();



    public SocketTransport(BootConfiguration configuration ){

        if(configuration != null){
            List<NodeInfo> nodeInfoList =  configuration.netConfiguration.getNodeInfoList();
            
            for(var info: nodeInfoList){
               registerReplikaSocket(info.nodeID, info.address, info.pbftPort);
               registerClientSocket(info.clientToken, info.address, info.clientPort);
            }
        }

    }

    public void registerReplikaSocket(int replikaId, String address, int port){
        int handle = SocketService.getInstance().registerSocket(address, port);
        replicaSocketHandles.put(replikaId, handle);
    }

    public void registerClientSocket(String clientId, String address, int port){
        int handle = SocketService.getInstance().registerSocket(address, port);
        clientSocketHandles.put(clientId, handle);
    }


    private void sendToSocket(Integer handle, String data){
        if(handle != null)
            SocketService.getInstance().sendMsg(
                    handle, data.getBytes(StandardCharsets.UTF_8)
            );
    }

    @Override
    public int countKnownReplicas() {
        return replicaSocketHandles.size();
    }

    @Override
    public void sendRequest(int replicaID, String data) {
        sendMessage(replicaID, data);
    }

    @Override
    public void multicastRequest(String string) {
        for(var handle: replicaSocketHandles.values())
            sendToSocket(handle, string);
    }

    @Override
    public IntStream knownReplicaIds() {
        return replicaSocketHandles.keySet().stream().mapToInt(i->i);
    }

    @Override
    public void sendMessage(int replicaId, String data) {
        Integer handle = replicaSocketHandles.get(replicaId);
        sendToSocket(handle, data);
    }

    @Override
    public void multicast(String data, int... ignoredReplicas) {
        Set<Integer> ignored = Arrays.stream(ignoredReplicas).boxed().collect(Collectors.toSet());

        for (Map.Entry<Integer, Integer> entry: replicaSocketHandles.entrySet())
            if (!ignored.contains(entry.getKey())){
                Integer handle = replicaSocketHandles.get(entry.getKey());
                sendToSocket(handle, data);
        }

    }

    @Override
    public void sendReply(String clientId, String reply) {
        Integer handle = clientSocketHandles.get(clientId);
        sendToSocket(handle, reply);
    }
}
