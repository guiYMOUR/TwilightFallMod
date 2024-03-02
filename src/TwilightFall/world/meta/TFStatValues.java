package TwilightFall.world.meta;

import TwilightFall.contents.TFIcons;
import TwilightFall.contents.TFPal;
import arc.Core;
import arc.util.Strings;
import mindustry.ui.Styles;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;

public class TFStatValues {
    public static StatValue consumeDark(float amount, boolean smooth, float timePeriod){
        return table -> {
            if(smooth) table.add(new displayImage(TFIcons.dark, Core.bundle.get("stat.twilight-fall.consDarkEngSec"), TFPal.darkEng));
            else table.add(new displayImage(TFIcons.dark, amount, TFPal.darkEng));

            table.add(TFIcons.darkName + "\n" + "[lightgray]" + Strings.autoFixed(amount / ((smooth ? 60 : timePeriod) / 60f), 2) + StatUnit.perSecond.localized()).padLeft(2).padRight(5).style(Styles.outlineLabel);
        };
    }
}
