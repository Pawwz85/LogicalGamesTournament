package pawz.P2PClient;

import jdk.jshell.spi.ExecutionControl;
import pawz.Tournament.Interfaces.IServiceSession;

import java.util.Map;

public class Request {
    public final IServiceSession session;
    public final String URI;
    public final Map<String, Object> params;


    public Request(IServiceSession session, String uri, Map<String, Object> params) {
        this.session = session;
        URI = uri;
        this.params = params;
    }

   /*
        Add method that allows creating Request object from parametrised strings
    */
}
