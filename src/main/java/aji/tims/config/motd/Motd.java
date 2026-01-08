package aji.tims.config.motd;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Motd {
    private final int weight;
    private final MotdLine line0;
    private final MotdLine line1;

    public Motd(JsonObject object){
        JsonElement weightElement = object.get("weight");
        if (weightElement == null || !weightElement.isJsonPrimitive() || !weightElement.getAsJsonPrimitive().isNumber() || !(weightElement.getAsJsonPrimitive().getAsNumber() instanceof Integer)) {
            weight = 5;
        } else {
            int v = weightElement.getAsInt();
            if (v >= 1 && v <= 10) {
                weight = v;
            } else {
                weight = 5;
            }
        }
        JsonElement infoElement = object.get("info");
        if (infoElement == null || !infoElement.isJsonObject()) {
            line0 = new MotdLine("");
            line1 = new MotdLine("");
        } else {
            JsonObject infoObject = infoElement.getAsJsonObject();
            JsonElement line0Element = infoObject.get("0");
            if (line0Element == null || !line0Element.isJsonPrimitive() || !line0Element.getAsJsonPrimitive().isString()) {
                line0 = new MotdLine("");
            } else {
                line0 = new MotdLine(line0Element.getAsString());
            }
            JsonElement line1Element = infoObject.get("1");
            if (line1Element == null || !line1Element.isJsonPrimitive() || !line1Element.getAsJsonPrimitive().isString()) {
                line1 = new MotdLine("");
            } else {
                line1 = new MotdLine(line1Element.getAsString());
            }
        }
    }

    public int getWeight(){
        return weight;
    }

    public String parse(){
        return line0.parse() + "\n" + line1.parse();
    }
}
