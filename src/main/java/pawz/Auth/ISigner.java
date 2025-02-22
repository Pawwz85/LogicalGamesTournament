package pawz.Auth;

import pawz.Tournament.Interfaces.ByteEncodable;

public interface ISigner<Signature, PrivateKey, PublicKey> {
    Signature sign(byte[] data, PrivateKey key);
    boolean verifySignature(Signature signature, byte[] data, PublicKey key);
}
