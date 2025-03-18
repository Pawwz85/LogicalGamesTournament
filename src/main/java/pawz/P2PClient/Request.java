package pawz.P2PClient;

import pawz.Tournament.Interfaces.IServiceSession;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class Request {
    public final IServiceSession session;
    public final URI uri;
    public final Map<String, Object> params;


    public Request(IServiceSession session, URI uri, Map<String, Object> params) {
        this.session = session;
        this.uri = uri;
        this.params = params;
    }

    public Request(IServiceSession session, String path, Map<String, Object> params) throws URISyntaxException {
        this.uri = new URI(null, null, path, null, null);
        this.session = session;
        this.params = params;
    }

}
