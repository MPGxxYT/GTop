package me.mortaldev.gtop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import me.mortaldev.gtop.utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandAlias("rename")
public class RenameCommand extends BaseCommand {

    @Default
    @CommandCompletion("@nothing")
    public void command(Player player, String[] args){
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) {
            player.sendMessage(TextUtil.format("&cYou must have something in your hand."));
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();

        if (args.length > 0) {
            StringBuilder newName = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i+1 == args.length) {
                    newName.append(args[i]);
                } else {
                    newName.append(args[i]).append(" ");
                }
            }
            itemMeta.displayName(TextUtil.format(newName.toString()));
        }
        item.setItemMeta(itemMeta);
    }
}
