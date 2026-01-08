package aji.tims.config.favicon;

import aji.tims.ThisIsMyServer;
import aji.tims.config.Config;
import aji.tims.config.ConfigFileFormatWrongException;
import aji.tims.config.motd.Motd;
import aji.tims.mixin.AccessorMinecraftServer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.server.ServerMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FaviconConfig implements Config {
    public static final String NAME = "favicon";

    private boolean enabled = false;
    private List<Favicon> favicons = new ArrayList<>();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public JsonElement toJson() {
        if (!enabled) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (Favicon favicon : favicons) {
            array.add(favicon.toJson());
        }
        return array;
    }

    @Override
    public void read(JsonElement element) throws ConfigFileFormatWrongException {
        if (!element.isJsonArray()) {
            enabled = false;
            return;
        }
        for (JsonElement faviconElement : element.getAsJsonArray()) {
            if (faviconElement.isJsonObject()) {
                favicons.add(new Favicon(faviconElement.getAsJsonObject()));
            }
        }
        enabled = true;
    }

    @Override
    public void clean() {
        enabled = false;
        favicons.clear();
    }

    public ServerMetadata.Favicon randomFavicon(){
        if (favicons.isEmpty() || !enabled) return ((AccessorMinecraftServer) ThisIsMyServer.server).getFavicon();
        int totalWeight = favicons.stream().mapToInt(Favicon::getWeight).sum();
        int random = new Random().nextInt(totalWeight);
        int currentWeight = 0;
        for (Favicon favicon : favicons) {
            currentWeight += favicon.getWeight();
            if (random < currentWeight) {
                return favicons.getFirst().getFavicon();
            }
        }
        return favicons.get(new Random().nextInt(favicons.size())).getFavicon();
    }
}
