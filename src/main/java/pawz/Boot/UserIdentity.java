package pawz.Boot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.PrivateKey;
import java.security.PublicKey;

public record UserIdentity(int id, @NotNull String token, @NotNull PublicKey publicKey, @Nullable PrivateKey privateKey) { }
