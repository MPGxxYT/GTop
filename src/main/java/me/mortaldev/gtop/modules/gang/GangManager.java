package me.mortaldev.gtop.modules.gang;

import me.mortaldev.crudapi.CRUD;
import me.mortaldev.crudapi.CRUDManager;
import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.menus.GTopMenu;
import me.mortaldev.gtop.modules.Report;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GangManager extends CRUDManager<GangData> {

  private GangManager() {
  }

  public static GangManager getInstance() {
    return Singleton.INSTANCE;
  }

  // Returns the EST timezone date.
  public LocalDate todayDate() {
    return ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDate();
  }

  /**
   * Gets the set of {@link LocalDate}s representing the week containing the current day as specified by
   * {@link #todayDate()}. The week is defined by the configuration keys {@code weekBegin} and {@code weekEnd}.
   *
   * @return A set of {@link LocalDate}s representing the week containing the current day.
   */
  public HashSet<LocalDate> todayWeek() {
    LocalDate today = todayDate();
    LocalDate begin = today.with(TemporalAdjusters.previousOrSame(Main.getMainConfig().getWeekBegin()));
    LocalDate end = today.with(TemporalAdjusters.nextOrSame(Main.getMainConfig().getWeekEnd()));
    return Stream.iterate(begin, date -> date.plusDays(1))
            .limit((end.toEpochDay() - begin.toEpochDay()) + 1)
            .collect(Collectors.toCollection(HashSet::new));
  }

  public HashSet<LocalDate> todayMonth() {
    LocalDate today = todayDate();
    LocalDate startOfMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
    LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

    return Stream.iterate(startOfMonth, date -> date.plusDays(1))
            .limit((endOfMonth.toEpochDay() - startOfMonth.toEpochDay()) + 1)
            .collect(Collectors.toCollection(HashSet::new));
  }

  public void makeReport() {
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

  // Store up to 42 dates, first in first out removal if over
  public void filterGangData(GangData gangData) {
    Queue<Map.Entry<LocalDate, Long>> dateBlockCountQueue = new LinkedList<>(gangData.getDateBlockCountMap().entrySet());
    while (dateBlockCountQueue.size() > 42) {
      dateBlockCountQueue.poll();
    }

    LinkedHashMap<LocalDate, Long> dateBlockCountMap = new LinkedHashMap<>();
    dateBlockCountQueue.forEach(e -> dateBlockCountMap.put(e.getKey(), e.getValue()));
    gangData.setDateBlockCountMap(dateBlockCountMap);
    GangDataCRUD.getInstance().saveData(gangData);
  }

  public void saveAllGangData() {
    getSet().forEach(GangDataCRUD.getInstance()::saveData);
  }

  @Override
  public CRUD<GangData> getCRUD() {
    return GangDataCRUD.getInstance();
  }

  @Override
  public void log(String string) {
    Main.log(string);
  }

  private static class Singleton {
    private static final GangManager INSTANCE = new GangManager();
  }
}
