package pawz.Solitaire;

import pawz.PuzzleSolutionBuilderFrame;
import pawz.Solitaire.CLI.*;
import pawz.Tournament.Exceptions.NotPreparedException;
import pawz.Tournament.Exceptions.WrongStateException;
import pawz.Tournament.Interfaces.*;
import pawz.Tournament.PuzzleSolutionDigester;

import java.util.*;

public class SolitaireTournament {

    private static class NoMoreTicketsException extends Exception{};
    private class DeclarationManager implements ISolutionDeclarationManager<SudokuMove>{

        private final IPuzzleSolutionTicketProxy<SudokuMove, SudokuState> ticket;
        private List<SudokuMove> solution = null;

        private static final PuzzleSolutionDigester<SudokuMove> digester = new PuzzleSolutionDigester<>();

        private DeclarationManager(IPuzzleSolutionTicketProxy<SudokuMove, SudokuState> ticket) {
            this.ticket = ticket;
        }

        @Override
        public void prepare(List<SudokuMove> solution) {
            this.solution = solution;
        }

        @Override
        public void declare() throws WrongStateException, NotPreparedException {
            if(this.solution == null)
                throw new NotPreparedException();
            this.ticket.declareSolution(digester.digestSolution(session.getSessionId(), solution) , System.currentTimeMillis());
        }

        @Override
        public void commit() throws WrongStateException, NotPreparedException {
            if(this.solution == null)
                throw new NotPreparedException();

            this.ticket.commitSolution(solution);
        }
    }

    private final List<IPuzzleSolutionTicketProxy<SudokuMove, SudokuState>> tickets;

    private final Map<Integer, DeclarationManager> declarationManagerMap = new HashMap<>();

    private int currentPuzzle = 0;

    private SudokuPuzzleUIModel puzzleUIModel;
    private SudokuPuzzleUIView puzzleUIView;

    private SudokuPuzzleUIController puzzleUIController;

    private final TicketDashboard dashboard;
    private final IServiceSession session;
    private static final GameDefinition<SudokuMove, SudokuState> gameDefinition = SudokuGameDefinition.getInstance();

    private final Collection<BasicCommandHandler> commandHandlers = new ArrayList<>();

    private boolean completed = false;

    public SolitaireTournament(IPuzzleSolutionTicketService<SudokuMove, SudokuState> service, IServiceSession session) {
        this.session = session;
        this.tickets = service.getAllOwnedTickets(session).stream().toList();
        this.puzzleUIView = new SudokuPuzzleUIView();

        try {
            setNewPuzzle(0);
        } catch (NoMoreTicketsException e){
            throw  new RuntimeException("Failed to create tournament, because service has no tickets at the moment of creation");
        }

        dashboard = new TicketDashboard(service, System.currentTimeMillis());

        initCommandHandlers();

    }

    private void setNewPuzzle(int index) throws NoMoreTicketsException{

        if(!(index < tickets.size()))
            throw new NoMoreTicketsException();

        IPuzzleSolutionTicketProxy<SudokuMove, SudokuState> ticket = tickets.get(index);

        PuzzleSolutionBuilderFrame<SudokuMove, SudokuState> frame = new PuzzleSolutionBuilderFrame<>(ticket.getState(), gameDefinition);
        puzzleUIModel = new SudokuPuzzleUIModel(this.puzzleUIView, frame);
        this.puzzleUIController = new SudokuPuzzleUIController(puzzleUIModel);
        currentPuzzle = index;
    }

    private Optional<DeclarationManager> declare() throws WrongStateException {
        IPuzzleSolutionTicketProxy<SudokuMove, SudokuState> proxy = tickets.get(currentPuzzle);
        if (!gameDefinition.isAcceptable(this.puzzleUIModel.getSolutionBuilder().getCurrentState())){
            // Game state is not acceptable

            System.out.println("The given solution is not acceptable!. Proceed anyway [Y/N case sensitive]");

            Scanner scanner = new Scanner(System.in);

            String choice = scanner.next();

            if(!choice.equals("Y"))
                return Optional.empty();
        }

        List<SudokuMove> solution = this.puzzleUIModel.getSolutionBuilder().getSolution();

        DeclarationManager result = new DeclarationManager(proxy);
        result.prepare(solution);

        try {
            result.declare();
        } catch (NotPreparedException e) {
            throw new RuntimeException(e);
        }

        this.declarationManagerMap.put(proxy.getID(), result);
        return Optional.of(result);
    }

    private void commitAllDeclared(){

        for(DeclarationManager man : declarationManagerMap.values()){
            try {
                man.commit();
            } catch (WrongStateException | NotPreparedException ignored) {}
        }

        for(IPuzzleSolutionTicketProxy<SudokuMove, SudokuState> ticket: tickets) {
            try {
                ticket.verifySolution();
            } catch (WrongStateException ignored) {}
        }

    }

    private int handleCommit(){
        if(!completed){
            System.out.println("Tournament is not completed yet, solve more puzzles");
            return 0;
        }

        commitAllDeclared();
        return 0;
    }


    private void declareAndProceedToNextPuzzle(){
        try {
            if(declare().isPresent())
                setNewPuzzle(currentPuzzle + 1);
        } catch (WrongStateException | NoMoreTicketsException e) {
            completed = true;
        }
    }

    private void show(){
        if(completed){
            dashboard.displayTickets(session);
        } else{
            puzzleUIModel.show();
        }
    }


    private void handleCommand(String[] args, Map<String, String> kwargs) throws NotEnoughArgumentsException {

        for(BasicCommandHandler handler: commandHandlers){
            if(handler.match(args, kwargs)){
                handler.execute(args, kwargs);
                return;
            }
        }

        puzzleUIController.tryExecute(args, kwargs);

    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        CommandParser commandParser = new CommandParser();

        show();

        while(true){
            ImmutablePair<String[], Map<String, String>> pair = commandParser.parseLine(scanner.nextLine());
            try {
                handleCommand(pair.first(), pair.second());
                show();
            } catch (NotEnoughArgumentsException ignored) {}
        }
    }


    private void initCommandHandlers() {

        BasicCommandHandlerBuilder builder = new BasicCommandHandlerBuilder();

        BasicCommandHandler declare = builder.withKeywordMatcher("declare")
                .withRunner(ignored -> {declareAndProceedToNextPuzzle(); return 0;})
                .withDescription("Declares given solution as solved and proceeds to next puzzle if next puzzle exists")
                .withShortHelp("Usage:\n\tdeclare")
                .withLongHelp("""
                        The usage of this command is very simple, it is just
                        \tdeclare
                        \tCommand above will mark given puzzle as complete, and proceed to next puzzle if possible, or proceed to final results\s""")
                .build();

        BasicCommandHandler commit = builder.withKeywordMatcher("commit")
                        .withRunner(pair -> handleCommit())
                        .withDescription("Commits all owned tickets. Note that this command works only all owned tickets were previously declared")
                        .withLongHelp("""
                                The usage of this command is:
                                \tcommit
                                """)
                        .withShortHelp("Usage: commit")
                        .build();

        BasicCommandHandler showTickets = builder.withKeywordMatcher("tickets")
                .withDescription("Displays tickets present in the tournament")
                .withShortHelp("Usage: tickets")
                .withLongHelp("""
                        The usage of this command is:
                        \ttickets
                        """)
                .withRunner(pair -> {dashboard.displayTickets(session); return 0;})
                .build();


        commandHandlers.add(declare);
        commandHandlers.add(commit);
        commandHandlers.add(showTickets);
    }

}
