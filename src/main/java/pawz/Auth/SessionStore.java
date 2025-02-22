package pawz.Auth;

import org.jetbrains.annotations.NotNull;
import pawz.Boot.BootConfiguration;
import pawz.Boot.UserIdentity;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Interfaces.IServiceSession;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionStore<Signature> {
    private final @NotNull ISigner<Signature, PrivateKey, PublicKey> signer;

    private final Map<String, SessionHeader<PublicKey>> store = new HashMap<>();

    public SessionStore(@NotNull ISigner<Signature, PrivateKey, PublicKey> signer) {
        this.signer = signer;
    }

    public void configure(BootConfiguration configuration){
        for(UserIdentity identity : configuration.userIdentities){
            store.put(identity.token(), new SessionHeader<>(identity.id(),  identity.token(), identity.publicKey()));
        }
    }

    public Map<String, SessionHeader<PublicKey>> getStore() {
        return store;
    }

    public Optional<IServiceSession> authenticate(SignedMessage<Signature> msg){

        SessionHeader<PublicKey> sessionHeader = store.get(msg.sessionToken());

        if(sessionHeader == null)
            return Optional.empty();

        if(signer.verifySignature(msg.signature(), msg.payload(), sessionHeader.publicKey())){
            return Optional.of(new UserSession(sessionHeader.userId()));
        }

        return Optional.empty();
    }

}
