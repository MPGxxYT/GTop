package me.mortaldev.gtop.modules;

import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.utils.GSON;

import java.io.File;
import java.util.LinkedHashMap;

public class Report {
  private static final String PATH = Main.getInstance().getDataFolder() + "/reports/";
  LinkedHashMap<String, Long> topGangs = new LinkedHashMap<>();

  public Report() {}

  public void addData(String name, Long count){
    topGangs.put(name, count);
  }

  public void saveReport(){
    File filePath = new File(PATH + generateReportName() + ".json");
    GSON.saveJsonObject(filePath, this);
  }

  private String generateReportName(){
    String month = GangManager.todayDate().getMonth().toString();
    File mainPath = new File(PATH);
    if (!mainPath.exists()) {
      mainPath.mkdirs();
    }
    File[] files = mainPath.listFiles();
    String reportName = month;
    if (files != null) {
      reportName = reportName + "_" + files.length;
    }
    return reportName;
  }
}
