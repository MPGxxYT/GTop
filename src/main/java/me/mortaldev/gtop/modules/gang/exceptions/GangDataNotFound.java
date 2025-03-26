package me.mortaldev.gtop.modules.gang.exceptions;

import net.brcdev.gangs.gang.Gang;

public class GangDataNotFound extends Throwable {
  public GangDataNotFound(Gang gang) {
    super("Failed to find gangData for gang: " + gang.getName());
  }
}
