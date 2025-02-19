package Mockups;

import pawz.Tournament.Exceptions.NotPreparedException;
import pawz.Tournament.Exceptions.WrongStateException;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.IPuzzleSolutionTicketProxy;
import pawz.Tournament.Interfaces.ISolutionDeclarationManager;
import pawz.Tournament.PuzzleSolutionDigester;

import java.util.ArrayList;
import java.util.List;

/*
    What makes this class a mockup useful only in unit testing is the way the epoch timestamp
    is generated. Currently, it is only the current system time. In real implementation, timestamp
    should be set by pbft leader.
 */
public class MockedSolutionDeclarationManager<Move extends ByteEncodable, State extends ByteEncodable>
        implements ISolutionDeclarationManager<Move> {

    private final IPuzzleSolutionTicketProxy<Move, State> ticketProxy;
    private List<Move> solution;
    private boolean prepared;

    public MockedSolutionDeclarationManager(IPuzzleSolutionTicketProxy<Move, State> ticketProxy) {
        this.ticketProxy = ticketProxy;
        this.solution = new ArrayList<>();
        prepared = false;
    }

    private void prepareCheck() throws NotPreparedException{
        if (!prepared)
            throw  new NotPreparedException();
    }

    @Override
    public void prepare(List<Move> solution) {
        this.solution = solution;
        prepared = true;
    }

    @Override
    public void declare() throws WrongStateException, NotPreparedException {
        prepareCheck();
        long now = System.currentTimeMillis();
        PuzzleSolutionDigester<Move> digester = new PuzzleSolutionDigester<>();
        byte[] hash = digester.digestSolution(ticketProxy.getPlayerID(), solution);
        ticketProxy.declareSolution(hash, now);
    }

    @Override
    public void commit() throws WrongStateException, NotPreparedException {
        prepareCheck();
        ticketProxy.commitSolution(this.solution);
    }
}
