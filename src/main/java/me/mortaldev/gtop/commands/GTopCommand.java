package me.mortaldev.gtop.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.menus.GTopMenu;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("gtop")
public class GTopCommand extends BaseCommand {

  @Default
  public void openMenu(Player player) {
    Main.getGuiManager().openGUI(new GTopMenu(1, GTopMenu.ViewType.ALL_TIME), player);
  }

  @Subcommand("reload")
  @CommandPermission("gtop.admin")
  public void reloadConfig(CommandSender sender) {
    String reloadResponse = Main.getMainConfig().reload();
    if (sender instanceof Player) {
      Main.log(reloadResponse);
    }
    sender.sendMessage(reloadResponse);
  }

  @Subcommand("save")
  @CommandPermission("gtop.admin")
  public void saveData(CommandSender sender) {
    sender.sendMessage("Saving gang data.");
    GangManager.getInstance().saveAllGangData();
  }

  @Subcommand("update")
  @CommandCompletion("@gangs")
  @Syntax("<gang name>")
  @CommandPermission("gtop.admin")
  public void updateGang(CommandSender sender, GangData gangData) {
    sender.sendMessage("Updating gang data.");
    GangManager.getInstance().loadByID(gangData.getGangName());
  }

  @Subcommand("report")
  @CommandPermission("gtop.admin")
  public void makeReport(CommandSender sender) {
    GangManager.getInstance().makeReport();
    sender.sendMessage("Saving a report.");
  }
}
