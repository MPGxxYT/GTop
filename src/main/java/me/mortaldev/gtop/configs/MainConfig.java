package me.mortaldev.gtop.configs;

import java.time.DayOfWeek;
import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.modules.config.AbstractConfig;
import me.mortaldev.gtop.modules.config.YamlConfig;
import me.mortaldev.gtop.modules.gang.GangManager;
import org.bukkit.configuration.file.FileConfiguration;

public class MainConfig extends AbstractConfig {
  private int saveInterval;
  private int reportCount;
  private int dataSavingLength; // in months
  private DayOfWeek weekBegin;
  private DayOfWeek weekEnd;

  public MainConfig() {
    super("config");
  }

  @Override
  public String reload() {
    String reload = super.reload();
    Main.getInstance().setPeriodicSaves(false);
    Main.getInstance().setPeriodicSaves(true);
    GangManager.getInstance().saveAllGangData();
    return reload;
  }

  @Override
  protected void loadInitialConfig() {
    super.loadInitialConfig();
    saveInterval = getConfig().getInt("saveInterval");
    if (saveInterval < 0) {
      setSaveInterval(0);
    }
    reportCount = getConfig().getInt("reportCount");
    if (reportCount < 1) {
      setReportCount(3);
    }
    dataSavingLength = getConfig().getInt("dataSavingLength");
    if (dataSavingLength < 1) {
      setDataSavingLength(3);
    }
    weekBegin = loadDayOfWeekValue("weekBegin", DayOfWeek.SUNDAY);
    weekEnd = loadDayOfWeekValue("weekEnd", DayOfWeek.SATURDAY);
  }

  private DayOfWeek loadDayOfWeekValue(String key, DayOfWeek defaultValue) {
    String weekdayString = getConfig().getString(key);
    if (weekdayString == null || weekdayString.isBlank()) {
      YamlConfig.failedToLoad(getName(), key);
      return defaultValue;
    }
    try {
      return DayOfWeek.valueOf(weekdayString.toUpperCase());
    } catch (IllegalArgumentException e) {
      YamlConfig.failedToLoad(getName(), key);
      return defaultValue;
    }
  }

  @Override
  public FileConfiguration getConfig() {
    return super.getConfig();
  }

  public int getSaveInterval() {
    return saveInterval;
  }

  public void setSaveInterval(int saveInterval) {
    this.saveInterval = saveInterval;
    setValue("saveInterval", saveInterval);
  }

  public int getDataSavingLength() {
    return dataSavingLength;
  }

  public void setDataSavingLength(int dataSavingLength) {
    this.dataSavingLength = dataSavingLength;
    setValue("dataSavingLength", dataSavingLength);
  }

  public DayOfWeek getWeekBegin() {
    return weekBegin;
  }

  public void setWeekBegin(DayOfWeek weekBegin) {
    this.weekBegin = weekBegin;
    setValue("weekBegin", weekBegin.toString());
  }

  public DayOfWeek getWeekEnd() {
    return weekEnd;
  }

  public void setWeekEnd(DayOfWeek weekEnd) {
    this.weekEnd = weekEnd;
    setValue("weekEnd", weekEnd.toString());
  }

  public int getReportCount() {
    return reportCount;
  }

  public void setReportCount(int reportCount) {
    this.reportCount = reportCount;
    setValue("reportCount", reportCount);
  }
}
