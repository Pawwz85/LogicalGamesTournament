package pawz.Auth;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.ByteEncodable;

import java.util.Arrays;
import java.util.Objects;

public final class SignedMessage<Signature> {
    private final Signature signature;
    @NotNull
    private final String sessionToken;
    @NotNull
    private final byte[] payload;

    public SignedMessage(
            Signature signature,
            @NotNull String sessionToken,
            @NotNull byte[] payload) {
        this.signature = signature;
        this.sessionToken = sessionToken;
        this.payload = payload;
    }

    public Signature signature() {
        return signature;
    }

    @NotNull
    public String sessionToken() {
        return sessionToken;
    }

    @NotNull
    public byte[] payload() {
        return payload;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SignedMessage<Signature>) obj;
        return Objects.equals(this.signature, that.signature) &&
                Objects.equals(this.sessionToken, that.sessionToken) &&
                Arrays.equals(this.payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signature, sessionToken, Arrays.hashCode(payload));
    }

    @Override
    public String toString() {
        return "SignedMessage[" +
                "signature=" + signature + ", " +
                "sessionToken=" + sessionToken + ", " +
                "payload=" + Arrays.toString(payload) + ']';
    }

}
