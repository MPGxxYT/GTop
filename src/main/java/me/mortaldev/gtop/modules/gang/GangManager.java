package me.mortaldev.gtop.modules.gang;

import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.menus.GTopMenu;
import me.mortaldev.gtop.modules.Report;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.Bukkit;

import java.io.File;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GangManager {
  public static final String GANG_DATA_SAVED = "GangData Saved. ({0})";
  public static final String GANG_DATA_LOADED = "GangData Loaded. ({0})";
  private static final HashSet<GangData> updatedGangData = new HashSet<>();
  private static HashSet<GangData> gangDataList;

  // Returns the EST timezone date.
  public static LocalDate todayDate() {
    return ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDate();
  }

  public static HashSet<LocalDate> todayWeek() {
    LocalDate today = todayDate();
    LocalDate begin = today.with(TemporalAdjusters.previousOrSame(Main.getMainConfig().getWeekBegin()));
    LocalDate end = today.with(TemporalAdjusters.nextOrSame(Main.getMainConfig().getWeekEnd()));
    return Stream.iterate(begin, date -> date.plusDays(1))
        .limit((end.toEpochDay() - begin.toEpochDay()) + 1)
        .collect(Collectors.toCollection(HashSet::new));
  }

  public static HashSet<LocalDate> todayMonth() {
    LocalDate today = todayDate();
    LocalDate startOfMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
    LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

    return Stream.iterate(startOfMonth, date -> date.plusDays(1))
        .limit((endOfMonth.toEpochDay() - startOfMonth.toEpochDay()) + 1)
        .collect(Collectors.toCollection(HashSet::new));
  }

  public static void addToUpdatedSet(GangData data) {
    updatedGangData.add(data);
  }

  public static void saveGangDataList() {
    String message;
    if (updatedGangData.isEmpty()) {
      message = MessageFormat.format(GANG_DATA_SAVED, 0);
    } else {
      for (GangData gangData : updatedGangData) {
        GangDataCRUD.saveGangData(gangData);
      }
      message = MessageFormat.format(GANG_DATA_SAVED, updatedGangData.size());
      updatedGangData.clear();
    }
    Main.log(message);
  }

  public static void loadGangDataList() {
    gangDataList = new HashSet<>();
    File mainPath = new File(GangDataCRUD.getPATH());
    if (!mainPath.exists()) {
      mainPath.mkdirs();
    }
    File[] files = mainPath.listFiles();
    List<String> gangNameList = new ArrayList<>();
    if (files != null) {
      for (File file : files) {
        String name = file.getName().replace(".json", "");
        GangData gangData = GangDataCRUD.getGangData(name);
        if (gangData.getDateBlockCountMap().size() > 42) {
          filterGangData(gangData);
        }
        gangDataList.add(gangData);
        gangNameList.add(name);
      }
    }
    if (!(gangDataList.size() == GangsPlusApi.getAllGangs().size())) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> updateToGangList(gangNameList), 60);
    }
    String message = MessageFormat.format(GANG_DATA_LOADED, gangDataList.size());
    Main.log(message);
  }

  public static void makeReport() {
    LinkedHashMap<GangData, Long> topMonthly = new GTopMenu(1, GTopMenu.ViewType.MONTHLY).getTopMonthly();
    int reportCount = Main.getMainConfig().getReportCount();
    Report report = new Report();
    Iterator<Map.Entry<GangData, Long>> iterator = topMonthly.entrySet().iterator();
    for (int i = 0; i < reportCount; i++) {
      if (!iterator.hasNext()) {
        break;
      }
      Map.Entry<GangData, Long> next = iterator.next();
      report.addData(next.getKey().getGangName(), next.getValue());
    }
    report.saveReport();
  }

  private static void updateToGangList(List<String> gangNameList) {
    for (Gang gang : GangsPlusApi.getAllGangs()) {
      if (!gangNameList.contains(gang.getName())) {
        GangData gangData = new GangData(gang.getName());
        addGangData(gangData);
      }
    }
  }

  // Store up to 42 dates, first in first out removal if over
  public static void filterGangData(GangData gangData) {
    Queue<Map.Entry<LocalDate, Long>> dateBlockCountQueue = new LinkedList<>(gangData.getDateBlockCountMap().entrySet());
    while (dateBlockCountQueue.size() > 42) {
      dateBlockCountQueue.poll();
    }

    LinkedHashMap<LocalDate, Long> dateBlockCountMap = new LinkedHashMap<>();
    dateBlockCountQueue.forEach(e -> dateBlockCountMap.put(e.getKey(), e.getValue()));
    gangData.setDateBlockCountMap(dateBlockCountMap);
    GangDataCRUD.saveGangData(gangData);
  }

  public static void updateGangDataList() {
    loadGangDataList();
  }

  public static HashSet<GangData> getGangDataList() {
    return gangDataList;
  }

  public static GangData getGangData(Gang gang) {
    return getGangData(gang.getName());
  }

  public static GangData getGangData(String gangName) {
    for (GangData gangData : gangDataList) {
      if (gangData.getGangName().equalsIgnoreCase(gangName)) {
        return gangData;
      }
    }
    Bukkit.getLogger().warning("Failed to get gangData of: " + gangName);
    return null;
  }

  public static void updateGangData(GangData gangData) {
    GangData oldData = getGangData(gangData.getGangName());
    gangDataList.remove(oldData);
    gangDataList.add(gangData);
  }

  public static void addGangData(GangData gangData) {
    gangDataList.add(gangData);
    GangDataCRUD.saveGangData(gangData);
  }

  public static void addGangData(Gang gang) {
    GangData gangData = getGangData(gang);
    if (gangData == null) {
      return;
    }
    addGangData(gangData);
  }

  public static void deleteGang(GangData gangData) {
    gangDataList.remove(gangData);
    GangDataCRUD.deleteGangData(gangData.getGangName());
  }

  public static void deleteGang(Gang gang) {
    GangData gangData = getGangData(gang);
    if (gangData == null) {
      return;
    }
    deleteGang(gangData);
  }
}
