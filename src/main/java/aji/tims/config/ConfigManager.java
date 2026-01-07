package aji.tims.config;

import aji.tims.ThisIsMyServer;
import aji.tims.config.favicon.FaviconConfig;
import aji.tims.config.motd.MotdConfig;
import aji.tims.config.notice.NoticeConfig;
import aji.tims.util.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigManager {
    private static final Set<Class<? extends Config>> CONFIGS = Set.of(
            NoticeConfig.class,
            MotdConfig.class,
            FaviconConfig.class
    );

    private final Map<String, Config> configs = new HashMap<>();
    private final File file;

    public ConfigManager(File file){
        this.file = file;
        for (Class<? extends Config> config : CONFIGS) {
            try {
                Config instance = config.getDeclaredConstructor().newInstance();
                configs.put(instance.name(), instance);
            } catch (Exception e) {
                //我不可能出错
            }
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                ThisIsMyServer.LOGGER.error("Failed to create config file", e);
            }
            save();
        }
        load();
        ThisIsMyServer.LOGGER.info("Config inited");
    }

    private void load(){
        try {
            JsonElement element = JsonParser.parseReader(new FileReader(file));
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                configs.forEach((name, config) -> {
                    try {
                        JsonElement configElement = object.get(name);
                        if (configElement != null) {
                            config.read(configElement);
                        }
                    } catch (ConfigFileFormatWrongException e) {
                        ThisIsMyServer.LOGGER.error("The config file format is wrong{}", e.getMessage(), e);
                    }
                });
            }else {
                ThisIsMyServer.LOGGER.error("The config file format is wrong", new ConfigFileFormatWrongException("Must Object"));
            }
        } catch (FileNotFoundException e) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ThisIsMyServer.LOGGER.error("Failed to create config file", ex);
            }
            save();
            load();
            return;
        }
        ThisIsMyServer.LOGGER.info("Config loaded");
    }

    public void reload(){
        configs.forEach((name, config) -> config.clean());
        load();
    }

    public void save(){
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(file));
            writer.setIndent("  ");
            writer.beginObject();
            configs.forEach((name, config) -> {
                try {
                    writer.name(name);
                    JsonUtil.writeJson(writer, config.toJson());
                } catch (IOException e) {
                    ThisIsMyServer.LOGGER.error("Failed to save config file", e);
                }
            });
            writer.endObject();
            writer.flush();
        } catch (IOException e) {
            ThisIsMyServer.LOGGER.error("Failed to save config file", e);
        }
        ThisIsMyServer.LOGGER.info("Config saved");
    }

    public Config getConfig(String name){
        return configs.get(name);
    }
}
