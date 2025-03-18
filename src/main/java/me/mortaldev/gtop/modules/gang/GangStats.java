package me.mortaldev.gtop.modules.gang;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class GangStats {
  HashMap<LocalDate, Long> dateBlockCountMap = new HashMap<>();

  public Long getTotal() {
    Long total = 0L;
    for (Long value : dateBlockCountMap.values()) {
      total += value;
    }
    return total;
  }

  public GangStats addDay(LocalDate date, Long amount) {
    dateBlockCountMap.put(date, amount);
    return this;
  }

  public Long getDay(LocalDate date) {
    return dateBlockCountMap.getOrDefault(date, 0L);
  }

  public Long getFirstDay(int day) {
    for (Map.Entry<LocalDate, Long> entry : dateBlockCountMap.entrySet()) {
      if (entry.getKey().getDayOfMonth() == day) {
        return entry.getValue();
      }
    }
    return 0L;
  }

  public GangStats addMonth(GangData gangData, Month month) {
    for (Map.Entry<LocalDate, Long> entry : gangData.getDateBlockCountMap().entrySet()) {
      if (entry.getKey().getMonth() == month) {
        addDay(entry.getKey(), entry.getValue());
      }
    }
    return this;
  }
}
