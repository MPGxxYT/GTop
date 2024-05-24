package me.mortaldev.gtop.modules.gang;

import me.mortaldev.gtop.Main;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.Bukkit;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GangManager {
  private static HashSet<GangData> gangDataList;

  // Returns the EST timezone date.
  public static LocalDate todayDate() {
    return ZonedDateTime.now(ZoneId.of("-05:00")).toLocalDate();
  }

  public static HashSet<LocalDate> todayWeek() {
    LocalDate today = todayDate();
    LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
    return Stream.iterate(monday, date -> date.plusDays(1))
        .limit((sunday.toEpochDay() - monday.toEpochDay()) + 1)
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
        gangDataList.add(gangData);
        gangNameList.add(name);
      }
    }
    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> updateToGangList(gangNameList), 60);
    Bukkit.getLogger().info("GangData Loaded. (" + gangDataList.size() + ")");
  }

  private static void updateToGangList(List<String> gangNameList) {
    if (!(gangDataList.size() == GangsPlusApi.getAllGangs().size())) {
      for (Gang gang : GangsPlusApi.getAllGangs()) {
        if (!gangNameList.contains(gang.getName())) {
          GangData gangData = new GangData(gang.getName());
          addGangData(gangData);
        }
      }
    }
  }

  // Store up to 45 dates, first in first out removal if over
  public static void filterGangList() {
    for (GangData gangData : gangDataList) {
      Queue<Map.Entry<LocalDate, Long>> dateBlockCountQueue = new LinkedList<>(gangData.getDateBlockCountMap().entrySet());
      while (dateBlockCountQueue.size() > 45) {
        dateBlockCountQueue.remove();
      }

      LinkedHashMap<LocalDate, Long> dateBlockCountMap = new LinkedHashMap<>();
      dateBlockCountQueue.forEach(e -> dateBlockCountMap.put(e.getKey(), e.getValue()));
      gangData.setDateBlockCountMap(dateBlockCountMap);
    }
  }


  public static void saveGangDataList() {
    for (GangData gangData : gangDataList) {
      GangDataCRUD.saveGangData(gangData);
    }
    Bukkit.getLogger().info("GangData Saved. (" + gangDataList.size() + ")");
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
