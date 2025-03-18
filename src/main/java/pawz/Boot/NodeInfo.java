package pawz.Boot;

import org.jetbrains.annotations.NotNull;

public class NodeInfo {
    
    public final int nodeID;

    public final @NotNull String clientToken;

    public final @NotNull String address;

    public final int pbftPort;
    public final int clientPort;

    public NodeInfo(int nodeID, @NotNull String clientID, @NotNull String address, int pbftPort, int clientPort) {
        this.nodeID = nodeID;
        this.clientToken = clientID;
        this.address = address;
        this.pbftPort = pbftPort;
        this.clientPort = clientPort;
    }

}
