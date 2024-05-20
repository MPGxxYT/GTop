package me.mortaldev.gtop.modules.gang;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class GangManager {

  public GangManager() {
    LocalDate localDate = ZonedDateTime.now(ZoneId.of("-05:00")).toLocalDate();
  }


}
