package pawz.Boot;

import pawz.Auth.DefaultSigner;
import pawz.Auth.ISigner;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public class bootTest {
    public static void main(String[] args){
        TemporaryBootDataLoader loader = new TemporaryBootDataLoader();
        List<UserIdentity> identities = loader.loadIdentities();

        ISigner<byte[], PrivateKey, PublicKey> signer= new DefaultSigner();

        byte[] data = "Test".getBytes();
        System.out.printf("Identities found: %d\n", identities.size());
        for(UserIdentity identity: identities){
            System.out.printf("id=%d, token=%s\n", identity.id(), identity.token());
            System.out.println(identity.publicKey());
            System.out.println(identity.privateKey());
            byte[] signature = signer.sign(data, identity.privateKey());
            System.out.println(signer.verifySignature(signature, data, identity.publicKey()));
        }

    }
}
