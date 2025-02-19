package pawz.Tournament.DTO;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteDecoder;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.GameDefinition;
import pawz.Tournament.PuzzleSolutionTicket;

import java.io.IOException;

public class PuzzleSolutionTicketByteDecoder<Move extends ByteEncodable, State extends ByteEncodable> implements ByteDecoder<PuzzleSolutionTicket<Move, State>> {

    private @NotNull
    final PuzzleSolutionTicketDTOByteDecoder<Move, State> dtoByteDecoder;

    private @NotNull
    final GameDefinition<Move, State> gameDefinition;

    public PuzzleSolutionTicketByteDecoder(@NotNull PuzzleSolutionTicketDTOByteDecoder<Move, State> dtoByteDecoder, @NotNull GameDefinition<Move, State> gameDefinition) {
        this.dtoByteDecoder = dtoByteDecoder;
        this.gameDefinition = gameDefinition;
    }

    @Override
    public PuzzleSolutionTicket<Move, State> fromBytes(byte[] bytes) throws IOException {
        PuzzleSolutionTicketDTO<Move, State> dto = dtoByteDecoder.fromBytes(bytes);
        return new PuzzleSolutionTicket<>(dto, gameDefinition);
    }
}
