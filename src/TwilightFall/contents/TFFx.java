package TwilightFall.contents;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static TwilightFall.TwilightFallMod.name;
import static arc.graphics.g2d.Draw.*;
import static arc.math.Angles.randLenVectors;
import static mindustry.content.Fx.rand;
import static mindustry.content.Fx.v;

public class TFFx {
    public static Effect harvestEffect = new Effect(24, e -> {
        if(!(e.data instanceof Rect rect)) return;
        Lines.stroke(3 * e.fout(), e.color);
        Lines.rect(rect);
    });
}
