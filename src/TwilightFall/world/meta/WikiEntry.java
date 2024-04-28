package TwilightFall.world.meta;

import TwilightFall.TwilightFallMod;
import TwilightFall.contents.TFPal;
import TwilightFall.wiki.Wiki;
import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import mindustry.ui.Fonts;

import static mindustry.ui.Styles.*;

public class WikiEntry extends Table {
    public WikiEntry(Wiki wiki){
        button(wiki.displayName, new TextButton.TextButtonStyle(){{
            down = none;
            up = none;
            over = none;
            font = Fonts.def;
            fontColor = TFPal.wikiEntry;
            disabledFontColor = Color.gray;
        }}, () -> TwilightFallMod.wiki.show(wiki)).size(20, 10).tooltip(Core.bundle.get("wiki.twilight-fall-hovTxt")).margin(3).pad(6);
    }
}
