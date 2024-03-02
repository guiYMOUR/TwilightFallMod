package TwilightFall.contents;

import arc.graphics.Color;
import mindustry.type.Item;

public class TFItems {
    public static Item
            chromium, cobalt, gold, DarkEng, DarkPetal;

    public static void load(){
        chromium = new Item("chromium", Color.valueOf("ffb5b5")){{
            cost = 1.3f;
            hardness = 2;
        }};
        cobalt = new Item("cobalt", Color.valueOf("A1B1FF")){{
            cost = 1.4f;
            hardness = 3;
        }};
        gold = new Item("gold", Color.gold){{
            cost = 1.2f;
            hardness = 4;
        }};

        DarkEng = new Item("dark-eng", Color.valueOf("9e78dc")){{
            cost = 1.5f;
            radioactivity = 0.5f;
            explosiveness = 0.2f;
        }};

        DarkPetal = new Item("dark-petal", Color.pink){{
            flammability = 0.1f;
        }};
    }
}
