package me.mortaldev.gtop.register.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.event.Event;

import javax.annotation.Nullable;


@SuppressWarnings({"NullableProblems", "unchecked"})
public class ExrGangsAllTimeBlocks extends SimplePropertyExpression<Gang, Long> {

  static {
    register(ExrGangsAllTimeBlocks.class, Long.class, "gang['s] all[ ]time blocks", "gang");
  }

  @Override
  public @Nullable Long convert(Gang gang) {
    GangData gangData = GangManager.getGangData(gang);
    if (gangData == null) {
      return null;
    }
    return gangData.getAllTimeCounter();
  }

  @Override
  protected Long[] get(Event event, Gang[] source) {
    GangData gangData = GangManager.getGangData(source[0]);
    if (gangData == null) {
      return null;
    }
    return new Long[]{gangData.getAllTimeCounter()};
  }

  @Override
  public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
    if (mode == ChangeMode.REMOVE || mode == ChangeMode.ADD || mode == ChangeMode.SET || mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
      return CollectionUtils.array(Long.class);
    }
    return null;
  }

  @Override
  public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
    Gang[] gang = getExpr().getArray(event);
    GangData gangData = GangManager.getGangData(gang[0]);
    if (gangData == null) {
      return;
    }
    switch (mode) {
      case ADD -> gangData.setAllTimeCounter(gangData.getAllTimeCounter() + (Long) delta[0]);
      case SET -> gangData.setAllTimeCounter((Long) delta[0]);
      case REMOVE -> {
        long amount = gangData.getAllTimeCounter() - (Long) delta[0];
        if (amount < 0) {
          amount = 0;
        }
        gangData.setAllTimeCounter(amount);
      }
      default -> gangData.setAllTimeCounter(0L);
    }
  }

  @Override
  protected String getPropertyName() {
    return "gang's all time blocks";
  }

  @Override
  public Class<? extends Long> getReturnType() {
    return Long.class;
  }
}
