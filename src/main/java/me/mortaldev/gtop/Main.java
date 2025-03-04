package me.mortaldev.gtop;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import co.aikar.commands.PaperCommandManager;
import me.mortaldev.gtop.commands.GTopCommand;
import me.mortaldev.gtop.configs.MainConfig;
import me.mortaldev.gtop.listeners.OnGangCommand;
import me.mortaldev.gtop.listeners.OnGangCreate;
import me.mortaldev.gtop.listeners.OnGangDisband;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.menuapi.GUIListener;
import me.mortaldev.menuapi.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public final class Main extends JavaPlugin {

  private static final String LABEL = "GTop";
  static Main instance;
  static HashSet<String> dependencies = new HashSet<>() {{
    add("GangsPlus");
    add("Skript");
  }};
  static PaperCommandManager commandManager;
  static GUIManager guiManager;
  static HashMap<String, Integer> tasks = new HashMap<>();
  private static MainConfig mainConfig;

  public static Main getInstance() {
    return instance;
  }

  public static String getLabel() {
    return LABEL;
  }

  public static GUIManager getGuiManager() {
    return guiManager;
  }

  public static MainConfig getMainConfig() {
    return mainConfig;
  }

  public static void log(String message) {
    Bukkit.getLogger().info("[" + Main.getLabel() + "] " + message);
  }

  @Override
  public void onEnable() {
    instance = this;
    commandManager = new PaperCommandManager(this);

    // DATA FOLDER

    if (!getDataFolder().exists()) {
      getDataFolder().mkdir();
    }

    // DEPENDENCIES

    for (String plugin : dependencies) {
      if (Bukkit.getPluginManager().getPlugin(plugin) == null) {
        getLogger().warning("Could not find " + plugin + "! This plugin is required.");
        Bukkit.getPluginManager().disablePlugin(this);
        return;
      }
    }

    // CONFIGS
    mainConfig = new MainConfig();

    // Managers (Loading data)
    GangManager.getInstance().load();
    GangManager.getInstance().getSet().forEach(GangManager.getInstance()::filterGangData);

    // GUI Manager
    guiManager = new GUIManager();
    GUIListener guiListener = new GUIListener(guiManager);
    Bukkit.getPluginManager().registerEvents(guiListener, this);


    // Events

    getServer().getPluginManager().registerEvents(new OnGangCommand(), this);
    getServer().getPluginManager().registerEvents(new OnGangDisband(), this);
    getServer().getPluginManager().registerEvents(new OnGangCreate(), this);

    // COMMANDS

//    commandManager.registerCommand(new LoreCommand());
//    commandManager.registerCommand(new RenameCommand());
    commandManager.registerCommand(new GTopCommand());

    // Skript API

    SkriptAddon addon = Skript.registerAddon(this);
    try {
      addon.loadClasses("me.mortaldev.gtop.register", "expressions");
      addon.loadClasses("me.mortaldev.gtop.register", "types");
    } catch (IOException e) {
      e.printStackTrace();
    }

    setPeriodicSaves(true);
    getLogger().info(LABEL + " Enabled");
  }

  @Override
  public void onDisable() {
    if (isPeriodicallySaving()) {
      setPeriodicSaves(false);
    }
    GangManager.getInstance().saveAllGangData();
    getLogger().info(LABEL + " Disabled");
  }

  public boolean isPeriodicallySaving() {
    return tasks.containsKey("gangSaves");
  }

  public void setPeriodicSaves(boolean b) {
    if (b && !isPeriodicallySaving()) {
      if (mainConfig.getSaveInterval() <= 0) {
        return;
      }
      long saveInterval = (20L * 60L) * mainConfig.getSaveInterval();
      tasks.put("gangSaves", Bukkit.getScheduler().scheduleSyncRepeatingTask(this, GangManager.getInstance()::saveAllGangData, saveInterval, saveInterval));
    } else if (isPeriodicallySaving()) {
      Bukkit.getScheduler().cancelTask(tasks.remove("gangSaves"));
    }
  }

}