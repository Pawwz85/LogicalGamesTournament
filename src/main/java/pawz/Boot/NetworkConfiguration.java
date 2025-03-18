package pawz.Boot;

import java.util.List;

public class NetworkConfiguration {
    private final int tolerance;
    private final List<NodeInfo> nodeInfoList;

    public NetworkConfiguration(int f, List<NodeInfo> nodeInfoList){
        tolerance = f;
        this.nodeInfoList = nodeInfoList;
    }


    public int getTolerance(){
        return tolerance;
    }

    public int f(){
        return tolerance;
    }

    public List<NodeInfo> getNodeInfoList() {
        return nodeInfoList;
    }

}
