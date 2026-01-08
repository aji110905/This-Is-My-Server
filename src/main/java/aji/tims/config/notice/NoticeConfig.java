package aji.tims.config.notice;

import aji.tims.config.Config;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class NoticeConfig implements Config {
    public static final String NAME = "notice";

    private final List<Notice> notices = new ArrayList<>();
    private boolean enabled = false;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public JsonElement toJson(){
        if (!enabled) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (Notice notice : notices) {
            array.add(notice.toJson());
        }
        return array;
    }

    @Override
    public void read(JsonElement element){
        if (!element.isJsonArray()) {
            enabled = false;
        }
        for (JsonElement noticeElement : element.getAsJsonArray()) {
            if (noticeElement.isJsonObject()) {
                notices.add(new Notice(noticeElement.getAsJsonObject()));
            }
        }
        enabled = true;
    }

    @Override
    public void clean() {
        enabled = false;
        notices.clear();
    }

    public void send(ServerPlayerEntity player){
        if (notices.isEmpty()) return;
        int totalWeight = notices.stream().mapToInt(Notice::getWeight).sum();
        int random = new Random().nextInt(totalWeight);
        int currentWeight = 0;
        for (Notice notice : notices) {
            currentWeight += notice.getWeight();
            if (random < currentWeight) {
                notice.send(player);
                return;
            }
        }
        notices.get(new Random().nextInt(notices.size())).send(player);
    }
}
