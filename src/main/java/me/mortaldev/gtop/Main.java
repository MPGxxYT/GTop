package me.mortaldev.gtop;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import java.io.IOException;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import me.mortaldev.gtop.commands.GTopCommand;
import me.mortaldev.gtop.configs.MainConfig;
import me.mortaldev.gtop.listeners.OnGangCommand;
import me.mortaldev.gtop.listeners.OnGangCreate;
import me.mortaldev.gtop.listeners.OnGangDisband;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.utils.TimedRunnable;
import me.mortaldev.menuapi.GUIListener;
import me.mortaldev.menuapi.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

  private static final String LABEL = "GTop";
  static Main instance;
  static HashSet<String> dependencies =
      new HashSet<>() {
        {
          add("GangsPlus");
          add("Skript");
        }
      };
  static PaperCommandManager commandManager;
  static GUIManager guiManager;
  static HashMap<String, Integer> tasks = new HashMap<>();
  private static MainConfig mainConfig;
  private TimedRunnable reportTimer;

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

  public static void warn(String message) {
    Bukkit.getLogger().warning("[" + Main.getLabel() + "] " + message);
  }

  public static void severe(String message) {
    Bukkit.getLogger().severe("[" + Main.getLabel() + "] " + message);
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
    commandManager
        .getCommandContexts()
        .registerContext(
            GangData.class,
            c -> {
              String s = c.popFirstArg();
              Optional<GangData> gangDataOptional = GangManager.getInstance().getByID(s);
              if (gangDataOptional.isEmpty()) {
                throw new InvalidCommandArgument("Gang not found by that name.");
              }
              return gangDataOptional.get();
            });
    commandManager
        .getCommandCompletions()
        .registerCompletion(
            "gangs",
            c ->
                GangManager.getInstance().getSet().stream()
                    .map(GangData::getGangName)
                    .collect(Collectors.toSet()));
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
    reportTimer = new TimedRunnable(ZoneId.of("America/New_York"));
    startReportTimer(reportTimer);
    getLogger().info(LABEL + " Enabled");
  }

  private void startReportTimer(TimedRunnable reportTimer) {
    reportTimer.start(
        () -> {
          GangManager.getInstance().makeReport();
          log("Report automatically saved.");
        },
        (date) -> {
          int lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
          int currentDayOfMonth = date.getDayOfMonth();
          int currentMinute = date.getMinute();
          int currentHour = date.getHour();
          return currentDayOfMonth == lastDayOfMonth && currentMinute >= 57 && currentHour == 23;
        });
  }

  @Override
  public void onDisable() {
    reportTimer.stop();
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
      tasks.put(
          "gangSaves",
          Bukkit.getScheduler()
              .scheduleSyncRepeatingTask(
                  this, GangManager.getInstance()::saveAllGangData, saveInterval, saveInterval));
    } else if (isPeriodicallySaving()) {
      Bukkit.getScheduler().cancelTask(tasks.remove("gangSaves"));
    }
  }
}
