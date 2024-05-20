package me.mortaldev.gtop.modules.gang;

import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.utils.GSON;
import org.bukkit.Bukkit;

import java.io.File;

public class GangCRUD {
  private static final String PATH = Main.getInstance().getDataFolder() + "/gangData/";

  public static String getPATH() {
    return PATH;
  }

  public static void saveGangData(GangData gangData){
    File filePath = new File(PATH + gangData.getGangName() + ".json");
    GSON.saveJsonObject(filePath, gangData);
  }
  public static GangData getGangData(String gangName) {
    File filePath = new File(PATH + gangName + ".json");
    if (filePath.exists()) {
      return GSON.getJsonObject(filePath, GangData.class);
    } else {
      throw new IllegalArgumentException("Could not get GangData: '" + gangName + "' does not exist.");
    }
  }
  public static void deleteGangData(String gangName) {
    File filePath = new File(PATH + gangName + ".json");
    if (filePath.exists()) {
      filePath.delete();
      Bukkit.getLogger().info("GangData '" + gangName + "' has been deleted.");
    } else {
      throw new IllegalArgumentException("Could not delete GangData: '" + gangName + "' does not exist.");
    }
  }
}
