package me.mortaldev.gtop.menus;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.modules.gang.GangStats;
import me.mortaldev.gtop.modules.gang.MemberData;
import me.mortaldev.gtop.utils.ItemStackHelper;
import me.mortaldev.gtop.utils.TextUtil;
import me.mortaldev.gtop.utils.Utils;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class GangStatsMenu extends InventoryGUI {
  PageData pageData;
  GangData gangData;
  Month month;
  GangStats gangStats;

  public GangStatsMenu(PageData pageData, GangData gangData) {
    this.pageData = pageData;
    this.gangData = gangData;
    this.month = GangManager.getInstance().todayDate().getMonth();
    this.gangStats = new GangStats().addMonth(gangData, month);
  }

  public GangStatsMenu(PageData pageData, GangData gangData, Month month) {
    this.pageData = pageData;
    this.gangData = gangData;
    this.month = month;
    this.gangStats = new GangStats().addMonth(gangData, month);
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(
        null, 9 * 6, TextUtil.format(gangData.getGangName() + " Monthly Stats"));
  }

  @Override
  public void decorate(Player player) {
    for (int i = 0; i < 54; i++) {
      this.getInventory()
          .setItem(
              i,
              ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE)
                  .name("")
                  .addLore("")
                  .build());
    }
    LocalDate localDate = GangManager.getInstance().todayDate();
    Month todayMonth = localDate.getMonth();
    int slot = 3;
    for (int i = 0; i < Main.getMainConfig().getDataSavingLength(); i++) {
      Month minus = todayMonth.minus(i);
      addButton(slot, MonthButton(minus));
      if (Main.getMainConfig().getDataSavingLength() % 2 == 0) {
        slot += 2;
      } else {
        slot += 1;
      }
    }
    this.addButton(0, BackButton());
    this.addButton(8, ViewSwitchButton());
    switch (pageData.getGangStatsViewType()) {
      case DAY_TOTAL -> dayTotalView();
      case MEMBER_TOTAL -> memberTotalView();
    }
    super.decorate(player);
  }

  private void memberTotalView() {
    LinkedHashMap<UUID, Long> totalUUIDMap = new LinkedHashMap<>();
    for (Map.Entry<LocalDate, MemberData> entry : gangData.getMemberBlockCountMap().entrySet()) {
      if (entry.getKey().getMonthValue() == month.getValue()) {
        HashMap<UUID, Long> map = entry.getValue().getMap();
        LinkedHashMap<UUID, Long> finalTotalUUIDMap = totalUUIDMap;
        map.forEach((uuid, aLong) -> finalTotalUUIDMap.merge(uuid, aLong, Long::sum));
      }
    }
    LinkedHashMap<UUID, Long> sortedMembers = new LinkedHashMap<>();
    totalUUIDMap.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .forEachOrdered(e -> sortedMembers.put(e.getKey(), e.getValue()));
    totalUUIDMap = Utils.reverseMap(sortedMembers);
    Iterator<Map.Entry<UUID, Long>> iterator = totalUUIDMap.entrySet().iterator();
    for (int i = 0, slot = 10; i < 35; i++, slot++) {
      if (!iterator.hasNext()) {
        this.getInventory().setItem(slot, ItemStackHelper.builder(Material.AIR).build());
      } else {
        Map.Entry<UUID, Long> next = iterator.next();
        ItemStack head = GangManager.getInstance().getHead(next.getKey(), next.getValue());
        getInventory().setItem(slot, head);
      }
      if (slot == 16 || slot == 25 || slot == 34 || slot == 43) {
        slot += 2;
      }
    }
  }

  private void dayTotalView() {
    LocalDate localDate = GangManager.getInstance().todayDate();
    ItemStack endOfMonthPane =
        ItemStackHelper.builder(Material.RED_STAINED_GLASS_PANE).name("&7[END OF MONTH]").build();
    int length = YearMonth.of(localDate.getYear(), month).lengthOfMonth();
    for (int i = 10, j = 19, k = 28, l = 37, m = 46; i < 17; i++, j++, k++, l++, m++) {
      int day = i - 9;
      addButton(i, getDayButton(day));
      addButton(j, getDayButton(day + 7));
      addButton(k, getDayButton(day + 14));
      addButton(l, getDayButton(day + 21));
      if (day + 28 < length + 1) {
        addButton(m, getDayButton(day + 28));
      } else {
        getInventory().setItem(m, endOfMonthPane);
      }
    }
  }

  private InventoryButton ViewSwitchButton() {
    return new InventoryButton()
        .creator(
            player -> {
              ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
              SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
              skullMeta.setOwningPlayer(player);
              skull.setItemMeta(skullMeta);
              ItemStackHelper.Builder builder =
                  ItemStackHelper.builder(skull)
                      .name("&e&lViewing Type")
                      .addLore("")
                      .addLore("&7Day Total")
                      .addLore("&7Member Total")
                      .addLore("")
                      .addLore("&7[Click to switch view]");
              if (pageData.getGangStatsViewType() == ViewType.DAY_TOTAL) {
                builder.setLore(TextUtil.format("&f&l Day Total"), 1);
              } else {
                builder.setLore(TextUtil.format("&f&l Member Total"), 2);
              }
              return builder.build();
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              pageData.setGangStatsViewType(ViewType.nextView(pageData.getGangStatsViewType()));
              Main.getGuiManager().openGUI(new GangStatsMenu(pageData, gangData, month), player);
            });
  }

  private InventoryButton getDayButton(int day) {
    ItemStackHelper.Builder noDataItem =
        ItemStackHelper.builder(Material.GRAY_STAINED_GLASS_PANE).name("").addLore("");
    if (gangStats.getFirstDay(day) > 0) {
      return new InventoryButton()
          .creator(player -> getYesDataItem(day))
          .consumer(
              event -> {
                Player player = (Player) event.getWhoClicked();
                LocalDate date =
                    LocalDate.of(GangManager.getInstance().todayDate().getYear(), month, day);
                Main.getGuiManager()
                    .openGUI(new GangMemberStatsMenu(gangData, date, pageData), player);
              });
    } else {
      return new InventoryButton()
          .creator(player -> noDataItem.name("&7" + Utils.formatOrdinal(day)).build())
          .consumer(event -> {});
    }
  }

  private ItemStack getYesDataItem(int day) {
    Long total = gangStats.getTotal();
    Long dayAmount = gangStats.getFirstDay(day);
    float percentOfTotal = ((float) dayAmount / total) * 100;
    ItemStackHelper.Builder yesDataItem;
    if (percentOfTotal >= 50) {
      yesDataItem = ItemStackHelper.builder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).name("");
    } else if (percentOfTotal >= 20) {
      yesDataItem = ItemStackHelper.builder(Material.LIME_STAINED_GLASS_PANE).name("");
    } else if (percentOfTotal >= 5) {
      yesDataItem = ItemStackHelper.builder(Material.YELLOW_STAINED_GLASS_PANE).name("");
    } else {
      yesDataItem = ItemStackHelper.builder(Material.ORANGE_STAINED_GLASS_PANE).name("");
    }
    return yesDataItem
        .name("&e&l" + Utils.formatOrdinal(day))
        .addLore("&f - " + String.format("%,d", dayAmount) + " Blocks Mined")
        .addLore("")
        .addLore("&7[Click to view breakdown]")
        .build();
  }

  private InventoryButton MonthButton(Month month) {
    GangStats newGangStats = new GangStats().addMonth(gangData, month);
    String monthDisplayName = month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH);
    return new InventoryButton()
        .creator(
            player -> {
              ItemStackHelper.Builder displayItem =
                  ItemStackHelper.builder(Material.BOOK)
                      .name("&e&l" + monthDisplayName)
                      .addLore(
                          "&7 - " + String.format("%,d", newGangStats.getTotal()) + " Blocks Mined")
                      .addLore("");
              if (month.equals(this.month)) {
                displayItem.addLore("&7&l[SELECTED]");
                ItemStack build = displayItem.addItemFlag(ItemFlag.HIDE_ENCHANTS).build();
                build.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
                return build;
              } else {
                return displayItem.addLore("&7[Click to view]").build();
              }
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              Main.getGuiManager().openGUI(new GangStatsMenu(pageData, gangData, month), player);
            });
  }

  private InventoryButton BackButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.ARROW)
                    .name("&c&lBack")
                    .addLore("&7Click to return to previous page.")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              Main.getGuiManager().openGUI(new GTopMenu(pageData), player);
            });
  }

  public enum ViewType {
    DAY_TOTAL,
    MEMBER_TOTAL;

    public static ViewType nextView(ViewType viewType) {
      return switch (viewType) {
        case DAY_TOTAL -> MEMBER_TOTAL;
        case MEMBER_TOTAL -> DAY_TOTAL;
      };
    }
  }
}
