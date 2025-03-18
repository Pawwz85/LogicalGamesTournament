package pawz.Boot;

import com.google.gson.JsonElement;
import com.google.gson.*;
import com.google.gson.JsonParser;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TemporaryBootDataLoader {
    private static final Path temporaryDataPath = Path.of("temporaryBootData");

    private final static KeyFactory keyFactory;

    private final static JsonParser jsonParser = new JsonParser();

    static {
        try {
            Provider provider = new BouncyCastleProvider();
            keyFactory = KeyFactory.getInstance("EC", provider);
        } catch (NoSuchAlgorithmException  e) {
            throw new RuntimeException(e);
        }
    }

    private List<Path> getUserDirectories(){
        List<Path> result = new ArrayList<>();
        try ( DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Path.of(temporaryDataPath.toAbsolutePath() + "/tournament_users"))) {
            for(Path path: directoryStream){
                result.add(path);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }


    private Optional<PublicKey> loadPublicKey(Path pathToPemFile) {
        try(FileReader fileReader = new FileReader(new File(pathToPemFile.toUri()))){
            PemReader reader = new PemReader(fileReader);
            PemObject object = reader.readPemObject();
            KeySpec keySpec = new X509EncodedKeySpec(object.getContent());
            return Optional.of(keyFactory.generatePublic(keySpec));
        } catch (IOException e){
            return Optional.empty();
        } catch (InvalidKeySpecException e){
            System.out.println("User key is invalid");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<PrivateKey> loadPrivateKey(Path pathToPemFile){

        try(FileReader fileReader = new FileReader(new File(pathToPemFile.toUri()))){
            PemReader reader = new PemReader(fileReader);
            PemObject object = reader.readPemObject();

             KeySpec keySpec = new PKCS8EncodedKeySpec(object.getContent());

            return Optional.of(keyFactory.generatePrivate(keySpec));
        } catch (IOException e){
            return Optional.empty();
        } catch (InvalidKeySpecException e){
            System.out.println("User key is invalid");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<JsonObject> loadUserInfo(Path path) {
        try { // load user data
            try (FileReader fileReader = new FileReader(new File(path.toUri()))) {
                JsonElement element = jsonParser.parse(fileReader);
                System.out.println(element);
                if (element.isJsonObject()) {
                    return Optional.ofNullable(element.getAsJsonObject());
                }
            }
        } catch (IOException ignored) {}
            return Optional.empty();
    }
    public List<UserIdentity> loadIdentities(){
        List<Path> userDirectories = getUserDirectories();
        List<UserIdentity> identities = new ArrayList<>();

        for(Path p : userDirectories){

            Path userInfoPath = Path.of(p.toAbsolutePath() + "/user_info.json");
            Path publicKeyPath = Path.of(p.toAbsolutePath() + "/public_key.pem");
            Path privateKeyPath = Path.of(p.toAbsolutePath() + "/private_key.pem");

            Optional<JsonObject> optionalUserInfo = loadUserInfo(userInfoPath);
            Optional<PublicKey> optionalPublicKey = loadPublicKey(publicKeyPath);
            Optional<PrivateKey>  optionalPrivateKey = loadPrivateKey(privateKeyPath);

            if(optionalUserInfo.isEmpty() || optionalPublicKey.isEmpty()){
                continue;
            }



            JsonObject userInfo = optionalUserInfo.get();

            UserIdentity identity = createUserIdentity(userInfo, optionalPublicKey.get(), optionalPrivateKey.orElse(null));
            if (identity == null)
                continue;

            identities.add(identity);
        }

        return identities;
    }

    @Nullable
    private static UserIdentity createUserIdentity(JsonObject userInfo, PublicKey publicKey, @Nullable PrivateKey optionalPrivateKey) {
        if(userInfo.get("id") == null || userInfo.get("token") == null)
            return null;

        int id;
        String token;
        try{
            id = userInfo.get("id").getAsInt();
            token = userInfo.get("token").getAsString();
        } catch (JsonSyntaxException e){
            return null;
        }


        return new UserIdentity(
                id,
                token,
                publicKey,
                optionalPrivateKey
        );
    }

    private Optional<NodeInfo> createDevice(JsonObject userInfo){

        int replicaId;
        String host;
        int port;
        int pbftPort;
        String token;

        if(userInfo.get("port") == null || userInfo.get("address") == null ||
                userInfo.get("pbft_port") == null || userInfo.get("id") == null ||
            userInfo.get("token") == null)
            return Optional.empty();

        try{
            replicaId = userInfo.get("id").getAsInt();
            host = userInfo.get("host").getAsString();
            port = userInfo.get("port").getAsInt();
            pbftPort = userInfo.get("pbft_port").getAsInt();
            token = userInfo.get("token").getAsString();
        } catch (JsonSyntaxException e){
            return Optional.empty();
        }


        return Optional.of(new NodeInfo(
                replicaId, token, host, pbftPort, port
        ));

    }

    public List<NodeInfo> loadNodeInfo(){
        List<Path> userDirectories = getUserDirectories();
        List<NodeInfo> nodeInfoList = new ArrayList<>();

        for(Path p : userDirectories){
            Path userInfoPath = Path.of(p.toAbsolutePath() + "/user_info.json");
            Optional<JsonObject> optionalUserInfo = loadUserInfo(userInfoPath);

            if(optionalUserInfo.isPresent()){
                JsonObject userInfo = optionalUserInfo.get();
                Optional<NodeInfo> dev = createDevice(userInfo);
                dev.ifPresent(nodeInfoList::add);
            }

        }

        return nodeInfoList;
    };
}
