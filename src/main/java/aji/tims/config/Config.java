package aji.tims.config;

import com.google.gson.JsonElement;

public interface Config {
    String name();

    void read(JsonElement element);

    void clean();
}
