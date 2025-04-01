package pawz.demo2;

import javafx.stage.Stage;
import pawz.Boot.BootConfiguration;
import pawz.Boot.NetworkConfiguration;
import pawz.Boot.UserIdentity;
import pawz.Tournament.Interfaces.GameDefinition;
import pawz.TournamentFacade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main{
    public static void main(String[] args) throws Exception {
        /*
        NetworkConfiguration networkConfiguration = new NetworkConfiguration(1, new ArrayList<>());
        List<UserIdentity> userIdentities = new ArrayList<>();

        BootConfiguration config = new BootConfiguration(networkConfiguration, userIdentities);
        GameDefinition<LightOutMove, LightOutState> gameDefinition = new LightOutGameDefinition();
        int nodeID = 1;
        TournamentFacade<LightOutMove, LightOutState> facade = new TournamentFacade<>(config, gameDefinition , nodeID);

        facade.start();
        */
        App app = new App();

        // TODO: set up simple ui

        /*
        facade.stop();
        facade.join();
        */
    }

}
