package me.mortaldev.gtop.modules.gang;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import me.mortaldev.crudapi.CRUD;
import me.mortaldev.gtop.utils.ItemStackHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GangData implements CRUD.Identifiable {
  private static final ItemStack DEFAULT_BANNER = new ItemStack(Material.WHITE_BANNER);
  private final String gangName;
  private final LinkedHashMap<String, MemberData> memberBlockCountMap = new LinkedHashMap<>();
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

  public Long getAllTimeCounter() {
    return allTimeCounter;
  }

  public void setAllTimeCounter(Long allTimeCounter) {
    this.allTimeCounter = allTimeCounter;
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

  public MemberData getMemberData(LocalDate localDate) {
    String dateAsString = localDateToString(localDate);
    if (memberBlockCountMap.get(dateAsString) == null) {
      memberBlockCountMap.put(dateAsString, new MemberData());
    }
    return memberBlockCountMap.get(dateAsString);
  }

  public void addBlocksOnDate(LocalDate localDate, Long count, UUID uuid) {
    getMemberData(localDate).addData(uuid, count);
  }

  public void setBlocksOnDate(LocalDate localDate, Long count, UUID uuid) {
    getMemberData(localDate).setData(uuid, count);
  }

  public void subtractBlocksOnDate(LocalDate localDate, Long count, UUID uuid) {
    getMemberData(localDate).subtractData(uuid, count);
  }

  public long getTotalBlocksOnDate(LocalDate localDate) {
    return getMemberData(localDate).getTotal();
  }

  public long getBlocksOnDate(LocalDate localDate, UUID uuid) {
    return getMemberData(localDate).getData(uuid);
  }

  public LinkedHashMap<LocalDate, MemberData> getMemberBlockCountMap() {
    LinkedHashMap<LocalDate, MemberData> convertedMap = new LinkedHashMap<>();
    for (Map.Entry<String, MemberData> entry : memberBlockCountMap.entrySet()) {
      convertedMap.put(localDateFromString(entry.getKey()), entry.getValue());
    }
    return convertedMap;
  }

  public void setMemberBlockCountMap(LinkedHashMap<LocalDate, MemberData> memberBlockCountMap) {
    this.memberBlockCountMap.clear();
    for (Map.Entry<LocalDate, MemberData> entry : memberBlockCountMap.entrySet()) {
      this.memberBlockCountMap.put(localDateToString(entry.getKey()), entry.getValue());
    }
  }

  public void setRawMemberBlockCountMap(LinkedHashMap<String, MemberData> memberBlockCountMap) {
    this.memberBlockCountMap.clear();
    this.memberBlockCountMap.putAll(memberBlockCountMap);
  }
}
