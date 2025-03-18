package me.mortaldev.gtop.menus;

import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.modules.gang.GangStats;
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

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class GangStatsMenu extends InventoryGUI {
  int page;
  GTopMenu.ViewType viewType;
  GangData gangData;
  Month month;
  GangStats gangStats;

  public GangStatsMenu(int page, GTopMenu.ViewType viewType, GangData gangData) {
    this.page = page;
    this.viewType = viewType;
    this.gangData = gangData;
    this.month = GangManager.getInstance().todayDate().getMonth();
    this.gangStats = new GangStats().addMonth(gangData, month);
  }

  public GangStatsMenu(int page, GTopMenu.ViewType viewType, GangData gangData, Month month) {
    this.page = page;
    this.viewType = viewType;
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
    ItemStack endOfMonthPane =
        ItemStackHelper.builder(Material.RED_STAINED_GLASS_PANE).name("&7[END OF MONTH]").build();
    LocalDate localDate = GangManager.getInstance().todayDate();
    int length = YearMonth.of(localDate.getYear(), month).lengthOfMonth();
    for (int i = 10, j = 19, k = 28, l = 37, m = 46; i < 17; i++, j++, k++, l++, m++) {
      int day = i - 9;
      getInventory().setItem(i, getDayItem(day));
      getInventory().setItem(j, getDayItem(day + 7));
      getInventory().setItem(k, getDayItem(day + 14));
      getInventory().setItem(l, getDayItem(day + 21));
      if (day + 28 < length+1) {
        getInventory().setItem(m, getDayItem(day + 28));
      } else {
        getInventory().setItem(m, endOfMonthPane);
      }
    }
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
    super.decorate(player);
  }

  private ItemStack getDayItem(int day) {
    ItemStackHelper.Builder noDataItem =
        ItemStackHelper.builder(Material.GRAY_STAINED_GLASS_PANE).name("").addLore("");
    return gangStats.getFirstDay(day) > 0
        ? getYesDataItem(day)
        : noDataItem.name("&7" + Utils.formatOrdinal(day)).build();
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
              Main.getGuiManager()
                  .openGUI(new GangStatsMenu(page, viewType, gangData, month), player);
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
              Main.getGuiManager().openGUI(new GTopMenu(page, viewType), player);
            });
  }
}
