package me.mortaldev.gtop.modules.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Objects;
import me.mortaldev.gtop.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlConfig {

  public static final String FAILED_TO_LOAD_CONFIG =
      "[{0}.YML] Failed to load config value: {1} ({2})";
  public static final String INVALID_VALUE = "INVALID VALUE";
  public static final String OTHER_CONFIG_ERROR = "Error finding other config: ";
  public static final String RESOURCE_LOAD_ERROR = "Failed to load resource: ";
  private static final Main MAIN = Main.getInstance();

  public static String failedToLoad(String configName, String configValue) {
    return failedToLoad(configName, configValue, INVALID_VALUE);
  }

  public static String failedToLoad(String configName, String configValue, String failReason) {
    String message =
        MessageFormat.format(FAILED_TO_LOAD_CONFIG, configName, configValue, failReason);
    MAIN.getLogger().warning(message);
    YamlConfig.loadResource(configName);
    return message;
  }

  public static FileConfiguration createNewConfig(String name) {
    if (!name.contains(".yml")) {
      name = name.concat(".yml");
    }
    File file = new File(MAIN.getDataFolder(), name);
    if (file.exists()) {
      return getConfig(name);
    }
    try {
      file.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return YamlConfiguration.loadConfiguration(file);
  }

  public static FileConfiguration getConfig(String name) {
    if (!name.contains(".yml")) {
      name = name.concat(".yml");
    }
    File file = new File(MAIN.getDataFolder(), name);
    if (!file.exists()) {
      loadResource(name);
    }
    return YamlConfiguration.loadConfiguration(file);
  }

  public static FileConfiguration getOtherConfig(File file) {
    if (!file.exists()) {
      MAIN.getLogger().warning(OTHER_CONFIG_ERROR + file);
    }
    return YamlConfiguration.loadConfiguration(file);
  }

  public static void saveOtherConfig(File file, FileConfiguration config) {
    try {
      config.save(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveConfig(FileConfiguration config, String name) {
    if (!name.contains(".yml")) {
      name = name.concat(".yml");
    }
    File file = new File(MAIN.getDataFolder(), name);
    try {
      config.save(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void loadResource(String name) {
    if (!name.contains(".yml")) {
      name = name.concat(".yml");
    }
    InputStream stream = MAIN.getResource(name);
    if (stream == null) {
      MAIN.getLogger().warning(RESOURCE_LOAD_ERROR + name);
      return;
    }
    File file = new File(MAIN.getDataFolder(), name);
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      OutputStream outputStream = new FileOutputStream(file);
      outputStream.write(stream.readAllBytes());
      outputStream.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveDefaultConfig(FileConfiguration config) {
    MAIN.saveResource(config.getName(), false);
  }

  public static void reloadConfig(FileConfiguration config) {
    config = YamlConfiguration.loadConfiguration(new File(config.getCurrentPath()));
    Reader stream =
        new InputStreamReader(
            Objects.requireNonNull(MAIN.getResource(config.getName())), StandardCharsets.UTF_8);
    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(stream);
    config.setDefaults(defConfig);
  }
}
