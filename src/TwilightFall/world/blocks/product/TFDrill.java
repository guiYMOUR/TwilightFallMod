package TwilightFall.world.blocks.product;

import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Time;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Layer;
import mindustry.world.blocks.production.Drill;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

public class TFDrill extends Drill {
    public DrawBlock drawer = new DrawDefault();

    public TFDrill(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.icons(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    public class TFDrillBuild extends DrillBuild{

        @Override
        public void draw() {
            drawer.draw(this);
            if(drawRim){
                float z = Draw.z();
                Draw.color(heatColor);
                Draw.z(Layer.effect);
                Draw.alpha(warmup * 0.6f * (1f - 0.3f + Mathf.absin(Time.time, 3f, 0.3f)));
                Draw.blend(Blending.additive);
                Draw.rect(rimRegion, x, y);
                Draw.blend();
                Draw.color();
                Draw.z(z);
            }
            if(dominantItem != null && drawMineItem){
                Draw.color(dominantItem.color);
                Draw.rect(itemRegion, x, y);
                Draw.color();
            }
        }

        @Override
        public float totalProgress() {
            return timeDrilled;
        }
    }
}
