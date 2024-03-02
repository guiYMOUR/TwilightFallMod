package TwilightFall.world.meta;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.core.UI;
import mindustry.ui.Styles;

public class displayImage extends Stack {
    public displayImage(TextureRegion region, float amount, Color color){

        add(new Table(o -> {
            o.left();
            o.add(new Image(region)).color(color).size(32f).scaling(Scaling.fit);
        }));

        if(amount > 0){
            add(new Table(t -> {
                t.left().bottom();
                t.add(amount >= 1000 ? UI.formatAmount((int)amount) : Strings.autoFixed(amount, 2) + "").style(Styles.outlineLabel);
                t.pack();
            }));
        }
    }

    public displayImage(TextureRegion region, String st, Color color){

        add(new Table(o -> {
            o.left();
            o.add(new Image(region)).color(color).size(32f).scaling(Scaling.fit);
        }));

        if(st != null){
            add(new Table(t -> {
                t.left().bottom();
                t.add(st).fontScale(0.8f).style(Styles.outlineLabel);
                t.pack();
            }));
        }
    }
}
