package me.mortaldev.gtop.menus;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.utils.ItemStackHelper;
import me.mortaldev.gtop.utils.TextUtil;
import me.mortaldev.gtop.utils.Utils;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class GTopMenu extends InventoryGUI {

  LinkedHashMap<GangData, Long> topGangMap;
  int maxPage;
  int page;
  ViewType viewType;

  public GTopMenu(int page, ViewType viewType) {
    this.viewType = viewType;
    this.page = page;
    switch (viewType) {
      case ALL_TIME -> topGangMap = getTopAllTime();
      case MONTHLY -> topGangMap = getTopMonthly();
      case WEEKLY -> topGangMap = getTopWeekly();
    }
    if (topGangMap.isEmpty()) {
      this.maxPage = 1;
      this.page = 1;
    } else {
      this.maxPage = (int) Math.ceil((double) topGangMap.size() / 45);
      if (page > maxPage) {
        this.page = maxPage;
      }
    }
  }

  public int inventorySize() {
    int size = (int) Math.ceil((double) (topGangMap.size() - ((page - 1) * 45)) / 9);
    return Utils.clamp(size + 1, 3, 6);
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, inventorySize() * 9, TextUtil.format("GTop"));
  }

  @Override
  public void decorate(Player player) {
    // Buttons
    this.addButton(3, weeklyButton());
    this.addButton(4, monthlyButton());
    this.addButton(5, allTimeButton());
    ItemStack whiteGlass =
        ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE).name("").addLore("").build();
    int[] glassSlots = {0, 1, 2, 6, 7, 8};
    for (int i : glassSlots) {
      this.getInventory().setItem(i, whiteGlass);
    }
    if (page > 1) {
      this.addButton(0, backButton());
    }
    if (maxPage > 1 && maxPage != page) {
      this.addButton(8, nextButton());
    }

    // Display

    int skip = (page - 1) * 45;
    if (skip < 0) {
      skip = 0;
    }

    Iterator<Map.Entry<GangData, Long>> iterator =
        topGangMap.entrySet().stream().skip(skip).iterator();
    for (int i = 0; i < 45 && i < getInventory().getSize() - 9; i++) {
      if (!iterator.hasNext()) {
        break;
      }
      Map.Entry<GangData, Long> next = iterator.next();
      ItemStack bannerItem =
          ItemStackHelper.builder(next.getKey().getBanner())
              .name("&e#" + (i + 1 + ((page - 1) * 45)) + " &l" + next.getKey().getGangName())
              .lore(new ArrayList<>())
              .addLore("&7 - " + String.format("%,d", next.getValue()) + " Blocks Mined")
              .build();
      bannerItem.editMeta(itemMeta -> itemMeta.addItemFlags(ItemFlag.values()));
      bannerItem.editMeta(itemMeta -> itemMeta.setUnbreakable(true));
      this.getInventory().setItem(i + 9, bannerItem);
    }

    super.decorate(player);
  }

  public LinkedHashMap<GangData, Long> getTopAllTime(boolean sort) {
    HashSet<GangData> gangDataList = GangManager.getInstance().getSet();
    LinkedHashMap<GangData, Long> unsortedMap = new LinkedHashMap<>();
    for (GangData gangData : gangDataList) {
      if (gangData.getAllTimeCounter() > 0) {
        unsortedMap.put(gangData, gangData.getAllTimeCounter());
      }
    }
    if (sort) {
      return sortLinkedHash(unsortedMap);
    }
    return unsortedMap;
  }

  public LinkedHashMap<GangData, Long> getTopAllTime() {
    return getTopAllTime(true);
  }

  public LinkedHashMap<GangData, Long> getTopMonthly(boolean sort) {
    HashSet<GangData> gangDataList = GangManager.getInstance().getSet();
    LinkedHashMap<GangData, Long> unsortedMap = new LinkedHashMap<>();
    HashSet<LocalDate> localDates = GangManager.getInstance().todayMonth();
    for (GangData gangData : gangDataList) {
      Long totalAmount = 0L;
      for (Map.Entry<LocalDate, Long> entry : gangData.getDateBlockCountMap().entrySet()) {
        if (!localDates.contains(entry.getKey())) {
          continue;
        }
        totalAmount += entry.getValue();
      }
      if (totalAmount > 0) {
        unsortedMap.put(gangData, totalAmount);
      }
    }
    if (sort) {
      return sortLinkedHash(unsortedMap);
    }
    return unsortedMap;
  }

  public LinkedHashMap<GangData, Long> getTopMonthly() {
    return getTopMonthly(true);
  }

  /**
   * Retrieves the top gangs based on the weekly block count.
   *
   * <p>This method calculates the total number of blocks mined by each gang within the current
   * week. It iterates over all gang data, summing the block counts for dates that fall within the
   * current week.
   *
   * <p>
   *
   * @param sort if true, the resulting map is sorted by block count in descending order
   * @return a LinkedHashMap of GangData to total block count for the week
   */
  public LinkedHashMap<GangData, Long> getTopWeekly(boolean sort) {
    HashSet<GangData> gangDataList = GangManager.getInstance().getSet();
    LinkedHashMap<GangData, Long> unsortedMap = new LinkedHashMap<>();
    HashSet<LocalDate> localDates = GangManager.getInstance().todayWeek();
    for (GangData gangData : gangDataList) {
      Long totalAmount = 0L;
      for (Map.Entry<LocalDate, Long> entry : gangData.getDateBlockCountMap().entrySet()) {
        if (!localDates.contains(entry.getKey())) {
          continue;
        }
        totalAmount += entry.getValue();
      }
      if (totalAmount > 0) {
        unsortedMap.put(gangData, totalAmount);
      }
    }
    if (sort) {
      return sortLinkedHash(unsortedMap);
    }
    return unsortedMap;
  }

  public LinkedHashMap<GangData, Long> getTopWeekly() {
    return getTopWeekly(true);
  }

  private LinkedHashMap<GangData, Long> sortLinkedHash(LinkedHashMap<GangData, Long> unsortedMap) {
    try {
      unsortedMap =
          unsortedMap.entrySet().stream()
              .sorted(Map.Entry.<GangData, Long>comparingByValue().reversed())
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    } catch (Exception e) {
      Main.log(e.getMessage());
      throw new RuntimeException(e);
    }
    return unsortedMap;
  }

  private InventoryButton weeklyButton() {
    return new InventoryButton()
        .creator(
            player -> {
              Material material = viewType.equals(ViewType.WEEKLY) ? Material.PAPER : Material.BOOK;
              String name = viewType.equals(ViewType.WEEKLY) ? "&e&lWeekly" : "&eWeekly";
              String selectedLore =
                  viewType.equals(ViewType.WEEKLY) ? "&7&l[SELECTED]" : "&7[Click to select]";
              LocalDate today = GangManager.getInstance().todayDate();
              int count = 0;
              for (LocalDate date : GangManager.getInstance().todayWeek()) {
                if (date.toString().equals(today.toString())) {
                  break;
                }
                count++;
              }
              return ItemStackHelper.builder(material)
                  .name(name)
                  .addLore("&7View weekly blocks mined.")
                  .addLore("")
                  .addLore("&7(" + count + " days left)")
                  .addLore("")
                  .addLore(selectedLore)
                  .build();
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              if (!viewType.equals(ViewType.WEEKLY)) {
                Main.getGuiManager().openGUI(new GTopMenu(page, ViewType.WEEKLY), player);
              }
            });
  }

  private InventoryButton monthlyButton() {
    return new InventoryButton()
        .creator(
            player -> {
              Material material =
                  viewType.equals(ViewType.MONTHLY) ? Material.PAPER : Material.BOOK;
              String name = viewType.equals(ViewType.MONTHLY) ? "&e&lMonthly" : "&eMonthly";
              String selectedLore =
                  viewType.equals(ViewType.MONTHLY) ? "&7&l[SELECTED]" : "&7[Click to select]";
              LocalDate today = GangManager.getInstance().todayDate();
              int daysLeft = YearMonth.from(today).lengthOfMonth() - today.getDayOfMonth();
              String monthName =
                  today.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH);
              return ItemStackHelper.builder(material)
                  .name(name)
                  .addLore("&7View monthly blocks mined.")
                  .addLore("")
                  .addLore("&e&l" + monthName + " &7(" + daysLeft + " days left)")
                  .addLore("")
                  .addLore(selectedLore)
                  .build();
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              if (!viewType.equals(ViewType.MONTHLY)) {
                Main.getGuiManager().openGUI(new GTopMenu(page, ViewType.MONTHLY), player);
              }
            });
  }

  private InventoryButton allTimeButton() {
    return new InventoryButton()
        .creator(
            player -> {
              Material material =
                  viewType.equals(ViewType.ALL_TIME) ? Material.PAPER : Material.BOOK;
              String name = viewType.equals(ViewType.ALL_TIME) ? "&e&lAll Time" : "&eAll Time";
              String selectedLore =
                  viewType.equals(ViewType.ALL_TIME) ? "&7&l[SELECTED]" : "&7[Click to select]";
              return ItemStackHelper.builder(material)
                  .name(name)
                  .addLore("&7View all time blocks mined.")
                  .addLore("")
                  .addLore(selectedLore)
                  .build();
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              if (!viewType.equals(ViewType.ALL_TIME)) {
                Main.getGuiManager().openGUI(new GTopMenu(page, ViewType.ALL_TIME), player);
              }
            });
  }

  private InventoryButton backButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.ARROW)
                    .name("&c&lBack")
                    .addLore("&7Click to return to previous page")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              page--;
              Main.getGuiManager().openGUI(new GTopMenu(page, viewType), player);
            });
  }

  private InventoryButton nextButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.ARROW)
                    .name("&a&lNext")
                    .addLore("&7Click to view next page.")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              page++;
              Main.getGuiManager().openGUI(new GTopMenu(page, viewType), player);
            });
  }

  public enum ViewType {
    WEEKLY,
    MONTHLY,
    ALL_TIME
  }
}
