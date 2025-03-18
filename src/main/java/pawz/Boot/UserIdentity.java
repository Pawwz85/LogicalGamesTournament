package pawz.Boot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

public final class UserIdentity {
    private final int id;
    @NotNull
    private final String token;
    @NotNull
    private final PublicKey publicKey;
    @Nullable
    private final PrivateKey privateKey;

    public UserIdentity(int id, @NotNull String token, @NotNull PublicKey publicKey, @Nullable PrivateKey privateKey) {
        this.id = id;
        this.token = token;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public int id() {
        return id;
    }

    @NotNull
    public String token() {
        return token;
    }

    @NotNull
    public PublicKey publicKey() {
        return publicKey;
    }

    @Nullable
    public PrivateKey privateKey() {
        return privateKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UserIdentity) obj;
        return this.id == that.id &&
                Objects.equals(this.token, that.token) &&
                Objects.equals(this.publicKey, that.publicKey) &&
                Objects.equals(this.privateKey, that.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, publicKey, privateKey);
    }

    @Override
    public String toString() {
        return "UserIdentity[" +
                "id=" + id + ", " +
                "token=" + token + ", " +
                "publicKey=" + publicKey + ", " +
                "privateKey=" + privateKey + ']';
    }
}
