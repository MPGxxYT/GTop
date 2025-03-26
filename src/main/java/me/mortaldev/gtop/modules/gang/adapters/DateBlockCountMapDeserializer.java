package me.mortaldev.gtop.modules.gang.adapters;

import com.google.gson.*;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.MemberData;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class DateBlockCountMapDeserializer implements JsonDeserializer<GangData> {

  /**
   * Deserialize a JSON object into a GangData object.
   *
   * @param json a JSON object representing a GangData object
   * @param typeOfT the type of the object to be deserialized
   * @param context the context for deserialization
   * @return a GangData object
   * @throws JsonParseException if the JSON object cannot be deserialized
   */
  @Override
  public GangData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    // Get the gang name
    String gangName = jsonObject.get("gangName").getAsString();

    // Get the all time counter
    long allTimeCounter = jsonObject.get("allTimeCounter").getAsLong();

    // Get the banner
    String banner = jsonObject.get("banner").getAsString();

    // Get the member block count map
    LinkedHashMap<String, MemberData> dateBlockCountMap = new LinkedHashMap<>();
    JsonObject memberBlockCountMap = jsonObject.getAsJsonObject("memberBlockCountMap");
    if (memberBlockCountMap == null) {
      // If the member block count map is missing, use the date block count map
      JsonObject dateBlockCountMapJson = jsonObject.getAsJsonObject("dateBlockCountMap");
      for (Map.Entry<String, JsonElement> entry : dateBlockCountMapJson.entrySet()) {
        String date = entry.getKey();
        long count = entry.getValue().getAsLong();
        dateBlockCountMap.put(date, new MemberData().addData(count));
      }
    } else {
      // If the member block count map is present, use it
      for (Map.Entry<String, JsonElement> entry : memberBlockCountMap.entrySet()) {
        String date = entry.getKey(); // date
        JsonObject memberDataJson = entry.getValue().getAsJsonObject(); // map value of (uuid, long)
        MemberData memberData = new MemberData();
        for (Map.Entry<String, JsonElement> memberEntry : memberDataJson.entrySet()) {
          for (Map.Entry<String, JsonElement> idLongEntry :
              memberEntry.getValue().getAsJsonObject().entrySet()) {
            String uuid = idLongEntry.getKey();
            long count = idLongEntry.getValue().getAsLong();
            memberData.addData(UUID.fromString(uuid), count);
          }
        }
        dateBlockCountMap.put(date, memberData);
      }
    }

    // Create a new GangData object
    GangData gangData = new GangData(gangName);
    gangData.setAllTimeCounter(allTimeCounter);
    gangData.setRawBanner(banner);
    gangData.setRawMemberBlockCountMap(dateBlockCountMap);

    return gangData;
  }
}
