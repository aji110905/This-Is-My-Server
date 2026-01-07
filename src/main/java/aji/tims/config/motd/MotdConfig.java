package aji.tims.config.motd;

import aji.tims.ThisIsMyServer;
import aji.tims.config.Config;
import aji.tims.config.ConfigFileFormatWrongException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MotdConfig implements Config {
    public static final String NAME = "motd";

    private boolean enabled = false;
    private final List<Motd> motds= new ArrayList<>();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public JsonElement toJson() {
        if (!enabled) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (Motd motd : motds) {
            array.add(motd.toJson());
        }
        return array;
    }

    @Override
    public void read(JsonElement element) throws ConfigFileFormatWrongException {
        if (element.isJsonNull()) {
            enabled = false;
            return;
        }
        if (!element.isJsonArray()) {
            enabled = false;
            throw new ConfigFileFormatWrongException(this, "Mast Array`");
        }
        for (JsonElement motdElement : element.getAsJsonArray()) {
            try {
                motds.add(new Motd(motdElement.getAsJsonObject()));
            } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
                enabled = false;
                throw new ConfigFileFormatWrongException(this, e.getMessage());
            }
        }
        enabled = true;
    }

    @Override
    public void clean() {
        enabled = false;
        motds.clear();
    }

    public String randomMotd(){
        if (motds.isEmpty() || !enabled) return ThisIsMyServer.server.getServerMotd();
        int totalWeight = motds.stream().mapToInt(Motd::getWeight).sum();
        int random = new Random().nextInt(totalWeight);
        int currentWeight = 0;
        for (Motd motd : motds) {
            currentWeight += motd.getWeight();
            if (random < currentWeight) {
                return motd.parse();
            }
        }
        return motds.get(new Random().nextInt(motds.size())).parse();
    }
}
