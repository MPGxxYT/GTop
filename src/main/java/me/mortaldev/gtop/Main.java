package me.mortaldev.gtop;

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

public final class Main extends JavaPlugin {

    static Main instance;
    static PaperCommandManager commandManager;
    static GUIManager guiManager;
    private static final String LABEL = "GTop";

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

        if (!getDataFolder().exists()){
            getDataFolder().mkdir();
        }

        // DEPENDENCIES

        if (Bukkit.getPluginManager().getPlugin("GangsPlus") == null){
            getLogger().warning("Could not find GangsPlus! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("Skript") == null){
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

        getLogger().info(LABEL + " Enabled");

    }

    @Override
    public void onDisable() {
        getLogger().info(LABEL + " Disabled");
    }
}