package me.mortaldev.gtop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import me.mortaldev.gtop.utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("lore")
public class LoreCommand extends BaseCommand {

    @Default
    @CommandCompletion("@nothing")
    public void command(Player player, String[] args){
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) {
            player.sendMessage(TextUtil.format("&cYou must have something in your hand."));
            return;
        }
        List<Component> newItemLore = new ArrayList<>();
        StringBuilder fullLore = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i+1 == args.length) {
                fullLore.append(args[i]);
            } else {
                fullLore.append(args[i]).append(" ");
            }
        }

        String[] loreInput;

        if (args.length > 0) {
            loreInput = fullLore.toString().split(";;");
            for (String s : loreInput) {
                newItemLore.add(TextUtil.format(s));
            }
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.lore(newItemLore);
        item.setItemMeta(itemMeta);
    }
}
