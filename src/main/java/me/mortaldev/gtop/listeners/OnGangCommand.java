package me.mortaldev.gtop.listeners;

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

public class OnGangCommand implements Listener {

  @EventHandler
  public void GangCommand(PlayerCommandPreprocessEvent event){
    if (!event.getMessage().startsWith("/gang ")) {
      return;
    }
    String[] commandSplit = event.getMessage().split(" ");
    if (!commandSplit[1].equalsIgnoreCase("setbanner")) {
      return;
    }
    event.setCancelled(true);
    Player player = event.getPlayer();
    Gang gang = GangsPlusApi.getPlayersGang(player);
    if (!gang.getOwner().equals(player)) {
      player.sendMessage(TextUtil.format("&cOnly the owner can do this."));
      return;
    }
    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().equals(Material.AIR) || !Tag.BANNERS.isTagged(itemInMainHand.getType())) {
      player.sendMessage(TextUtil.format("&cYou must be holding a banner."));
    }
    gang.getName();
  }
}
