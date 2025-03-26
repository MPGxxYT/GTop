package me.mortaldev.gtop.register.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import java.time.LocalDate;
import java.util.UUID;
import javax.annotation.Nullable;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import net.brcdev.gangs.GangsPlusApi;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@SuppressWarnings({"NullableProblems"})
public class ExrGangsBlocks extends SimplePropertyExpression<Player, Long> {

  static {
    register(ExrGangsBlocks.class, Long.class, "gang['s] blocks", "player");
  }

  @Override
  public @Nullable Long convert(Player player) {
    String name = GangsPlusApi.getPlayersGang(player).getName();
    GangData gangData = GangManager.getInstance().getByID(name).orElseThrow();
    return gangData.getBlocksOnDate(GangManager.getInstance().todayDate(), player.getUniqueId());
  }

  @Override
  protected Long[] get(Event event, Player[] source) {
    String name = GangsPlusApi.getPlayersGang(source[0]).getName();
    GangData gangData = GangManager.getInstance().getByID(name).orElseThrow();
    return new Long[] {gangData.getBlocksOnDate(GangManager.getInstance().todayDate(), source[0].getUniqueId())};
  }

  @Override
  public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
    if (mode == ChangeMode.REMOVE
        || mode == ChangeMode.ADD
        || mode == ChangeMode.SET
        || mode == ChangeMode.RESET
        || mode == ChangeMode.DELETE) {
      return CollectionUtils.array(Long.class);
    }
    return null;
  }

  @Override
  public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
    Player[] players = getExpr().getArray(event);
    UUID uuid = players[0].getUniqueId();
    String name = GangsPlusApi.getPlayersGang(players[0]).getName();
    GangData gangData = GangManager.getInstance().getByID(name).orElseThrow();
    LocalDate todayDate = GangManager.getInstance().todayDate();
    switch (mode) {
      case ADD -> gangData.addBlocksOnDate(todayDate, (Long) delta[0], uuid);
      case SET -> gangData.setBlocksOnDate(todayDate, (Long) delta[0], uuid);
      case REMOVE -> gangData.subtractBlocksOnDate(todayDate, (Long) delta[0], uuid);
      default -> gangData.setBlocksOnDate(todayDate, 0L, uuid);
    }
    GangManager.getInstance().update(gangData);
  }

  @Override
  protected String getPropertyName() {
    return "gang's blocks";
  }

  @Override
  public Class<? extends Long> getReturnType() {
    return Long.class;
  }
}
