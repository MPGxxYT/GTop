package me.mortaldev.gtop.utils;

import me.mortaldev.gtop.records.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {


  /**
   * Replaces any special characters in the given string with underscores.
   *
   * @param string the string to be formatted
   * @return the formatted string with special characters replaced by underscores
   */
  public static String fileFormat(String string){
    return string.replaceAll("[^a-zA-Z0-9_-]", "_");
  }

  /**
   * Removes decoration tags from the given string.
   *
   * @param string the string from which to remove decoration tags
   * @return the string with decoration tags removed
   */
  public static String removeDecoration(String string) {
    StringBuilder editString = new StringBuilder(string);
    for (String key : Decorations.getKeys()) {
      key = "&" + key;
      editString.replace(0, editString.length(), editString.toString().replace(key, ""));
    }
    return editString.toString();
  }

  /**
   * Removes color tags from the given string.
   *
   * @param string the string from which to remove color tags
   * @return the string with color tags removed
   */
  public static String removeColors(String string) {
    StringBuilder editString = new StringBuilder(string);
    for (String key : Colors.getKeys()) {
      key = "&" + key;
      editString.replace(0, editString.length(), editString.toString().replace(key, ""));
    }
    return editString.toString().replaceAll("<#.{6}>", "");
  }

  /**
   * Serializes a Component object to a JSON string using GsonComponentSerializer.
   *
   * @param component The Component object to serialize.
   * @return The serialized JSON representation of the Component object.
   */
  public static String serializeComponent(Component component){
    return GsonComponentSerializer.gson().serialize(component);
  }

  public static String serializeComponent(String string){
    return serializeComponent(format(string));
  }

  /**
   * Deserializes a JSON string representation of a Component object using GsonComponentSerializer.
   *
   * @param string The JSON string to deserialize.
   * @return The deserialized Component object.
   */
  public static Component deserializeComponent(String string){
    return GsonComponentSerializer.gson().deserialize(string);
  }

  /**
   * Converts a Component object to a plain text string representation.
   *
   * @param component The Component object to convert.
   * @return The plain text string representation of the Component object.
   */
  public static String componentToString(Component component){
    return PlainTextComponentSerializer.plainText().serialize(component);
  }

  /**
   * Formats the given string using MiniMessage format tags and returns it as a Component object.
   *
   * @param str the string to be formatted
   * @return the formatted string as a Component object
   */
  public static Component format(String str) {
    return format(str, false);
  }

  /**
   * Formats the given string using MiniMessage format tags and returns it as a Component object.
   *
   * @param str the string to be formatted
   * @param disableReset whether to disable the reset tag or not
   * @return the formatted string as a Component object
   */
  public static Component format(String str, boolean disableReset) {
    String result = asString(str, disableReset);
    result = asParam(result);
    if (result.contains("§")) {
      result = result.replaceAll("§", "&");
    }
    return MiniMessage.miniMessage()
        .deserialize(result)
        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
  }

  // Welcome Home##My love!##sgt:/home ##ttp:Click Here
  // [EXTRA TEXT ] [ INPUT] [PAR][ARG  ] [PAR][   ARG  ]
  //             ||        ||          ||

   private static String asParam(String str) {
    if (str == null) {
      throw new IllegalArgumentException("Input string cannot be null.");
    }

    // Maps each cluster's start position to its key tag and value text.
    HashMap<Integer, Pair<String, String>> clusters = new HashMap<>();

    // Get the list of recognized keys from Types enum.
    List<String> keys = Arrays.stream(Types.getKeys()).toList();

    // Split the input string into potential clusters. Cluster may start with a key.
    String[] split = str.split("##");

    // Loop over potential clusters and store recognized ones in the map.
    for (int i = 0; i < split.length; i++) {
      addToClusters(i, split[i], keys, clusters);
    }

    // Holds the output string for each processed cluster.
    List<String> out = new ArrayList<>();
    // Placeholder for detected key in the last recognized cluster.
    String past_key = "";

    // Process each recognized cluster based on its key and formulate the output string.
    for (Map.Entry<Integer, Pair<String, String>> entry : clusters.entrySet()) {
      past_key = processClusterEntry(entry, past_key, clusters, out);
    }

    // Join all parts of the parameterized string together and return the result.
    return String.join("", out);
  }

  private static void addToClusters(
      int index, String str, List<String> keys, HashMap<Integer, Pair<String, String>> clusters) {
    String tag = "";
    String value = "";

    // If the string is at least 4 characters long,
    if (str != null && str.length() >= 4) {
      tag = str.substring(0, 4);
      value = str.substring(4);
    }
    // Put the entry in the clusters. If a recognized tag is found, use the tag and value, otherwise
    // use "text" as the tag
    if (keys.contains(tag)) {
      clusters.put(index, new Pair<>(tag, value));
    } else {
      clusters.put(index, new Pair<>("text", str != null ? str : ""));
    }
  }

  private static String processClusterEntry(
      Map.Entry<Integer, Pair<String, String>> entry,
      String past_text,
      HashMap<Integer, Pair<String, String>> clusters,
      List<String> out) {
    int index = entry.getKey();
    String tag = getValueFromEntry(entry, 'k');
    String v = getValueFromEntry(entry, 'v');

    // If the tag is "text", just build up the past_text.
    // If not, do the replacement according to the Types value.
    if (Objects.equals(tag, "text")) {
      if (!past_text.isEmpty()) {
        out.add(past_text);
      }
      past_text = v;
    } else {
      past_text = performTypeValueReplacement(tag, v, past_text);
    }

    // If this the last cluster, append `past_text` to `out`.
    if (clusters.size() == index + 1) {
      out.add(past_text);
    }

    return past_text;
  }

  // Helper function to get value from map entry's key or value.
  private static String getValueFromEntry(
      Map.Entry<Integer, Pair<String, String>> entry, char keyOrValue) {
    // If keyOrValue is 'k', get the key; otherwise, get the value.
    return keyOrValue == 'k' ? entry.getValue().first() : entry.getValue().second();
  }

  // Helper function to perform replacement based on the Types value.
  private static String performTypeValueReplacement(String tag, String value, String past_text) {
    // Retrieve the value associated with `tag` from the Types.
    String typeValue = "";
    if (Types.getTypeFromKey(tag) != null) {
      typeValue = Types.getTypeFromKey(tag).value;
    }
    // Perform replacement on `typeValue` and assign it to `past_text`.
    return typeValue.replace("#arg#", value).replace("#input#", past_text);
  }

  private static String asString(String str, boolean disableReset) {
    StringBuilder stringBuilder = new StringBuilder(str);
    stringBuilder.replace(0, stringBuilder.length(), str.replace("&nl", "<newline>"));

    // Parse and replace HTML-style hexadecimal color references.
    Pattern hexPattern = Pattern.compile("&#(.{6})");
    Matcher hexMatcher = hexPattern.matcher(str);
    while (hexMatcher.find()) {
      String hexCode = hexMatcher.group(1);
      stringBuilder.replace(
          0,
          stringBuilder.length(),
          stringBuilder.toString().replace("&#" + hexCode, "<#" + hexCode + ">"));
    }

    // Replace color format references
    for (Colors color : Colors.values()) {
      String key = "&" + color.getKey();
      String value =
          disableReset ? "<" + color.getValue() + ">" : "<reset><" + color.getValue() + ">";
      stringBuilder.replace(
          0, stringBuilder.length(), stringBuilder.toString().replace(key, value));
    }

    // Replace decoration format references
    for (Decorations decoration : Decorations.values()) {
      String key = "&" + decoration.getKey();
      String value = "<" + decoration.getValue() + ">";
      stringBuilder.replace(
          0, stringBuilder.length(), stringBuilder.toString().replace(key, value));
    }

    return stringBuilder.toString();
  }

  private enum Types {
    // CLICK ACTIONS
    CHANGE_PAGE("pge:", "<click:change_page:'#arg#'>#input#</click>"),
    COPY_TO_CLIPBOARD("cpy:", "<click:copy_to_clipboard:'#arg#'>#input#</click>"),
    OPEN_FILE("fle:", "<click:open_file:'#arg#'>#input#</click>"),
    OPEN_PAGE("url:", "<click:open_url:'#arg#'>#input#</click>"),
    RUN_COMMAND("cmd:", "<click:run_command:'#arg#'>#input#</click>"),
    SUGGEST_COMMAND("sgt:", "<click:suggest_command:'#arg#'>#input#</click>"),

    // HOVER
    SHOW_ENTITY("ent:", "<hover:show_entity:'#arg#'>#input#</hover>"),
    SHOW_ITEM("itm:", "<hover:show_item:'#arg#'>#input#</hover>"),
    SHOW_TEXT("ttp:", "<hover:show_text:'#arg#'>#input#</hover>"),

    // KEYBIND
    KEY("key:", "#input#<key:#arg#>"),

    // TRANSLATE
    // ex. ##lng:block.minecraft.diamond_block
    // ex. ##lng:commands.drop.success.single:'<red>1':'<blue>Stone'
    LANG("lng:", "#input#<lang:#arg#>"),

    // INSERT
    INSERT("ins:", "<insert:'#arg#'>#input#</insert>"),

    // RAINBOW
    // COLORS##rnb:##no colors
    RAINBOW("rnb:", "<rainbow>#input#</rainbow>"),

    // GRADIENT
    // colored##grd:#5e4fa2:#f79459##not colored
    // colored##grd:#5e4fa2:#f79459:red##not colored
    // colored##grd:green:blue##not colored
    GRADIENT("grd:", "<gradient:#arg#>#input#</gradient>"),

    // TRANSITION
    // colored##trn:[color1]:[color...]:[phase]##not colored
    // colored##trn:#00ff00:#ff0000:0##not colored
    TRANSITION("trn:", "<transition:#arg#>#input#</transition>"),

    // FONT
    FONT("fnt:", "<font:#arg#>#input#</font>"),

    // SELECTOR
    // Hello ##slt:@e[limit=5]##, I'm ##slt:@s##!
    SELECTOR("slt:", "#input#<selector:#arg#>"),

    // SCORE
    // ##score:_name_:_objective_##
    // You have won ##scr:rymiel:gamesWon/## games!
    SCORE("scr:", "#input#<score:#arg#>"),

    // NBT
    // ##nbt:block|entity|storage:id:path[:_separator_][:interpret]##
    // Your health is ##nbt:entity:'@s':Health/##
    NBT("nbt:", "#input#<nbt:#arg#>");

    private final String key;
    private final String value;

    Types(String key, String value) {
      this.key = key;
      this.value = value;
    }

    String getKey() {
      return key;
    }

    static String[] getKeys() {
      List<String> keys = new ArrayList<>();
      for (Types types : Types.values()) {
        keys.add(types.getKey());
      }
      return keys.toArray(new String[0]);
    }

    static Types getTypeFromKey(String string) {
      for (Types value : values()) {
        if (value.getKey().equals(string)) {
          return value;
        }
      }
      return null;
    }

    String getValue() {
      return value;
    }
  }

  private enum Decorations {
    BOLD("l", "b"),
    ITALIC("o", "em"),
    UNDERLINE("n", "u"),
    STRIKETHROUGH("m", "st"),
    OBFUSCATED("k", "obf"),
    RESET("r", "reset");

    private final String key;
    private final String value;

    Decorations(String key, String value) {
      this.key = key;
      this.value = value;
    }

    String getKey() {
      return key;
    }

    static String[] getKeys() {
      List<String> keys = new ArrayList<>();
      for (Decorations decorations : Decorations.values()) {
        keys.add(decorations.getKey());
      }
      return keys.toArray(new String[0]);
    }

    String getValue() {
      return value;
    }
  }

  private enum Colors {
    BLACK("0", "black"),
    DARK_BLUE("1", "dark_blue"),
    DARK_GREEN("2", "dark_green"),
    DARK_AQUA("3", "dark_aqua"),
    DARK_RED("4", "dark_red"),
    DARK_PURPLE("5", "dark_purple"),
    GOLD("6", "gold"),
    GREY("7", "gray"),
    DARK_GREY("8", "dark_gray"),
    BLUE("9", "blue"),
    GREEN("a", "green"),
    AQUA("b", "aqua"),
    RED("c", "red"),
    LIGHT_PURPLE("d", "light_purple"),
    YELLOW("e", "yellow"),
    WHITE("f", "white");

    private final String key;
    private final String value;

    Colors(String key, String value) {
      this.key = key;
      this.value = value;
    }

    String getKey() {
      return key;
    }

    static String[] getKeys() {
      List<String> keys = new ArrayList<>();
      for (Colors colors : Colors.values()) {
        keys.add(colors.getKey());
      }
      return keys.toArray(new String[0]);
    }

    String getValue() {
      return value;
    }
  }
}