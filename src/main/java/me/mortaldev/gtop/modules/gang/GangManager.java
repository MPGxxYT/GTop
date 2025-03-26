package me.mortaldev.gtop.modules.gang;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.mortaldev.crudapi.CRUD;
import me.mortaldev.crudapi.CRUDManager;
import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.menus.GTopMenu;
import me.mortaldev.gtop.menus.PageData;
import me.mortaldev.gtop.modules.Report;
import me.mortaldev.gtop.utils.ItemStackHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class GangManager extends CRUDManager<GangData> {

  HashSet<GangData> updatedGangs = new HashSet<>();

  private GangManager() {}

  public static GangManager getInstance() {
    return Singleton.INSTANCE;
  }

  // Returns the EST timezone date.
  public LocalDate todayDate() {
    return ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDate();
  }

  /**
   * Gets the set of {@link LocalDate}s representing the week containing the current day as
   * specified by {@link #todayDate()}. The week is defined by the configuration keys {@code
   * weekBegin} and {@code weekEnd}.
   *
   * @return A set of {@link LocalDate}s representing the week containing the current day.
   */
  public HashSet<LocalDate> todayWeek() {
    LocalDate today = todayDate();
    LocalDate begin =
        today.with(TemporalAdjusters.previousOrSame(Main.getMainConfig().getWeekBegin()));
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
    LinkedHashMap<GangData, Long> topMonthly = new GTopMenu(new PageData()).getTopMonthly();
    LinkedHashMap<GangData, Long> topWeekly = new GTopMenu(new PageData()).getTopWeekly();
    LinkedHashMap<GangData, Long> topAllTime = new GTopMenu(new PageData()).getTopAllTime();
    int reportCount = Main.getMainConfig().getReportCount();
    Report report = new Report();
    Iterator<Map.Entry<GangData, Long>> monthlyIterator = topMonthly.entrySet().iterator();
    Iterator<Map.Entry<GangData, Long>> weeklyIterator = topWeekly.entrySet().iterator();
    Iterator<Map.Entry<GangData, Long>> allTimeIterator = topAllTime.entrySet().iterator();

    for (int i = 0; i < reportCount; i++) {
      if (monthlyIterator.hasNext()) {
        Map.Entry<GangData, Long> next = monthlyIterator.next();
        report.addMonthData(next.getKey().getGangName(), next.getValue());
      }
      if (weeklyIterator.hasNext()) {
        Map.Entry<GangData, Long> next = weeklyIterator.next();
        report.addWeekData(next.getKey().getGangName(), next.getValue());
      }
      if (allTimeIterator.hasNext()) {
        Map.Entry<GangData, Long> next = allTimeIterator.next();
        report.addAllTimeData(next.getKey().getGangName(), next.getValue());
      }
    }
    report.saveReport();
  }

  // Remove dates that aren't within the last {dataSavingLength} months
  public void filterGangData(GangData data) {
    int dataSavingLength = Main.getMainConfig().getDataSavingLength();
    int thisMonth = todayDate().getMonthValue();
    int thisYear = todayDate().getYear();
    int oldestMonth;
    int oldestYear;
    if (thisMonth - dataSavingLength < 1) {
      oldestMonth = 12 + (thisMonth - dataSavingLength);
      oldestYear = thisYear - 1;
    } else {
      oldestMonth = thisMonth - dataSavingLength;
      oldestYear = thisYear;
    }
    LinkedHashMap<LocalDate, MemberData> memberBlockCountMap = data.getMemberBlockCountMap();
    memberBlockCountMap
        .entrySet()
        .removeIf(
            entry ->
                entry.getKey().getMonthValue() <= oldestMonth
                    && entry.getKey().getYear() <= oldestYear);
    data.setMemberBlockCountMap(memberBlockCountMap);
    GangDataCRUD.getInstance().saveData(data);
  }

  @Override
  public synchronized boolean update(GangData data) {
    // To prevent lag, update files only when saveAllGangData() is called
    updatedGangs.add(data);
    return super.update(data, false);
  }

  public void saveAllGangData() {
    // Only updates files that have been changed
    updatedGangs.forEach(GangDataCRUD.getInstance()::saveData);
    updatedGangs.clear();
  }

  public ItemStack getHead(UUID uuid, Long count) {
    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
    if (uuid.toString().equals(MemberData.getDefaultUuid().toString())) {
      return ItemStackHelper.builder(skull)
              .name("&e&l???")
              .addLore("&f - " + String.format("%,d", count) + " Blocks Mined")
              .build();
    }
    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
    skullMeta.setOwningPlayer(offlinePlayer);
    skull.setItemMeta(skullMeta);
    String name = offlinePlayer.getName();
    if (name == null) {
      name = "???";
    }
    return ItemStackHelper.builder(skull)
            .name("&e&l" + name)
            .addLore("&f - " + String.format("%,d", count) + " Blocks Mined")
            .build();
  }

  @Override
  public void load() {
    super.load();
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
