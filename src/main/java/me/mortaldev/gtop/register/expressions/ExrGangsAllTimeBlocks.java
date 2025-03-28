package me.mortaldev.gtop.register.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import javax.annotation.Nullable;
import me.mortaldev.gtop.modules.gang.GangData;
import me.mortaldev.gtop.modules.gang.GangManager;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.event.Event;

@SuppressWarnings({"NullableProblems"})
public class ExrGangsAllTimeBlocks extends SimplePropertyExpression<Gang, Long> {

  static {
    register(ExrGangsAllTimeBlocks.class, Long.class, "gang['s] all[ ]time blocks", "gang");
  }

  @Override
  public @Nullable Long convert(Gang gang) {
    String name = gang.getName();
    GangData gangData = GangManager.getInstance().getByID(name).orElseThrow();
    return gangData.getAllTimeCounter();
  }

  @Override
  protected Long[] get(Event event, Gang[] source) {
    String name = source[0].getName();
    GangData gangData = GangManager.getInstance().getByID(name).orElseThrow();
    return new Long[] {gangData.getAllTimeCounter()};
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
    Gang[] gang = getExpr().getArray(event);
    String name = gang[0].getName();
    GangData gangData = GangManager.getInstance().getByID(name).orElseThrow();
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
    GangManager.getInstance().update(gangData);
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
