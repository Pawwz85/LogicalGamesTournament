package pawz.P2PClient;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RequestParserTest {
    private final static RequestParser parser = new RequestParser();

    private byte[] stringToBytes (String s) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);
        stream.writeUTF(s);
        return byteArrayOutputStream.toByteArray();
    }

    private byte[] encodeRequest(URI uri, @Nullable String jsonParams) throws IOException {
        String UriString = uri.toString();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);

        byte[] uriBytes = stringToBytes(UriString);
        stream.writeInt(uriBytes.length);
        byte[] params = null;

        if(jsonParams != null) {
            params = stringToBytes(jsonParams);
            stream.writeInt(params.length);
        } else
            stream.writeInt(0);

        byteArrayOutputStream.write(uriBytes);
        if(jsonParams != null)
            byteArrayOutputStream.write(params);

        return byteArrayOutputStream.toByteArray();
    };

    @Test
    public void testBaseCase() throws RequestParser.ParsingException, URISyntaxException, IOException {
        URI uri = new URI("/");
        String json = "{\"var\":\"test\"}";
        byte[] encoded = encodeRequest(uri, json);
        Request req = parser.fromBytes(encoded, null);

        assertEquals(uri, req.uri);
        assertEquals("test", req.params.get("var"));
    }

    @Test
    public void testNoParams() throws RequestParser.ParsingException, URISyntaxException, IOException{
        URI uri = new URI("/");
        byte[] encoded = encodeRequest(uri, null);
        Request req = parser.fromBytes(encoded, null);

        assertTrue(req.params.isEmpty());
    }

    @Test
    public void testInvalidJson() throws URISyntaxException, IOException {
        URI uri = new URI("/");
        byte[] encoded = encodeRequest(uri, "invalid json");
        Assertions.assertThrows(RequestParser.ParsingException.class, ()-> parser.fromBytes(encoded, null));
    }

    @Test
    public void testInvalidUri() throws IOException {
        String invalidUri = "//Invalid Uri";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);
        stream.writeUTF(invalidUri);
        byte[] uriBytes = byteArrayOutputStream.toByteArray().clone();

        byteArrayOutputStream.reset();
        stream.writeInt(uriBytes.length);
        stream.writeInt(0);
        byteArrayOutputStream.write(uriBytes);

        Assertions.assertThrows(RequestParser.ParsingException.class, ()->parser.fromBytes(byteArrayOutputStream.toByteArray(), null));
    }

    @Test
    public void testEmptyBytes(){
        Assertions.assertThrows(RequestParser.ParsingException.class, ()->parser.fromBytes(new byte[0], null));
    }
}