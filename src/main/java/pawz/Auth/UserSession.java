package pawz.Auth;

import org.jetbrains.annotations.NotNull;
import pawz.Tournament.Interfaces.IServiceSession;
import pawz.Tournament.SessionType;

public class UserSession implements IServiceSession {

    private final int userId;

    public UserSession(int userId) {
        this.userId = userId;
    }

    @Override
    public SessionType getSessionType() {
        return SessionType.PlayerSession;
    }

    @Override
    public boolean isAuthenticated() {
        return true; // If store managed to create session, this means user had a valid token & signature
    }

    @Override
    public int getSessionId() {
        return userId;
    }
}
