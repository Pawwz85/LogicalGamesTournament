package pawz.Boot;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class BootConfiguration {
    public final @NotNull NetworkConfiguration netConfiguration;

    public final @NotNull List<UserIdentity> userIdentities;

    public BootConfiguration(@NotNull NetworkConfiguration netConfiguration, @NotNull List<UserIdentity> userIdentities) {
        this.netConfiguration = netConfiguration;
        this.userIdentities = userIdentities;
    }

    public static BootConfiguration loadFromFile(Path path){
        return null;
    }
}
