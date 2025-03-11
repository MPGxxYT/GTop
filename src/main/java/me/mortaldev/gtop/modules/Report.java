package me.mortaldev.gtop.modules;

import java.io.File;
import java.util.LinkedHashMap;
import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.utils.GSON;

public class Report {
  private static final String PATH = Main.getInstance().getDataFolder() + "/reports/";
  LinkedHashMap<String, Long> monthTop = new LinkedHashMap<>();
  LinkedHashMap<String, Long> weekTop = new LinkedHashMap<>();
  LinkedHashMap<String, Long> allTimeTop = new LinkedHashMap<>();

  public Report() {}

  public void addMonthData(String name, Long count) {
    monthTop.put(name, count);
  }

  public void addWeekData(String name, Long count) {
    weekTop.put(name, count);
  }

  public void addAllTimeData(String name, Long count) {
    allTimeTop.put(name, count);
  }

  public void saveReport() {
    File filePath = new File(PATH + generateReportName() + ".json");
    GSON.saveJsonObject(filePath, this);
  }

  private String generateReportName() {
    String month = GangManager.getInstance().todayDate().getMonth().toString();
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
