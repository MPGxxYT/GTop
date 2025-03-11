package me.mortaldev.gtop.modules.gang;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimedRunnable {

  Runnable runnable;
  ScheduledExecutorService scheduler;

  public TimedRunnable() {
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
  }

  /**
   * Start this runnable to run at the last minute of every month.
   *
   * @param runnable the runnable to run
   */
  public void start(Runnable runnable) {
    this.runnable = runnable;
    scheduler.scheduleAtFixedRate(this::checkLastMinute, 0, 1, TimeUnit.MINUTES);
  }

  /** Stops the runnable from running. This should be called when the plugin is being disabled. */
  public void stop() {
    scheduler.shutdown();
  }

  private void checkLastMinute() {
    LocalDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime();
    if (isLastMinuteOfLastDayOfMonth(now)) {
      runnable.run();
    }
  }

  private boolean isLastMinuteOfLastDayOfMonth(LocalDateTime date) {
    int lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
    int currentDayOfMonth = date.getDayOfMonth();
    int currentMinute = date.getMinute();
    int currentHour = date.getHour();
    return currentDayOfMonth == lastDayOfMonth && currentMinute >= 57 && currentHour == 23;
  }
}
