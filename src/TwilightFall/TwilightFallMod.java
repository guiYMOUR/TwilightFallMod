package TwilightFall;

import TwilightFall.contents.TFBlocks;
import TwilightFall.contents.TFItems;
import TwilightFall.contents.TFLiquids;
import TwilightFall.contents.TFStatusEffects;
import arc.Core;
import arc.graphics.Color;
import arc.util.Log;
import mindustry.mod.Mod;
import mindustry.mod.Mods;

import static mindustry.Vars.*;

public class TwilightFallMod extends Mod {
    public static String ModName = "twilight-fall";
    public static String name(String add){
        return ModName + "-" + add;
    }

    public TwilightFallMod(){
        Log.info("Twilight Fall mod loaded...");
    }

    public void setColorName(){
        Mods.LoadedMod mod = mods.locateMod(ModName);
        String st = Core.bundle.get("mod.displayName");
        StringBuilder fin = new StringBuilder();

        for(int i = 0; i < st.length(); i++){
            String s = String.valueOf(st.charAt(i));
            Color c = Color.valueOf("8c78cc").shiftHue(i * (int)(80f/st.length()));
            int ci = c.rgb888();
            String ct = Integer.toHexString(ci);
            String fct = "[" + "#" + ct + "]";
            fin.append(fct).append(s);
        }
        mod.meta.displayName = fin.toString();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void loadContent() {
        setColorName();
        TFStatusEffects.load();
        TFItems.load();
        TFLiquids.load();
        TFBlocks.load();
    }
}
