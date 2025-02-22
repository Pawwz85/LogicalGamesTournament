package pawz.Tournament.Interfaces;

import pawz.Tournament.Exceptions.AuthenticationException;
import pawz.Tournament.SessionType;

public interface IServiceSession {
    SessionType getSessionType();
    boolean isAuthenticated();
    int getSessionId();
}
