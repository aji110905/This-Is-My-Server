package aji.tims.config.notice;

import aji.tims.ThisIsMyServer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class Notice {
    private final boolean alsoNotifyEntrant;
    private final String priority;
    private final int weight;
    private NoticeEntity entrant;
    private NoticeEntity others;

    public Notice(JsonObject object){
        JsonElement alsoNotifyEntrantElement = object.get("alsoNotifyEntrant");
        if (alsoNotifyEntrantElement != null && alsoNotifyEntrantElement.isJsonPrimitive() && alsoNotifyEntrantElement.getAsJsonPrimitive().isBoolean()) {
            alsoNotifyEntrant = alsoNotifyEntrantElement.getAsBoolean();
        } else {
            alsoNotifyEntrant = false;
        }
        JsonElement priorityElement = object.get("priority");
        if (priorityElement != null && priorityElement.isJsonPrimitive() && priorityElement.getAsJsonPrimitive().isString() && Objects.equals(priorityElement.getAsString(), "entrant")) {
            priority = priorityElement.getAsString();
        } else {
            priority = "others";
        }
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
            entrant = new NoticeEntity(null);
            others = new NoticeEntity(null);
        } else {
            JsonObject infoObject = infoElement.getAsJsonObject();
            JsonElement entrantElement = infoObject.get("entrant");
            if (entrantElement != null && entrantElement.isJsonObject()) {
                try {
                    entrant = new NoticeEntity(entrantElement.getAsJsonObject());
                } catch (Exception e) {
                    entrant = new NoticeEntity(null);
                }
            } else {
                entrant = new NoticeEntity(null);
            }
            JsonElement othersElement = infoObject.get("others");
            if (othersElement != null && othersElement.isJsonObject()) {
                try {
                    others = new NoticeEntity(othersElement.getAsJsonObject());
                } catch (Exception e) {
                    others = new NoticeEntity(null);
                }
            } else {
                others = new NoticeEntity(null);
            }
        }
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
