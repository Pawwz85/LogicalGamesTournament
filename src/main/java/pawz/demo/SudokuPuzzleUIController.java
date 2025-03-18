package pawz.demo;

import org.jetbrains.annotations.NotNull;
import pawz.demo.CLI.BasicCommandHandler;
import pawz.demo.CLI.BasicCommandHandlerBuilder;
import pawz.demo.CLI.NotEnoughArgumentsException;
import pawz.Tournament.Interfaces.GameDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public class SudokuPuzzleUIController {
    private final @NotNull SudokuPuzzleUIModel model;

    private final Collection<BasicCommandHandler> commandHandlers;
    private final GameDefinition<SudokuMove, SudokuState> gameDefinition = new SudokuGameDefinition();

    public SudokuPuzzleUIController(@NotNull SudokuPuzzleUIModel model) {
        this.model = model;
        commandHandlers = new ArrayList<>();
        registerCommandHandlers();

    }

    private void  registerCommandHandlers(){
        BasicCommandHandlerBuilder builder = new BasicCommandHandlerBuilder();
        BasicCommandHandler place = builder.withKeywordMatcher("place").withShortHelp("place [int] [int]")
                .withLongHelp("place [int: value] [int: square_id]\n\tExample Usage:\n\tplace 9 46\n\tExample above will set a field 46 to contain 9")
                .withDescription("Places given value on given square")
                .withRunner(pair -> {handlePlaceCommand(pair.first(), pair.second()); return 0;})
                .build();

        BasicCommandHandler undo = builder.withKeywordMatcher("undo").withShortHelp("undo")
                .withLongHelp("undo\n\tExample usage:\n\tundo\n\tExample above will undo last move")
                .withDescription("Undoes last move")
                .withRunner(pair->{handleUndo(); return 0;}).build();

        commandHandlers.add(place);
        commandHandlers.add(undo);
    }

    private void handlePlaceCommand(String[] args, Map<String, String> kwargs) {

        if (args.length < 3) {
            model.setAdditionalText("Command 'place' received too few arguments. Type 'place -man' for help");
            return;
        }

        try{
            int value = Integer.parseInt(args[1]);
            int square_id = Integer.parseInt(args[2]);
            SudokuMove move = new SudokuMove(value, square_id);


            if (!gameDefinition.isMoveLegal(model.getSolutionBuilder().getCurrentState(), move)){
                model.setAdditionalText("Please, enter valid move");
            }
            model.getSolutionBuilder().addMove(move);
            model.onNext(model.getSolutionBuilder());
        } catch (NumberFormatException e){
            model.setAdditionalText("Please, enter valid numbers");
        }

    }
    private void handleUndo(){
        model.getSolutionBuilder().undoMove();
        model.onNext(model.getSolutionBuilder());
    }
    public void displayHelp(){
        model.setAdditionalText(getHelp());
    }

    public String getHelp(){
        StringBuilder stringBuilder = new StringBuilder();

        for(BasicCommandHandler handler: commandHandlers){
            stringBuilder.append(handler.getShortHelpString()).append('\n');
        }
        return stringBuilder.toString();
    }

    public boolean tryExecute(String[] args, Map<String, String> kwargs){
        BasicCommandHandler lastHandler = null;
        try{
            for(BasicCommandHandler handler: commandHandlers){
                if(handler.match(args, kwargs)){
                    lastHandler = handler;
                    handler.execute(args, kwargs);
                    return true;
                }
            }
        } catch (NotEnoughArgumentsException e){
            System.out.println(lastHandler.getLongHelpString());
        }
        //displayHelp();

        return false;
    }
}
