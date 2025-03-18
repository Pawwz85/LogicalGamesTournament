package pawz.Transport;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReadOnlySocketInfo {
    @NotNull
    String remoteAddress();

    int remotePort();

    int socketHandle();

    int msgCounter();
    long bytesSend();
    int errorCounter();

    @NotNull
    SocketStatus status();

    @Nullable
    String lastError();

}
