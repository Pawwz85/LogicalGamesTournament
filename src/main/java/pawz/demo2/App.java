package pawz.demo2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pawz.Boot.BootConfiguration;
import pawz.Boot.NetworkConfiguration;
import pawz.Boot.TemporaryBootDataLoader;
import pawz.Components.*;
import pawz.Components.Internals.TournamentEvent;
import pawz.Components.NamedEvents.PuzzleDashboardEvent;
import pawz.Components.NamedEvents.SubmissionDashboardEvent;
import pawz.Components.SolutionBuilderFrame.SolutionBuilderFrame;
import pawz.Puzzle;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTO;
import pawz.Tournament.PuzzleSolutionTicket;
import pawz.TournamentFacade;
import pawz.demo2.GUI.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App extends Application {

    private static final Random rand = new Random();
    private LightOutState generateRandomState(){
       LightOutState state = new LightOutState();

       for(int i = 0; i< 9; ++i)
           state.board[i] = rand.nextInt() % 2;

       return state;
    }

    private TournamentEvent generatePuzzleRepositoryEvent(){

        // Generate a list of 5 random puzzles
        List<Puzzle<LightOutMove, LightOutState>> repo = new ArrayList<>(5);
        for(int i = 0 ; i<5; ++i)
            repo.add(new Puzzle<>(generateRandomState()));

        return new PuzzleDashboardEvent<>(
                repo
        );
    }

    private TournamentEvent generateSubmissionRecordEvent(){
        List<PuzzleSolutionTicketDTO<LightOutMove, LightOutState>> records = new ArrayList<>(5);

        for(int i = 0; i<5; ++i){
          var ticket =  new PuzzleSolutionTicket<>(0, i, generateRandomState(), new LightOutGameDefinition());
            records.add(ticket.toDto());
        }

        return new SubmissionDashboardEvent<>(records);
    }


    @Override
    public void start(Stage stage) throws Exception {
        TemporaryBootDataLoader loader = new TemporaryBootDataLoader();
        NetworkConfiguration netConfig = new NetworkConfiguration(0, loader.loadNodeInfo());
        BootConfiguration configuration = new BootConfiguration(netConfig, loader.loadIdentities());

        int nodeID = 1;

        List<LightOutState> states = new ArrayList<>();

        for(int i = 0; i<5; ++i)
            states.add(generateRandomState());

        var facade = new TournamentFacade<>(configuration, new LightOutGameDefinition(), states ,nodeID);


        ComponentPack<LightOutMove, LightOutState> pack = facade.getComponentPack();


        LightOutState s = generateRandomState();

       ApplicationModel model = new ApplicationModel(facade, stage);

       Scene scene = new Scene(model.getAsParent());

       stage.setScene(scene);
       stage.show();

       facade.start();


    }
}
