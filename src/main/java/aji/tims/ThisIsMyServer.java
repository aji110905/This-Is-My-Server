package aji.tims;

import aji.tims.config.ConfigManager;
import net.fabricmc.api.DedicatedServerModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ThisIsMyServer{
	public static final String MOD_ID = "tims";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ConfigManager configManager;
    public static MinecraftServer server;
    public static boolean initialized = false;

    public void onInitializeServer() {
        configManager = new ConfigManager(new File(FabricLoader.getInstance().getConfigDir().toFile(), MOD_ID + ".json"));
        initialized = true;
    }
}