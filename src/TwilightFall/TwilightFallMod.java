package TwilightFall;

import TwilightFall.contents.*;
import TwilightFall.ui.wikiDialogs;
import TwilightFall.ui.wikiList;
import TwilightFall.wiki.Wiki;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import arc.util.Log;
import mindustry.gen.Icon;
import mindustry.gen.Iconc;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import mindustry.ui.dialogs.SettingsMenuDialog;
import mindustry.ui.fragments.MenuFragment;

import static mindustry.Vars.*;

public class TwilightFallMod extends Mod {
    public static String ModName = "twilight-fall";
    public static String name(String add){
        return ModName + "-" + add;
    }
    public static String toText(String str){
        return Core.bundle.format(str);
    }
    public static wikiDialogs wiki;
    public static wikiList wikiList;

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

    private static void toShowWiki() {
        wikiList.show(wiki);
    }

    @Override
    public void init() {
        if(ui != null) {
            wiki = new wikiDialogs();
            wikiList = new wikiList();

            wikiList.reset();
            Wikis.initWiki();

            ui.menufrag.addButton(new MenuFragment.MenuButton(Core.bundle.get("tf-show-wiki"), new TextureRegionDrawable(Core.atlas.find(name("dark"))), TwilightFallMod::toShowWiki));
        }
    }

    @Override
    public void loadContent() {
        setColorName();
        TFStatusEffects.load();
        TFItems.load();
        TFLiquids.load();
        TFUnitTypes.load();
        TFBlocks.load();
    }
}
