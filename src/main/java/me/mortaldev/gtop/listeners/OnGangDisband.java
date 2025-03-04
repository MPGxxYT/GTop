package me.mortaldev.gtop.listeners;

import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.events.GangDisbandEvent;
import me.mortaldev.gtop.modules.gang.GangManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnGangDisband implements Listener {

  @EventHandler
  public void gangDisband(GangDisbandEvent event) {
    String name = event.getGang().getName();
    if (name == null) {
      Main.log("Could not disband, gang name was null.");
    }
    GangManager.getInstance().getByID(name).ifPresent(GangManager.getInstance()::remove);
  }
}
