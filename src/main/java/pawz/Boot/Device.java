package pawz.Boot;

import org.jetbrains.annotations.NotNull;

public record Device(pawz.Boot.Device.IPAddress address, int port) {

    public enum IPVersion {
        V4,
        V6
    }

    public static class IPAddress {
        public @NotNull IPVersion version;
        public @NotNull String address;

        public IPAddress(@NotNull IPVersion version, @NotNull String address){
            this.address = address;
            this.version = version;
        }
    }

}
