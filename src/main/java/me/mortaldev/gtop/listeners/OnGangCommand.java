package me.mortaldev.gtop.listeners;

import java.util.HashSet;
import java.util.UUID;
import me.mortaldev.gtop.events.GangDisbandEvent;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import me.mortaldev.gtop.utils.TextUtil;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

public class OnGangCommand implements Listener {

  private final HashSet<UUID> confirmDisbandList = new HashSet<>();

  @EventHandler
  public void GangCommand(PlayerCommandPreprocessEvent event) {
    if (event.getMessage().startsWith("/gang ") || event.getMessage().startsWith("/g ")) {
      String[] commandSplit = event.getMessage().split(" ");
      Player player = event.getPlayer();
      switch (commandSplit[1].toLowerCase()) {
        case "setbanner" -> {
          event.setCancelled(true);
          Gang gang = GangsPlusApi.getPlayersGang(player);
          if (gang == null) {
            player.sendMessage(TextUtil.format("&cYou have to be in a gang to use this command."));
            return;
          }
          if (!gang.getOwner().equals(player)) {
            player.sendMessage(TextUtil.format("&cOnly the owner can do this."));
            return;
          }
          ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
          if (itemInMainHand.getType().equals(Material.AIR)) {
            player.sendMessage(TextUtil.format("&cYou must have something in your hand."));
            return;
          }
          GangData gangData = GangManager.getInstance().getByID(gang.getName()).orElseThrow();
          gangData.setBanner(new ItemStack(itemInMainHand.getType()));
          player.sendMessage(TextUtil.format("&eGang Banner Set!"));
        }
        case "disband" -> {
          Gang gang = GangsPlusApi.getPlayersGang(player);
          if (gang == null) {
            player.sendMessage(TextUtil.format("&7You have to be in a gang to use this command."));
            return;
          }
          if (!gang.getOwner().equals(player)) {
            player.sendMessage(TextUtil.format("&cOnly the owner can do this."));
            return;
          }
          confirmDisbandList.add(player.getUniqueId());
          // If needed, add scheduler for removing from list.
        }
        case "confirm" -> {
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
}
