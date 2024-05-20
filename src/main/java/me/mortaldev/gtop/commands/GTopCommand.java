package me.mortaldev.gtop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.menus.GTopMenu;
import org.bukkit.entity.Player;

@CommandAlias("gtop")
public class GTopCommand extends BaseCommand {

  @Default
  public void openMenu(Player player, String[] args){
    Main.getGuiManager().openGUI(new GTopMenu(1, GTopMenu.ViewType.ALL_TIME), player);
  }
}
