package pawz.P2PClient;

import com.gmail.woodyc40.pbft.Client;
import com.gmail.woodyc40.pbft.message.ClientReply;
import com.gmail.woodyc40.pbft.message.DefaultClientReply;
import com.gmail.woodyc40.pbft.message.ReplicaReply;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pawz.Auth.SignedMessage;
import pawz.Tournament.Replika.ReplikaDecoder;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ReplyListenerThread extends Thread{
    private final Client<SignedMessage<byte[]>, JsonObject, String> client;

    private final ReplikaDecoder replyDecoder = new ReplikaDecoder();

    private final JsonParser jsonParser = new JsonParser();

    private final Socket socket;

    public volatile boolean isRunning = true;


    public ReplyListenerThread(Client<SignedMessage<byte[]>, JsonObject, String> client, Socket socket) {
        this.client = client;
        this.socket = socket;
    }

    @Override
    public void run(){
        try {

            InputStream rawStream = socket.getInputStream();
            DataInputStream stream = new DataInputStream(rawStream);
            while (isRunning) {
                int msgLength = stream.readInt();
                byte[] data = rawStream.readNBytes(msgLength);
                String transport = new String(data);
                receiveTransport(transport);
            }
        } catch (IOException ignored){
            // TODO: user might want to restart this service actually if something goes wrong, so it would be good to log this fact somewhere
        }
    }

    private void receiveTransport(String transport){
        try{
            JsonObject msg = jsonParser.parse(transport).getAsJsonObject();

            if(msg.get("type").getAsString().equals("reply")){
                ReplicaReply<JsonObject> reply = replyDecoder.parseReply(msg);
                ClientReply<JsonObject> clientReply = new DefaultClientReply<>(reply.viewNumber(), reply.timestamp(), client, reply.replicaId(), reply.result());
                client.recvReply(clientReply);
            }

        } catch (Exception ignored){}
    }
}


