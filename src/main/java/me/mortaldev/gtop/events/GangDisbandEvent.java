package me.mortaldev.gtop.events;

import net.brcdev.gangs.gang.Gang;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GangDisbandEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Gang gang;

  public GangDisbandEvent(Gang gang) {
    this.gang = gang;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  public Gang getGang() {
    return gang;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }
}
