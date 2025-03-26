package me.mortaldev.gtop.menus;

import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.modules.gang.MemberData;
import me.mortaldev.gtop.utils.ItemStackHelper;
import me.mortaldev.gtop.utils.TextUtil;
import me.mortaldev.gtop.utils.Utils;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class GangMemberStatsMenu extends InventoryGUI {
  LocalDate date;
  MemberData memberData;
  GangData gangData;
  Month month;
  PageData pageData;

  public GangMemberStatsMenu(
      GangData gangData, LocalDate date, PageData pageData) {
    this.pageData = pageData;
    this.gangData = gangData;
    this.date = date;
    this.month = date.getMonth();
    this.memberData = gangData.getMemberData(date);
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(
        null, 9 * getInventorySize(), TextUtil.format("Gang Member Stats"));
  }

  private int getInventorySize() {
    double rows = Math.ceil((double) memberData.getMap().size() / 9);
    return Utils.clamp((int) rows, 2, 6);
  }

  @Override
  public void decorate(Player player) {
    addButton(0, BackButton());
    for (int i = 0; i < 9; i++) {
      this.getInventory()
          .setItem(
              i,
              ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE)
                  .name("")
                  .addLore("")
                  .build());
    }
    int i = 9;
    for (Map.Entry<UUID, Long> entry : getSortedMembers().entrySet()) {
      UUID uuid = entry.getKey();
      getInventory().setItem(i, GangManager.getInstance().getHead(uuid, entry.getValue()));
      i++;
      if (i >= 53) {
        break;
      }
    }
    super.decorate(player);
  }

  private LinkedHashMap<UUID, Long> getSortedMembers() {
    LinkedHashMap<UUID, Long> sortedMembers = new LinkedHashMap<>();
    memberData.getMap().entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .forEachOrdered(e -> sortedMembers.put(e.getKey(), e.getValue()));
    return Utils.reverseMap(sortedMembers);
  }

  private InventoryButton BackButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.ARROW)
                    .name("&c&lBack")
                    .addLore("&7Click to return to previous menu.")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              Main.getGuiManager()
                  .openGUI(new GangStatsMenu(pageData, gangData, month), player);
            });
  }
}
