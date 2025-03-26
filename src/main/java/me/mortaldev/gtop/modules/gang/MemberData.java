package me.mortaldev.gtop.modules.gang;

import java.util.HashMap;
import java.util.UUID;

public class MemberData {
  private static final UUID DEFAULT_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
  private final HashMap<UUID, Long> memberBlockCountMap = new HashMap<>();

  public static UUID getDefaultUuid() {
    return DEFAULT_UUID;
  }

  /**
   * Add a certain amount of blocks to the total count of blocks a player has mined in this gang.
   *
   * @param uuid the UUID of the player
   * @param amount the amount of blocks to add
   * @return this member data
   */
  public MemberData addData(UUID uuid, Long amount) {
    Long currentData = memberBlockCountMap.getOrDefault(uuid, 0L);
    memberBlockCountMap.put(uuid, currentData + amount);
    return this;
  }

  /**
   * Adds a certain amount of blocks to the total count of blocks the default player
   * (00000000-0000-0000-0000-000000000000) has mined in this gang.
   *
   * @param amount the amount of blocks to add
   * @return this member data
   */
  public MemberData addData(Long amount) {
    addData(DEFAULT_UUID, amount);
    return this;
  }

  public MemberData setData(UUID uuid, Long amount) {
    memberBlockCountMap.put(uuid, amount);
    return this;
  }

  /**
   * Subtracts a certain amount of blocks from the total count of blocks a player has mined in this
   * gang.
   *
   * @param uuid the UUID of the player
   * @param amount the amount of blocks to subtract
   * @return this member data
   */
  public MemberData subtractData(UUID uuid, Long amount, boolean allowNegative) {
    Long currentData = memberBlockCountMap.getOrDefault(uuid, 0L);
    long newAmount = currentData - amount;
    if (newAmount < 0 && !allowNegative) {
      newAmount = 0;
    }
    memberBlockCountMap.put(uuid, newAmount);
    return this;
  }

  public MemberData subtractData(UUID uuid, Long amount) {
    return subtractData(uuid, amount, false);
  }

  public void removeData(UUID uuid) {
    memberBlockCountMap.remove(uuid);
  }

  public HashMap<UUID, Long> getMap() {
    return memberBlockCountMap;
  }

  public Long getData(UUID uuid) {
    return memberBlockCountMap.getOrDefault(uuid, 0L);
  }

  public Long getTotal() {
    Long total = 0L;
    for (Long value : memberBlockCountMap.values()) {
      total += value;
    }
    return total;
  }
}
