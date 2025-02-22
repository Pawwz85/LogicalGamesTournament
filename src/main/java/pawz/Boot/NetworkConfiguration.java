package pawz.Boot;

import java.util.ArrayList;
import java.util.List;

public class NetworkConfiguration {
    private final int tolerance;
    private final List<Device> devices;

    public NetworkConfiguration(int f, List<Device> devices){
        tolerance = f;
        this.devices = devices;
    }


    public int getTolerance(){
        return tolerance;
    }

    public int f(){
        return tolerance;
    }

    public List<Device> getDevices() {
        return devices;
    }

}
