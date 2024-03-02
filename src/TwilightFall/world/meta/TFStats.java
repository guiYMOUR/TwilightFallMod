package TwilightFall.world.meta;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class TFStats {
    public static StatCat
        growCat = new StatCat("twilight-fall-growCat");

    public static Stat
        grow = new Stat("twilight-fall-grow", growCat),
        result = new Stat("twilight-fall-result", growCat),
        harvest = new Stat("twilight-fall-harvest", StatCat.crafting);
}
