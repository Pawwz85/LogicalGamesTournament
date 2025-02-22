package pawz.Auth;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteEncodable;

public record SessionHeader<PublicKey>(
        int userId,
        @NotNull String token,
        @NotNull PublicKey publicKey) {
}
