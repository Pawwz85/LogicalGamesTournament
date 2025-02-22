package pawz.Auth;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;

public class DefaultSigner implements ISigner<byte[], PrivateKey, PublicKey> {

    private final static String algorithm = "SHA224withECDSA";
    private final static Signature signature;

    static {
        try {
            signature = Signature.getInstance(algorithm, new BouncyCastleProvider());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] sign(byte[] data, PrivateKey privateKey) {
        try {
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch ( SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifySignature(byte[] signature, byte[] data, PublicKey publicKey) {
        try {
            Signature signer = DefaultSigner.signature;
            signer.initVerify(publicKey);
            signer.update(data);
            return signer.verify(signature);
        } catch (SignatureException | InvalidKeyException e) {
            // TODO: log this exception
            return false;
        }
    }
}
