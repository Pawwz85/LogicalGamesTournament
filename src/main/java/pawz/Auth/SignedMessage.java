package pawz.Auth;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteEncodable;

public record SignedMessage<Signature>(
        Signature signature,
        @NotNull String sessionToken,
        @NotNull byte[] payload) {
}
