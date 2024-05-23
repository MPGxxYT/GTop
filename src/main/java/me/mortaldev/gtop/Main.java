package me.mortaldev.gtop;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import co.aikar.commands.PaperCommandManager;
import me.mortaldev.gtop.commands.GTopCommand;
import me.mortaldev.gtop.commands.LoreCommand;
import me.mortaldev.gtop.commands.RenameCommand;
import me.mortaldev.gtop.listeners.OnGangCommand;
import me.mortaldev.gtop.listeners.OnGangCreate;
import me.mortaldev.gtop.listeners.OnGangDisband;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.modules.menu.GUIListener;
import me.mortaldev.gtop.modules.menu.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;

public final class Main extends JavaPlugin {

  private static final String LABEL = "GTop";
  static Main instance;
  static PaperCommandManager commandManager;
  static GUIManager guiManager;
  static HashMap<String, Integer> tasks = new HashMap<>();

  public static PaperCommandManager getCommandManager() {
    return commandManager;
  }

  public static Main getInstance() {
    return instance;
  }

  public static String getLabel() {
    return LABEL;
  }

  public static GUIManager getGuiManager() {
    return guiManager;
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

    if (Bukkit.getPluginManager().getPlugin("GangsPlus") == null) {
      getLogger().warning("Could not find GangsPlus! This plugin is required.");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }

    if (Bukkit.getPluginManager().getPlugin("Skript") == null) {
      getLogger().warning("Could not find Skript! This plugin is required.");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }

    // Managers (Loading data)
    GangManager.loadGangDataList();

    // GUIs
    guiManager = new GUIManager();

    GUIListener guiListener = new GUIListener(guiManager);
    Bukkit.getPluginManager().registerEvents(guiListener, this);

    // CONFIGS
//        WildConfig.loadConfig(true);
//        MainConfig.loadConfig(true);


    // Events

    getServer().getPluginManager().registerEvents(new OnGangCommand(), this);
    getServer().getPluginManager().registerEvents(new OnGangDisband(), this);
    getServer().getPluginManager().registerEvents(new OnGangCreate(), this);

    // COMMANDS

    commandManager.registerCommand(new LoreCommand());
    commandManager.registerCommand(new RenameCommand());
    commandManager.registerCommand(new GTopCommand());

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
    setPeriodicSaves(false);
    GangManager.saveGangDataList();
    getLogger().info(LABEL + " Disabled");
  }

  private void setPeriodicSaves(boolean b) {
    long timeBetweenSaves = 30L;
    if (b) {
      tasks.put("gangSaves", Bukkit.getScheduler().scheduleSyncRepeatingTask(this, GangManager::saveGangDataList, 0L, (20L * 60L) * timeBetweenSaves));
    } else {
      Bukkit.getScheduler().cancelTask(tasks.remove("gangSaves"));
    }
  }

}