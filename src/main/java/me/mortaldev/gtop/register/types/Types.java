package me.mortaldev.gtop.register.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import net.brcdev.gangs.gang.Gang;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "NullableProblems"})
public class Types {

  static {
    Classes.registerClass(
        new ClassInfo<>(Gang.class, "gang")
            .user("gang")
            .name("Gang")
            .description("Represents a gang in connection with the GangsPlus Plugin")
            .examples("%player%'s gang")
            .defaultExpression(new EventValueExpression<>(Gang.class))
            .parser(
                new Parser<>() {

                  @Override
                  public @Nullable Gang parse(String s, ParseContext context) {
                    return null;
                  }

                  @Override
                  public boolean canParse(ParseContext context) {
                    return false;
                  }

                  @Override
                  public String toString(Gang gang, int i) {
                    return gang.getName();
                  }

                  @Override
                  public String toVariableNameString(Gang gang) {
                    return "GANG-" + gang.getName();
                  }
                }));
  }
}
