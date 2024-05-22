package me.mortaldev.gtop.listeners;

import me.mortaldev.gtop.events.GangDisbandEvent;
import me.mortaldev.gtop.modules.gang.GangManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnGangDisband implements Listener {

  @EventHandler
  public void gangDisband(GangDisbandEvent event){
    GangManager.deleteGang(event.getGang());
  }
}
