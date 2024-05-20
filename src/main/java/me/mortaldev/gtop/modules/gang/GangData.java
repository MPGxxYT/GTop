package me.mortaldev.gtop.modules.gang;

import me.mortaldev.gtop.utils.ItemStackHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.HashMap;

public class GangData {
  private final String gangName;

  // Stores the date and the amount of blocks mined that day.
  // (will throughout said day)
  private final HashMap<Date, Integer> dateBlockCounterMap;
  private String banner;

  public GangData(String gangName) {
    this(gangName, new ItemStack(Material.WHITE_BANNER));
  }
  public GangData(String gangName, ItemStack banner) {
    this(gangName, ItemStackHelper.serialize(banner));
  }
  public GangData(String gangName, String banner){
    this.gangName = gangName;
    dateBlockCounterMap = new HashMap<>();
    this.banner = banner;
  }

  public String getGangName() {
    return gangName;
  }
  public String getRawBanner() {
    return banner;
  }
  public ItemStack getBanner() {
    return ItemStackHelper.deserialize(banner);
  }
  public Integer getBlocksOnDate(Date date){
    if (dateBlockCounterMap.containsKey(date)) {
      return dateBlockCounterMap.get(date);
    }
    return null;
  }
  public void setBlocksOnDate(Date date, Integer amount){
    dateBlockCounterMap.put(date, amount);
  }
  void setBanner(ItemStack itemStack){
    banner = ItemStackHelper.serialize(itemStack);
  }
  void setRawBanner(String data){
    banner = data;
  }

}
