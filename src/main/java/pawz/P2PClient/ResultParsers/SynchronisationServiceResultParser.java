package pawz.P2PClient.ResultParsers;

import com.google.gson.JsonObject;
import pawz.Tournament.Interfaces.ByteEncodable;
import pawz.Tournament.Replika.ReplicaSnapshot;
import pawz.Tournament.Replika.ReplicaSnapshotDecoder;

import java.util.Base64;
import java.util.Optional;

public class SynchronisationServiceResultParser<Move extends ByteEncodable, State extends ByteEncodable> {
    private final ReplicaSnapshotDecoder<Move, State> replicaSnapshotDecoder;


    public SynchronisationServiceResultParser(ReplicaSnapshotDecoder<Move, State> stateReplicaSnapshotDecoder) {
        this.replicaSnapshotDecoder = stateReplicaSnapshotDecoder;
    }

    public Optional<ReplicaSnapshot<Move, State>> getSnapshot(JsonObject APIResult){
        try {
            int statusCode = APIResult.get("http_status_code").getAsInt();

            if( statusCode == 200){
                String snapshotBase64 = APIResult.get("snapshot").getAsString();
                byte[] snapshotBytes = Base64.getDecoder().decode(snapshotBase64);
                return Optional.of(replicaSnapshotDecoder.fromBytes(snapshotBytes));
            }

        } catch (Exception ignored){}
        return Optional.empty();
    }

    public Optional<ReplicaChecksums> getChecksums(JsonObject APIResult){
        try {
            int statusCode = APIResult.get("http_status_code").getAsInt();

            if(statusCode == 200){
                String puzzlesChecksumBase64 = APIResult.get("puzzles_checksum").getAsString();
                String ticketsChecksumBase64 = APIResult.get("tickets_checksum").getAsString();

                return Optional.of(
                        new ReplicaChecksums(
                                Base64.getDecoder().decode(puzzlesChecksumBase64),
                                Base64.getDecoder().decode(ticketsChecksumBase64)
                        )
                );
            }

        } catch (Exception ignored){}

        return Optional.empty();
    }
}
