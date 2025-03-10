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
public class ExrGangsBlocks extends SimplePropertyExpression<Gang, Long> {

  static {
    register(ExrGangsBlocks.class, Long.class, "gang['s] blocks", "gang");
  }

  @Override
  public @Nullable Long convert(Gang gang) {
    String name = gang.getName();
    GangData gangData = GangManager.getInstance().getByID(name).orElseThrow();
    return gangData.getBlocksCountOnDate(GangManager.getInstance().todayDate());
  }

  @Override
  protected Long[] get(Event event, Gang[] source) {
    String name = source[0].getName();
    GangData gangData = GangManager.getInstance().getByID(name).orElseThrow();
    return new Long[] {gangData.getBlocksCountOnDate(GangManager.getInstance().todayDate())};
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
      case ADD ->
          gangData.setBlocksCountOnDate(
              GangManager.getInstance().todayDate(),
              gangData.getBlocksCountOnDate(GangManager.getInstance().todayDate())
                  + (Long) delta[0]);
      case SET ->
          gangData.setBlocksCountOnDate(GangManager.getInstance().todayDate(), (Long) delta[0]);
      case REMOVE -> {
        long amount =
            gangData.getBlocksCountOnDate(GangManager.getInstance().todayDate()) - (Long) delta[0];
        if (amount < 0) {
          amount = 0;
        }
        gangData.setBlocksCountOnDate(GangManager.getInstance().todayDate(), amount);
      }
      default -> gangData.setBlocksCountOnDate(GangManager.getInstance().todayDate(), 0L);
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
