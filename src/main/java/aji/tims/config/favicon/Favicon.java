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

    public Favicon(JsonObject object){
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
        JsonElement pathElement = object.get("path");
        if (pathElement == null || pathElement.isJsonNull()) {
            path = null;
            favicon = null;
        } else {
            path = pathElement.getAsString();
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

    public int getWeight(){
        return weight;
    }

    public ServerMetadata.Favicon getFavicon() {
        return isValid ? new ServerMetadata.Favicon(favicon) : null;
    }
}
