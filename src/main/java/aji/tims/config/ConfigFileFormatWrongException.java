package aji.tims.config;

public class ConfigFileFormatWrongException extends RuntimeException {
    public ConfigFileFormatWrongException(String msg) {
        super("The config file format is wrong: " + msg);
    }

    public ConfigFileFormatWrongException(Config config, String msg) {
        super("The config file format is wrong: " + config.name() + ". " + msg);
    }
}
