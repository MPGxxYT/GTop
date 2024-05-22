package me.mortaldev.gtop.modules.gang;

import me.mortaldev.gtop.utils.ItemStackHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.HashMap;

public class GangData {
  private final String gangName;
  private final HashMap<Date, Integer> dateBlockCounterMap;
  private Long allTimeCounter;
  private String banner;
  private static final ItemStack DEFAULT_BANNER = new ItemStack(Material.WHITE_BANNER);

  public GangData(String gangName) {
    this(gangName, DEFAULT_BANNER);
  }

  private GangData(String gangName, ItemStack banner) {
    this(gangName, ItemStackHelper.serialize(banner));
  }

  private GangData(String gangName, String banner) {
    this.gangName = gangName;
    this.dateBlockCounterMap = new HashMap<>();
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

  public Integer getBlocksOnDate(Date date) {
    if (dateBlockCounterMap.containsKey(date)) {
      return dateBlockCounterMap.get(date);
    }
    return null;
  }

  public Long getAllTimeCounter() {
    return allTimeCounter;
  }

  public void setAllTimeCounter(Long allTimeCounter) {
    this.allTimeCounter = allTimeCounter;
  }

  public void setBlocksOnDate(Date date, Integer amount) {
    dateBlockCounterMap.put(date, amount);
  }

}
