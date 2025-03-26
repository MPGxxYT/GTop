package me.mortaldev.gtop.menus;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.modules.gang.MemberData;
import me.mortaldev.gtop.modules.gang.exceptions.GangDataNotFound;
import me.mortaldev.gtop.utils.ItemStackHelper;
import me.mortaldev.gtop.utils.TextUtil;
import me.mortaldev.gtop.utils.Utils;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class GTopMenu extends InventoryGUI {

  private LinkedHashMap<GangData, Long> topGangMap;
  private PageData pageData;
  private int maxPage;
  private int page;
  private ViewType viewType;

  public GTopMenu(PageData pageData) {
    this.pageData = pageData;
    this.viewType = pageData.getGtopViewType();
    this.page = pageData.getPage();
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
    this.addButton(3, WeeklyButton());
    this.addButton(4, MonthlyButton());
    this.addButton(5, AllTimeButton());
    ItemStack whiteGlass =
        ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE).name("").addLore("").build();
    int[] glassSlots = {0, 1, 2, 6, 7, 8};
    for (int i : glassSlots) {
      this.getInventory().setItem(i, whiteGlass);
    }
    if (page > 1) {
      this.addButton(0, BackButton());
    }
    if (maxPage > 1 && maxPage != page) {
      this.addButton(8, NextButton());
    }
    Gang playersGang = GangsPlusApi.getPlayersGang(player);
    if (playersGang != null) {
      try {
        this.addButton(7, GangStatsButton(playersGang));
      } catch (GangDataNotFound e) {
        this.getInventory().setItem(7, whiteGlass);
        Main.warn(e.getMessage());
      }
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
      InventoryButton gangDisplayButton =
          GangDisplayButton(next.getKey(), i + 1 + ((page - 1) * 45));
      addButton(i + 9, gangDisplayButton);
    }

    super.decorate(player);
  }

  private InventoryButton GangDisplayButton(GangData gang, int placement) {
    GangData gangData = GangManager.getInstance().getByID(gang.getGangName()).orElseThrow();
    return new InventoryButton()
        .creator(
            player -> {
              ItemStack build =
                  ItemStackHelper.builder(gangData.getBanner())
                      .name("&e&l#" + placement + " " + gang.getGangName())
                      .lore(new ArrayList<>())
                      .addLore(
                          "&7 - "
                              + String.format("%,d", topGangMap.get(gangData))
                              + " Blocks Mined")
                      .addLore("")
                      .addLore("&7[Click to view stats]")
                      .build();
              build.editMeta(
                  m -> {
                    m.addItemFlags(ItemFlag.values());
                    m.setUnbreakable(true);
                  });
              return build;
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              Main.getGuiManager()
                  .openGUI(new GangStatsMenu(pageData.setPage(page), gangData), player);
            });
  }

  private InventoryButton GangStatsButton(Gang gang) throws GangDataNotFound {
    GangData gangData =
        GangManager.getInstance()
            .getByID(gang.getName())
            .orElseThrow(() -> new GangDataNotFound(gang));
    Iterator<GangData> iterator = topGangMap.keySet().iterator();
    int placement = 1;
    while (iterator.hasNext()) {
      GangData next = iterator.next();
      if (next.equals(gangData)) {
        break;
      }
      placement++;
    }
    Long blocks = topGangMap.get(gangData);
    if (blocks == null) {
      blocks = 0L;
    }
    int finalPlacement = placement;
    Long finalBlocks = blocks;
    return new InventoryButton()
        .creator(
            player -> {
              ItemStack build =
                  ItemStackHelper.builder(gangData.getBanner())
                      .name("&e&l#" + finalPlacement + " " + gang.getName())
                      .addLore("&7 - " + String.format("%,d", finalBlocks) + " Blocks Mined")
                      .addLore("")
                      .addLore("&7[Click to view stats]")
                      .build();
              build.editMeta(
                  m -> {
                    m.addItemFlags(ItemFlag.values());
                    m.setUnbreakable(true);
                  });
              return build;
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();

              Main.getGuiManager()
                  .openGUI(new GangStatsMenu(pageData.setPage(page), gangData), player);
            });
  }

  private InventoryButton WeeklyButton() {
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
                Main.getGuiManager()
                    .openGUI(
                        new GTopMenu(pageData.setPage(page).setGtopViewType(ViewType.WEEKLY)),
                        player);
              }
            });
  }

  private InventoryButton MonthlyButton() {
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
                Main.getGuiManager()
                    .openGUI(
                        new GTopMenu(pageData.setPage(page).setGtopViewType(ViewType.MONTHLY)),
                        player);
              }
            });
  }

  private InventoryButton AllTimeButton() {
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
                Main.getGuiManager()
                    .openGUI(
                        new GTopMenu(pageData.setPage(page).setGtopViewType(ViewType.ALL_TIME)),
                        player);
              }
            });
  }

  private InventoryButton BackButton() {
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
              Main.getGuiManager()
                  .openGUI(new GTopMenu(pageData.setPage(page).setGtopViewType(viewType)), player);
            });
  }

  private InventoryButton NextButton() {
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
              Main.getGuiManager()
                  .openGUI(new GTopMenu(pageData.setPage(page).setGtopViewType(viewType)), player);
            });
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
      for (Map.Entry<LocalDate, MemberData> entry : gangData.getMemberBlockCountMap().entrySet()) {
        if (!localDates.contains(entry.getKey())) {
          continue;
        }
        totalAmount += entry.getValue().getTotal();
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
      for (Map.Entry<LocalDate, MemberData> entry : gangData.getMemberBlockCountMap().entrySet()) {
        if (!localDates.contains(entry.getKey())) {
          continue;
        }
        totalAmount += entry.getValue().getTotal();
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

  public enum ViewType {
    WEEKLY,
    MONTHLY,
    ALL_TIME
  }
}
