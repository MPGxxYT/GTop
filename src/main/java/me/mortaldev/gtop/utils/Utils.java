package me.mortaldev.gtop.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

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
   * Returns a new LinkedHashMap with the same key-value mappings as the given original
   * LinkedHashMap, but in reverse order. The original LinkedHashMap is not modified.
   *
   * @param original The LinkedHashMap to reverse.
   * @return A new LinkedHashMap with the same mappings, but in reverse order.
   */
  public static <K, V> LinkedHashMap<K, V> reverseMap(LinkedHashMap<K, V> original) {

    LinkedHashMap<K, V> reversed = new LinkedHashMap<>();
    ListIterator<Map.Entry<K, V>> iterator =
        new ArrayList<>(original.entrySet()).listIterator(original.size());

    while (iterator.hasPrevious()) {
      Map.Entry<K, V> entry = iterator.previous();
      reversed.put(entry.getKey(), entry.getValue());
    }

    return reversed;
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
