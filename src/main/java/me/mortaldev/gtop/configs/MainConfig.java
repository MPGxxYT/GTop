package me.mortaldev.gtop.configs;

import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.modules.config.AbstractConfig;
import me.mortaldev.gtop.modules.config.YamlConfig;
import me.mortaldev.gtop.modules.gang.GangManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.DayOfWeek;

public class MainConfig extends AbstractConfig {
  private int saveInterval;
  private int reportCount;
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
    GangManager.saveGangDataList();
    return reload;
  }

  @Override
  protected void loadInitialConfig() {
    super.loadInitialConfig();
    saveInterval = getConfig().getInt("saveInterval");
    reportCount = getConfig().getInt("reportCount");
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