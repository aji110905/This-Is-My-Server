package aji.tims.config.notice;

import aji.tims.ThisIsMyServer;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;

public class Notice {
    private final boolean alsoNotifyEntrant;
    private final String priority;
    private final int weight;
    private final NoticeEntity entrant;
    private final NoticeEntity others;

    public Notice(JsonObject jsonObject) throws IllegalArgumentException{
        alsoNotifyEntrant = jsonObject.get("alsoNotifyEntrant").getAsBoolean();
        priority = jsonObject.get("priority").getAsString();
        int v = jsonObject.get("weight").getAsInt();
        if (v >= 1 && v <= 10) weight = v;
        else weight = 5;
        JsonObject info = jsonObject.getAsJsonObject("info");
        entrant = new NoticeEntity(info.get("entrant").isJsonNull() ? null : (info.getAsJsonObject("entrant")));
        others = new NoticeEntity(info.get("others").isJsonNull() ? null : (info.getAsJsonObject("others")));
    }

    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("alsoNotifyEntrant", alsoNotifyEntrant);
        jsonObject.addProperty("priority", priority);
        jsonObject.addProperty("weight", weight);
        JsonObject info = new JsonObject();
        info.add("entrant", entrant.toJson());
        info.add("others", others.toJson());
        jsonObject.add("info", info);
        return jsonObject;
    }

    public void send(ServerPlayerEntity player){
        if (priority.equals("others")){
            sendOthers(player);
            entrant.send(player);
        } else {
            entrant.send(player);
            sendOthers(player);
        }
    }

    private void sendOthers(ServerPlayerEntity player){
        for (ServerPlayerEntity playerEntity : ThisIsMyServer.server.getPlayerManager().getPlayerList()) {
            if (!playerEntity.getUuid().equals(player.getUuid()) || alsoNotifyEntrant) others.send(playerEntity);
        }
    }

    public int getWeight(){
        return weight;
    }
}
