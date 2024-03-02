package TwilightFall.contents;

import arc.graphics.Color;
import mindustry.content.StatusEffects;
import mindustry.type.Liquid;

public class TFLiquids {
    public static Liquid
            nutrient, lube;
    public static void load(){
        nutrient = new Liquid("nutrient", Color.rgb(238, 253, 212)){{
            heatCapacity = 0.5f;
            effect = StatusEffects.overclock;
            boilPoint = 0.5f;
            gasColor = Color.grays(0.7f);
        }};
        lube = new Liquid("lube", Color.rgb(252, 205, 32).a(0.8f)){{
            heatCapacity = 0.7f;
            temperature = 0.3f;
            boilPoint = 0.6f;
            viscosity = 0.7f;
            flammability = 0.2f;
            gasColor = Color.grays(0.7f);
            effect = StatusEffects.muddy;
        }};
    }
}
