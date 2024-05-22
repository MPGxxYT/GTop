package me.mortaldev.gtop.listeners;

import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import net.brcdev.gangs.event.GangCreateEvent;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnGangCreate implements Listener {

  @EventHandler
  public void gangCreate(GangCreateEvent event){
    Gang gang = event.getGang();
    GangData gangData = new GangData(gang.getName());
    GangManager.addGangData(gangData);
  }
}
