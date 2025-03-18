package me.mortaldev.gtop.utils;

public class Utils {


  public static String formatOrdinal(int number) {
    if (number % 100 >= 11 && number % 100 <= 13) {
      return number + "th";
    }
    return switch (number % 10) {
      case 1 -> number + "st";
      case 2 -> number + "nd";
      case 3 -> number + "rd";
      default -> number + "th";
    };
  }

  /**
   * Returns the given value clamped between the minimum and maximum values.
   *
   * @param value The value to be clamped.
   * @param min The minimum value.
   * @param max The maximum value.
   * @return The clamped value. If the value is less than the minimum value, the minimum value is
   *     returned. If the value is greater than the maximum value, the maximum value is returned.
   *     Otherwise, the value itself is returned.
   */
  public static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }
}
