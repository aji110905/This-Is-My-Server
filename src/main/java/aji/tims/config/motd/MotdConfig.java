package aji.tims.config.motd;

import aji.tims.ThisIsMyServer;
import aji.tims.config.Config;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MotdConfig implements Config {
    public static final String NAME = "motd";

    private final List<Motd> motds= new ArrayList<>();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void read(JsonElement element){
        if (!element.isJsonArray()) {
            return;
        }
        for (JsonElement motdElement : element.getAsJsonArray()) {
            if (motdElement.isJsonObject()) {
                motds.add(new Motd(motdElement.getAsJsonObject()));
            }
        }
    }

    @Override
    public void clean() {
        motds.clear();
    }

    public String randomMotd(){
        if (motds.isEmpty()) return ThisIsMyServer.server.getServerMotd();
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
