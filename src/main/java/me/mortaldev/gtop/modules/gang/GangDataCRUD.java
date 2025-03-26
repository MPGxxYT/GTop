package me.mortaldev.gtop.modules.gang;

import java.util.HashMap;
import me.mortaldev.crudapi.CRUD;
import me.mortaldev.gtop.Main;
import me.mortaldev.gtop.modules.gang.adapters.DateBlockCountMapDeserializer;

public class GangDataCRUD extends CRUD<GangData> {
  private static final String PATH = Main.getInstance().getDataFolder() + "/gangData/";

  private GangDataCRUD() {}

  public static GangDataCRUD getInstance() {
    return Singleton.INSTANCE;
  }

  @Override
  public Class<GangData> getClazz() {
    return GangData.class;
  }

  @Override
  public HashMap<Class<?>, Object> getTypeAdapterHashMap() {
    return new HashMap<>() {
      {
        put(GangData.class, new DateBlockCountMapDeserializer());
      }
    };
  }

  @Override
  public String getPath() {
    return PATH;
  }

  private static class Singleton {
    private static final GangDataCRUD INSTANCE = new GangDataCRUD();
  }
}
