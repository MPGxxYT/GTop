package me.mortaldev.gtop.modules.gang;

import me.mortaldev.crudapi.CRUD;
import me.mortaldev.gtop.utils.ItemStackHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class GangData implements CRUD.Identifiable {
  private static final ItemStack DEFAULT_BANNER = new ItemStack(Material.WHITE_BANNER);
  private final String gangName;
  private final LinkedHashMap<String, Long> dateBlockCountMap;
  private Long allTimeCounter;
  private String banner;

  public GangData(String gangName) {
    this(gangName, DEFAULT_BANNER);
  }

  private GangData(String gangName, ItemStack banner) {
    this(gangName, ItemStackHelper.serialize(banner));
  }

  private GangData(String gangName, String banner) {
    this.gangName = gangName;
    this.dateBlockCountMap = new LinkedHashMap<>() {{
      put(localDateToString(GangManager.getInstance().todayDate()), 0L);
    }};
    this.allTimeCounter = 0L;
    this.banner = banner;
  }

  public String getGangName() {
    return gangName;
  }

  public String getRawBanner() {
    return banner;
  }

  public void setRawBanner(String data) {
    banner = data;
  }

  public ItemStack getBanner() {
    return ItemStackHelper.deserialize(banner);
  }

  public void setBanner(ItemStack itemStack) {
    banner = ItemStackHelper.serialize(itemStack);
  }

  public Long getBlocksCountOnDate(LocalDate date) {
    String stringDate = localDateToString(date);
    if (dateBlockCountMap.containsKey(stringDate)) {
      return dateBlockCountMap.get(stringDate);
    }
    return 0L;
  }

  public LinkedHashMap<LocalDate, Long> getDateBlockCountMap() {
    LinkedHashMap<LocalDate, Long> returnMap = new LinkedHashMap<>();
    for (Map.Entry<String, Long> entry : dateBlockCountMap.entrySet()) {
      LocalDate localDate = localDateFromString(entry.getKey());
      returnMap.put(localDate, entry.getValue());
    }
    return returnMap;
  }

  public void setDateBlockCountMap(LinkedHashMap<LocalDate, Long> linkedHashMap) {
    dateBlockCountMap.clear();
    linkedHashMap.forEach((key, value) -> dateBlockCountMap.put(localDateToString(key), value));
  }

  public Long getAllTimeCounter() {
    return allTimeCounter;
  }

  public void setAllTimeCounter(Long allTimeCounter) {
    this.allTimeCounter = allTimeCounter;
  }

  public void setBlocksCountOnDate(LocalDate date, Long amount) {
    dateBlockCountMap.put(localDateToString(date), amount);
  }

  private String localDateToString(LocalDate date) {
    return date.format(DateTimeFormatter.ISO_DATE);
  }

  private LocalDate localDateFromString(String dateString) {
    return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
  }

  @Override
  public String getID() {
    return gangName;
  }
}
