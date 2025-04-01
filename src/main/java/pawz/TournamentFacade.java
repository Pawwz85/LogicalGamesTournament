package pawz;

import com.gmail.woodyc40.pbft.Client;
import com.gmail.woodyc40.pbft.Replica;
import com.google.gson.JsonObject;
import pawz.Auth.SignedMessage;
import pawz.Boot.BootConfiguration;
import pawz.Boot.NodeInfo;
import pawz.Boot.UserIdentity;
import pawz.Components.ComponentPack;
import pawz.Components.Internals.EventLoop;
import pawz.Components.Internals.ServiceProbe;
import pawz.Components.PuzzleDashboardComponent;
import pawz.Components.SolutionBuilderComponent;
import pawz.Components.SubmissionDashboardComponent;
import pawz.P2PClient.ResultParsers.SynchronisationServiceResultParser;
import pawz.P2PClient.SignedMessageFactory;
import pawz.Tournament.DTO.PuzzleSolutionTicketByteDecoder;
import pawz.Tournament.DTO.PuzzleSolutionTicketDTOByteDecoder;
import pawz.Tournament.Exceptions.RepositoryException;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;
import pawz.Tournament.PuzzleDecoder;
import pawz.Tournament.PuzzleSolutionTicket;
import pawz.Tournament.Replika.*;
import pawz.Tournament.Synchronisation.SynchronisationCriticalSection;
import pawz.Tournament.Synchronisation.SynchronisationThread;
import pawz.Transport.ServerThread;
import pawz.Transport.SocketTransport;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TournamentFacade<Move extends ByteEncodable, State extends ByteEncodable> {

    private final PBFTThread<Move, State> pbftThread;

    private final SynchronisationThread<Move, State> syncThread;

    private final ServerThread serverThread;

    private final Thread eventThread;
    private final Thread serviceProbeThread;
    private final Replica<SignedMessage<byte[]>, JsonObject, String> replica;

    private final Client<SignedMessage<byte[]>, JsonObject, String> client;

    private final TournamentSystem<Move, State> tournamentSystem;

    private final ServiceProbe<Move, State> serviceProbe;

    public final EventLoop tournamentEventLoop = new EventLoop();
    private final SynchronisationCriticalSection<ReplicaSnapshot<Move, State>> synchronisationCriticalSection;

    private final SocketTransport socketTransport;

    private final UserIdentity currentUser;
    private final NodeInfo currentNode;

    private final ComponentPack<Move, State> componentPack;
    private final GameDefinition<Move, State> gameDefinition;
    private final BootConfiguration configuration;

    private Optional<UserIdentity> getUserIdentity(BootConfiguration configuration, int nodeID){
        return configuration.userIdentities.stream().filter(i -> i.id() == nodeID).findFirst();
    }

    private Optional<NodeInfo> getNodeInfo(BootConfiguration configuration, int nodeID){
        return configuration.netConfiguration.getNodeInfoList().stream().filter(info -> info.nodeID == nodeID).findFirst();
    }

    public TournamentFacade(BootConfiguration configuration,
                            GameDefinition<Move, State> gameDefinition,
                            List<State> preparedStates,
                            int nodeID) throws IOException {

        Optional<UserIdentity> currentUserOptional = getUserIdentity(configuration, nodeID);
        Optional<NodeInfo> currentNodeInfo = getNodeInfo(configuration, nodeID);

        this.gameDefinition = gameDefinition;
        this.configuration = configuration;

        if(currentNodeInfo.isEmpty() || currentUserOptional.isEmpty()){
            throw new RuntimeException("Invalid tournament configuration");
        }

        currentNode = currentNodeInfo.get();
        currentUser = currentUserOptional.get();

        PuzzleSolutionTicketDTOByteDecoder<Move, State> ticketDTOByteDecoder = new PuzzleSolutionTicketDTOByteDecoder<>(gameDefinition.moveByteDecoder(), gameDefinition.stateByteDecoder(), gameDefinition);
        PuzzleDecoder<Move, State> puzzleDecoder =  new PuzzleDecoder<>(gameDefinition.stateByteDecoder());

        LocalSolutionTicketRepository<Move, State> localSolutionTicketRepository
                = new LocalSolutionTicketRepository<>(new PuzzleSolutionTicketByteDecoder<>(
                     ticketDTOByteDecoder, gameDefinition
        ));

        LocalPuzzleRepository<Move, State> localPuzzleRepository = new LocalPuzzleRepository<>(
               puzzleDecoder
        );

        try {
            initRepositories(preparedStates, localPuzzleRepository, localSolutionTicketRepository);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }


        socketTransport = new SocketTransport(configuration);

        SignedMessageFactory signedMessageFactory = new SignedMessageFactory(
                currentUser.token(),
                currentUser.privateKey()
        );

        synchronisationCriticalSection = new SynchronisationCriticalSection<>();
        tournamentSystem = new TournamentSystem<>(localPuzzleRepository, localSolutionTicketRepository, gameDefinition);
        ServiceReplikaFactory<Move, State> serviceReplikaFactory = new ServiceReplikaFactory<>(configuration, tournamentSystem);


        replica = serviceReplikaFactory.build();
        client = serviceReplikaFactory.buildClient(currentUser.token());

        ConcurrentLinkedQueue<String> msgBuffer = new ConcurrentLinkedQueue<>();


        // TODO: bind sockets to specified ports
        serverThread = new ServerThread(msgBuffer, new ServerSocket(currentNode.pbftPort));

        pbftThread = new PBFTThread<>(msgBuffer, replica, client, synchronisationCriticalSection);

        SynchronisationServiceResultParser<Move, State> synchronisationServiceResultParser = new SynchronisationServiceResultParser<>(new ReplicaSnapshotDecoder<>(ticketDTOByteDecoder, puzzleDecoder));
        syncThread = new SynchronisationThread<>(synchronisationCriticalSection, client, synchronisationServiceResultParser, tournamentSystem.synchronisationService, signedMessageFactory);

        eventThread = new Thread(this.tournamentEventLoop);

        this.componentPack = initComponentPack(gameDefinition);

        serviceProbe = new ServiceProbe<>(tournamentSystem, synchronisationCriticalSection, tournamentEventLoop);
        this.serviceProbeThread = new Thread(serviceProbe);

    }

    public void start(){
        serverThread.start();
        pbftThread.start();
        syncThread.start();
        eventThread.start();
        serviceProbeThread.start();
    }

    public void stop() {
        serverThread.isRunning = false;
        pbftThread.isRunning = false;
        syncThread.isRunning = false;
        tournamentEventLoop.isRunning = false;
        serviceProbe.isRunning = false;
    }

    public void join() throws InterruptedException {
        serverThread.join();
        pbftThread.join();
        syncThread.join();
        eventThread.join();
        serviceProbeThread.join();
    }

    private ComponentPack<Move, State> initComponentPack(GameDefinition<Move, State> gameDefinition){
        ComponentPack<Move, State> result = new ComponentPack<>(
                this.tournamentEventLoop,
                new PuzzleDashboardComponent<>(),
                new SolutionBuilderComponent<>(gameDefinition),
                new SubmissionDashboardComponent<>()
        );

        result.eventLoop.registerComponent(result.puzzleDashboardComponent);
        result.eventLoop.registerComponent(result.solutionBuilderComponent);
        result.eventLoop.registerComponent(result.submissionDashboardComponent);
        return result;
    }

    public ComponentPack<Move, State> getComponentPack(){
        return componentPack;
    }

    @Deprecated
    public TournamentSystem<Move, State> getTournamentSystem() {
        return tournamentSystem;
    }

    public int getPlayerCount(){
        return configuration.userIdentities.size();
    }

    private void initRepositories(List<State> preparedPuzzles, LocalPuzzleRepository<Move, State> puzzleRepository,
                                  LocalSolutionTicketRepository<Move, State> ticketRepository) throws RepositoryException {

        for(var s : preparedPuzzles)
            puzzleRepository.persists(new Puzzle<>(s));

       for(UserIdentity user: configuration.userIdentities)
            for(State s: preparedPuzzles){
                PuzzleSolutionTicket<Move, State> t = new PuzzleSolutionTicket<>(user.id(), 0, s, this.gameDefinition);
                ticketRepository.persists(t);
            }
    }

}
