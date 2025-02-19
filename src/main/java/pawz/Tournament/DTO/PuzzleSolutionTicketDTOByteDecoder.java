package pawz.Tournament.DTO;

import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;

import java.io.IOException;

public class PuzzleSolutionTicketDTOByteDecoder<Move extends ByteEncodable, State extends ByteEncodable>
        implements ByteDecoder<PuzzleSolutionTicketDTO<Move, State>> {

    private final  ByteDecoder<Move> moveByteDecoder;
    private final ByteDecoder<State> stateByteDecoder;

    private final GameDefinition<Move, State> gameDefinition;

    public PuzzleSolutionTicketDTOByteDecoder(ByteDecoder<Move> moveByteDecoder, ByteDecoder<State> stateByteDecoder, GameDefinition<Move, State> gameDefinition) {
        this.moveByteDecoder = moveByteDecoder;
        this.stateByteDecoder = stateByteDecoder;
        this.gameDefinition = gameDefinition;
    }

    @Override
    public PuzzleSolutionTicketDTO<Move, State> fromBytes(byte[] bytes) throws IOException {
        PuzzleSolutionTicketDTO<Move, State> result = new PuzzleSolutionTicketDTO<>();
        result.loadFromBytes(bytes, moveByteDecoder, stateByteDecoder);
        return result;
    }
}
