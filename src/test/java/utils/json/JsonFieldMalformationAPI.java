package utils.json;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonFieldMalformationAPI {

    public int flags = 0x3f;

    public List<JsonElement> getCustomMalformations() {
        return customMalformations;
    }

    private List<JsonElement> customMalformations = new ArrayList<>();


    public void setFlag(int flag){
        flags |= flag;
    }

    public void clearFlag(int flag) {
        flags &= ~flag;
    }

    public int getFlags(){
        return flags;
    }

    public void addCustomMalformation(JsonElement malformation){
        customMalformations.add(malformation);
    }

}
