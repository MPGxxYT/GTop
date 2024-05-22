package me.mortaldev.gtop.listeners;

import me.mortaldev.gtop.events.GangDisbandEvent;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.utils.TextUtil;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OnGangCommand implements Listener {

  private final List<UUID> confirmDisbandList = new ArrayList<>();

  @EventHandler
  public void GangCommand(PlayerCommandPreprocessEvent event) {
    if (event.getMessage().startsWith("/gang ") || event.getMessage().startsWith("/g ")) {
      String[] commandSplit = event.getMessage().split(" ");
      Player player = event.getPlayer();
      if (commandSplit[1].equalsIgnoreCase("setbanner")) {
        event.setCancelled(true);
        Gang gang = GangsPlusApi.getPlayersGang(player);
        if (gang == null) {
          return;
        }
        if (!gang.getOwner().equals(player)) {
          player.sendMessage(TextUtil.format("&cOnly the owner can do this."));
          return;
        }
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType().equals(Material.AIR) || !Tag.BANNERS.isTagged(itemInMainHand.getType())) {
          player.sendMessage(TextUtil.format("&cYou must be holding a banner."));
        }
        GangData gangData = GangManager.getGangData(gang);
        gangData.setBanner(itemInMainHand);
        player.sendMessage(TextUtil.format("&eGang Banner Set!"));
      } else if (commandSplit[1].equalsIgnoreCase("disband")) {
        Gang gang = GangsPlusApi.getPlayersGang(player);
        if (gang == null) {
          return;
        }
        if (!gang.getOwner().equals(player)) {
          return;
        }
        confirmDisbandList.add(player.getUniqueId());
        // If needed, add scheduler for removing from list.
      } else if (commandSplit[1].equalsIgnoreCase("confirm")) {
        if (!confirmDisbandList.contains(player.getUniqueId())) {
          return;
        }
        Gang gang = GangsPlusApi.getPlayersGang(player);
        if (gang == null) {
          confirmDisbandList.remove(player.getUniqueId());
          return;
        }
        new GangDisbandEvent(gang).callEvent();
        confirmDisbandList.remove(player.getUniqueId());
      }
    }
  }
}
