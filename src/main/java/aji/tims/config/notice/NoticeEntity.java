package aji.tims.config.notice;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class NoticeEntity {
    private final boolean isSend;
    private final LinkedList<NoticeLine> lines;

    public NoticeEntity(JsonObject jsonObject){
        if (jsonObject == null){
            isSend = false;
            lines = null;
        }else {
            isSend = true;
            LinkedList<NoticeLine> lines = new LinkedList<>();
            try {
                Map<Integer, NoticeLine> temp = new HashMap<>();
                int size = 0;
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    String key = entry.getKey();
                    if (!key.matches("\\d+")) throw new IllegalArgumentException("key must be number");
                    int index = Integer.parseInt(key);
                    NoticeLine noticeLine = new NoticeLine(entry.getValue().getAsString());
                    temp.put(index, noticeLine);
                    size++;
                }
                for (int i = 0; i < size; i++) {
                    NoticeLine noticeLine = temp.get(i);
                    if (noticeLine == null) throw new IllegalArgumentException("key must be continuous");
                    lines.add(noticeLine);
                }
            }catch (Exception e){
                throw new IllegalArgumentException(e);
            }
            this.lines = lines;
        }
    }

    public JsonObject toJson(){
        if (!isSend) return null;
        JsonObject object = new JsonObject();
        for (int i = 0; i < lines.size(); i++) {
            object.addProperty(String.valueOf(i), lines.get(i).getString());
        }
        return object;
    }

    public void send(ServerPlayerEntity player){
        if (!isSend) return;
        for (NoticeLine line : lines) {
            player.sendMessage(Text.of(line.parse(player)));
        }
    }
}
