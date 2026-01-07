package aji.tims.config.favicon;

import aji.tims.ThisIsMyServer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.ServerMetadata;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Favicon {
    private final int weight;
    private final String path;
    private boolean isValid = false;
    private byte[] favicon;

    public Favicon(JsonObject object) throws IllegalArgumentException{
        int v = object.get("weight").getAsInt();
        if (v >= 1 && v <= 10) weight = v;
        else weight = 5;
        JsonElement element = object.get("path");
        if (element == null || element.isJsonNull()) {
            path = null;
            favicon = null;
        } else {
            path = element.getAsString();
            File file = Path.of(path).toFile();
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                isValid = bufferedImage.getWidth() == 64 && bufferedImage.getHeight() == 64;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
                favicon = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                isValid = true;
            } catch (IOException e) {
                favicon = null;
                isValid = false;
                ThisIsMyServer.LOGGER.error("Favicon cannot be loaded", e);
            }
        }
    }

    public JsonObject toJson(){
        JsonObject object = new JsonObject();
        object.addProperty("weight", weight);
        if (path != null) {
            object.addProperty("path", path);
        }
        return object;
    }

    public int getWeight(){
        return weight;
    }

    public ServerMetadata.Favicon getFavicon() {
        return isValid ? new ServerMetadata.Favicon(favicon) : null;
    }
}
