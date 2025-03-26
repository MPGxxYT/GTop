package me.mortaldev.gtop.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class TimedRunnable {

  Runnable runnable;
  ScheduledExecutorService scheduler;
  ZoneId timeZone;

  public TimedRunnable(ZoneId timeZone) {
    this.timeZone = timeZone;
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
  }

  public void start(Runnable runnable, Predicate<LocalDateTime> predicate) {
    this.runnable = runnable;
    scheduler.scheduleAtFixedRate(
        () -> {
          if (predicate.test(ZonedDateTime.now(timeZone).toLocalDateTime())) {
            runnable.run();
          }
        },
        0,
        1,
        TimeUnit.MINUTES);
  }

  /** Stops the runnable from running. This should be called when the plugin is being disabled. */
  public void stop() {
    scheduler.shutdown();
  }
}
