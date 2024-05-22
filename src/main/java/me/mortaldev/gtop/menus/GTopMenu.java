package me.mortaldev.gtop.menus;

import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.modules.menu.InventoryButton;
import me.mortaldev.gtop.modules.menu.InventoryGUI;
import me.mortaldev.gtop.utils.ItemStackHelper;
import me.mortaldev.gtop.utils.TextUtil;
import me.mortaldev.gtop.utils.Utils;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GTopMenu extends InventoryGUI {

  static List<Gang> allGangs = GangsPlusApi.getAllGangs();
  static int maxPage = (int) Math.ceil((double) allGangs.size() / 45);
  int page;
  ViewType viewType;

  public GTopMenu(int page, ViewType viewType) {
    this.viewType = viewType;
    if (page > maxPage) {
      this.page = maxPage;
      return;
    }
    this.page = page;

  }

  public int inventorySize(){
    int size = (int) Math.ceil((double) allGangs.size() / 9);
    return Utils.clamp(size, 3, 6);
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, inventorySize() * 9, TextUtil.format("&3&lJBCrates"));
  }

  @Override
  public void decorate(Player player) {
    // Buttons
    this.addButton(3, weeklyButton());
    this.addButton(4, monthlyButton());
    this.addButton(5, allTimeButton());
    ItemStack whiteGlass = ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE).name("").addLore("").build();
    int[] glassSlots = {0, 1, 2, 6, 7, 8};
    for (int i : glassSlots) {
      this.getInventory().setItem(i, whiteGlass);
    }
    if (page > 1) {
      this.addButton(0, backButton());
    }
    if (maxPage > 1) {
      this.addButton(8, nextButton());
    }

    // Display
    List<GangData> gangDataList = GangManager.getGangDataList();
    for (int i = 0; i < 9; i++) {

    }

    super.decorate(player);
  }

  private InventoryButton weeklyButton() {
    return new InventoryButton()
        .creator(
            player -> {
              Material material = viewType.equals(ViewType.WEEKLY) ? Material.PAPER : Material.BOOK;
              String name = viewType.equals(ViewType.WEEKLY) ? "&e&lWeekly" : "&eWeekly";
              String selectedLore = viewType.equals(ViewType.WEEKLY) ? "&7&l[SELECTED]" : "&7[Click to select]";
              return ItemStackHelper.builder(material).name(name).addLore("&7View weekly blocks mined.").addLore("").addLore(selectedLore).build();
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
              Material material = viewType.equals(ViewType.MONTHLY) ? Material.PAPER : Material.BOOK;
              String name = viewType.equals(ViewType.MONTHLY) ? "&e&lMonthly" : "&eMonthly";
              String selectedLore = viewType.equals(ViewType.MONTHLY) ? "&7&l[SELECTED]" : "&7[Click to select]";
              return ItemStackHelper.builder(material).name(name).addLore("&7View monthly blocks mined.").addLore("").addLore(selectedLore).build();
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
              Material material = viewType.equals(ViewType.ALL_TIME) ? Material.PAPER : Material.BOOK;
              String name = viewType.equals(ViewType.ALL_TIME) ? "&e&lAll Time" : "&eAll Time";
              String selectedLore = viewType.equals(ViewType.ALL_TIME) ? "&7&l[SELECTED]" : "&7[Click to select]";
              return ItemStackHelper.builder(material).name(name).addLore("&7View all time blocks mined.").addLore("").addLore(selectedLore).build();
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
            player -> ItemStackHelper.builder(Material.ARROW).name("&c&lBack").addLore("&7Click to return to previous page").build())
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
            player -> ItemStackHelper.builder(Material.ARROW).name("&a&lNext").addLore("&7Click to view next page.").build())
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
