package aji.tims.config.motd;

import com.google.gson.JsonObject;

public class Motd {
    private final int weight;
    private final MotdLine line0;
    private final MotdLine line1;

    public Motd(JsonObject object) throws IllegalArgumentException {
        int v = object.get("weight").getAsInt();
        if (v >= 1 && v <= 10) weight = v;
        else weight = 5;
        JsonObject info = object.get("info").getAsJsonObject();
        line0 = new MotdLine(info.get("0") == null || info.get("0").isJsonNull()? "" : info.get("0").getAsString());
        line1 = new MotdLine(info.get("1") == null || info.get("1").isJsonNull()? "" : info.get("1").getAsString());
    }

    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("weight", weight);
        JsonObject info = new JsonObject();
        info.addProperty("0", line0.getString());
        info.addProperty("1", line1.getString());
        return jsonObject;
    }

    public int getWeight(){
        return weight;
    }

    public String parse(){
        return line0.parse() + "\n" + line1.parse();
    }
}
