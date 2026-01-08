package aji.tims.config.notice;

import aji.tims.config.Config;
import com.google.gson.JsonElement;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class NoticeConfig implements Config {
    public static final String NAME = "notice";

    private final List<Notice> notices = new ArrayList<>();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void read(JsonElement element){
        if (!element.isJsonArray()) {
            return;
        }
        for (JsonElement noticeElement : element.getAsJsonArray()) {
            if (noticeElement.isJsonObject()) {
                notices.add(new Notice(noticeElement.getAsJsonObject()));
            }
        }
    }

    @Override
    public void clean() {
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
