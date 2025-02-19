package pawz.Tournament.Interfaces;

import pawz.Tournament.Exceptions.NotPreparedException;
import pawz.Tournament.Exceptions.WrongStateException;

import java.util.List;

public interface ISolutionDeclarationManager<Move extends ByteEncodable> {

    void prepare(List<Move> solution);
    void declare() throws WrongStateException, NotPreparedException;
    void commit() throws WrongStateException, NotPreparedException;
}
