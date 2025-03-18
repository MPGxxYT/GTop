package me.mortaldev.gtop.modules.config;

import javax.annotation.Nullable;
import me.mortaldev.gtop.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractConfig {
  private static final String EXTENSION = ".yml";
  private final String name;
  private FileConfiguration config;

  public AbstractConfig(String configName) {
    this.name = configName;
    loadInitialConfig();
    Main.log("Loaded" + " " + name + EXTENSION);
  }

  public FileConfiguration getConfig() {
    return config;
  }

  public String getName() {
    return name;
  }

  /**
   * Reloads the configuration by loading the initial configuration and returns a reload message.
   *
   * @return the reload message
   */
  public String reload() {
    loadInitialConfig();
    return "Reloaded " + getName() + EXTENSION;
  }

  /**
   * Sets a value in the configuration at the given path.
   *
   * @param path the path of the value to set
   * @param value the value to set at the given path
   */
  public void setValue(@NotNull String path, @Nullable Object value) {
    getConfig().set(path, value);
    YamlConfig.saveConfig(getConfig(), getName());
  }

  private void loadConfig(String configName) {
    this.config = YamlConfig.getConfig(configName);
  }

  protected void loadInitialConfig() {
    loadConfig(getName());
  }
}
