package pawz.Auth;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteEncodable;

import java.util.Objects;

public final class SessionHeader<PublicKey> {
    private final int userId;
    @NotNull
    private final String token;
    @NotNull
    private final PublicKey publicKey;

    public SessionHeader(
            int userId,
            @NotNull String token,
            @NotNull PublicKey publicKey) {
        this.userId = userId;
        this.token = token;
        this.publicKey = publicKey;
    }

    public int userId() {
        return userId;
    }

    @NotNull
    public String token() {
        return token;
    }

    @NotNull
    public PublicKey publicKey() {
        return publicKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SessionHeader) obj;
        return this.userId == that.userId &&
                Objects.equals(this.token, that.token) &&
                Objects.equals(this.publicKey, that.publicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, token, publicKey);
    }

    @Override
    public String toString() {
        return "SessionHeader[" +
                "userId=" + userId + ", " +
                "token=" + token + ", " +
                "publicKey=" + publicKey + ']';
    }

}
