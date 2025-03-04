package me.mortaldev.gtop.register.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class ExrPlayerGang extends SimpleExpression<Gang> {

  static {
    Skript.registerExpression(ExrPlayerGang.class, Gang.class, ExpressionType.PROPERTY, "%player%['s] gang");
  }

  private Expression<Player> player;

  @SuppressWarnings("NullableProblems")
  @Override
  protected Gang[] get(Event event) {
    return new Gang[]{GangsPlusApi.getPlayersGang(player.getSingle(event))};
  }

  @SuppressWarnings({"NullableProblems", "unchecked"})
  @Override
  public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
    this.player = (Expression<Player>) exprs[0];
    return true;
  }

  @Override
  public boolean isSingle() {
    return true;
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public Class<? extends Gang> getReturnType() {
    return Gang.class;
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public String toString(Event event, boolean b) {
    return player.getSingle(event) + "'s gang";
  }

}
