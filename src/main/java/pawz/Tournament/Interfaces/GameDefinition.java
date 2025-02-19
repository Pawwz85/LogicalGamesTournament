package pawz.Tournament.Interfaces;

import org.jetbrains.annotations.NotNull;

public interface GameDefinition<Move extends ByteEncodable,  State extends ByteEncodable> {
    boolean isAcceptable(@NotNull State s);
    boolean isMoveLegal(@NotNull State s, @NotNull Move m);
    @NotNull State makeMove(@NotNull State s, @NotNull Move m);
}
