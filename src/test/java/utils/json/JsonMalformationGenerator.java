package utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

public class JsonMalformationGenerator {
    private final JsonObject source;
    private final Map<String, JsonFieldMalformationAPI> rules;

    public JsonMalformationGenerator(JsonObject source, Map<String, JsonFieldMalformationAPI> rules) {
        this.source = source;
        this.rules = rules;
    }


    public static class MalformationContext {
        public final JsonObject MalformedJson;
        public final String Context;

        public MalformationContext(JsonObject malformedJson, String context) {
            MalformedJson = malformedJson;
            Context = context;
        }
    }

    private void appendMalformation(String key, JsonFieldMalformationAPI keyRules, List<MalformationContext> resultCollector){

        int flags = keyRules.getFlags();
        JsonObject malformed;

        if((flags & JsonMalformationRulesFlags.TestNull) != 0){
            malformed = source.deepCopy();
            malformed.add(key, null);
            resultCollector.add(new MalformationContext(malformed, "unexpected null"));
        }

        if((flags & JsonMalformationRulesFlags.TestRemove) != 0){
            malformed = source.deepCopy();
            malformed.remove(key);
            resultCollector.add(new MalformationContext(malformed, "missing property"));
        }

        if((flags & JsonMalformationRulesFlags.TestEmptyArray) != 0){
            malformed = source.deepCopy();
            malformed.add(key, new JsonArray());
            resultCollector.add(new MalformationContext(malformed, "unexpected empty array"));
        }

        if((flags & JsonMalformationRulesFlags.TestInteger) != 0){
            malformed = source.deepCopy();
            malformed.addProperty(key, 7);
            resultCollector.add(new MalformationContext(malformed, "unexpected integer"));
        }

        if((flags & JsonMalformationRulesFlags.TestString) != 0){
            malformed = source.deepCopy();
            malformed.addProperty(key, "Test");
            resultCollector.add(new MalformationContext(malformed, "unexpected string"));
        }

        if((flags & JsonMalformationRulesFlags.TestObject) != 0){
            malformed = source.deepCopy();
            malformed.add(key, new JsonObject());
            resultCollector.add(new MalformationContext(malformed, "unexpected object"));
        }

        resultCollector.addAll(keyRules.getCustomMalformations().stream().map(m -> new MalformationContext(m.getAsJsonObject(), "Custom malformation")).collect(Collectors.toList()));
    }

    public Collection<MalformationContext> generateMalformations(){
        List<MalformationContext> result = new LinkedList<>();
        for (var entry: rules.entrySet()){
            String key = entry.getKey();
            JsonFieldMalformationAPI keyRules = entry.getValue();
            appendMalformation(key, keyRules, result);
        }
        return result;
    }

}
