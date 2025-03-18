package pawz.Tournament.Interfaces;

import com.google.gson.JsonObject;
import pawz.P2PClient.Request;

public interface IRequestRouter {
    JsonObject routeRequest(Request request);
}
