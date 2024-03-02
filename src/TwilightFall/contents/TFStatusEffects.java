package TwilightFall.contents;

import mindustry.type.StatusEffect;

public class TFStatusEffects {
    public static StatusEffect pierce;

    public static void load(){
        pierce = new StatusEffect("pierce"){{
            healthMultiplier = 0.7f;
        }};
    }
}
