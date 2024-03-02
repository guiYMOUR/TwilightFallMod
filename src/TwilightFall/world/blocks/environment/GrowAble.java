package TwilightFall.world.blocks.environment;

import TwilightFall.contents.TFBlocks;
import TwilightFall.contents.TFPal;
import TwilightFall.world.meta.TFStats;
import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.core.UI;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.ui.ItemImage;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.*;

public class GrowAble extends Block {
    public float growTime = 30 * 60f;
    public float baseGrow = 10 * 60f;
    public float shadowOffset = -2f;
    public float shadowAlpha = 0.5f;
    public float shakeSel = 40, shakeMag = 0.12f;
    public int rotRad = 0;

    public GrowAble(String name) {
        super(name);
        hasShadow = false;
        noUpdateDisabled = false;
        update = true;

        rotate = false;
        canOverdrive = false;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(TFStats.grow, Core.bundle.format("stat.twilight-fall-growNeed", growTime/60));
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("treeGrow", (GrowAbleBuild ent) -> new Bar(() ->
                Core.bundle.get("bar.twilight-fall-GROWING"),
                () -> Pal.accent,
                () -> (ent.grow - baseGrow)/(growTime + 1)
        ));
    }

    public class GrowAbleBuild extends Building{
        public float grow = baseGrow;

        protected float speedUp = 1;
        protected float speedTimer = 0;

        public float speedDelta(){
            return Time.delta * speedUp;
        }

        public void updateSpeed(){
            speedTimer = Math.max(speedTimer - Time.delta, 0);
            if(speedTimer <= 0){
                speedUp = 1;
            }
        }

        public void applySpeed(float sp, float timer){
            speedUp = Math.max(sp, speedUp);
            speedTimer = Math.max(speedTimer, timer);
        }

        @Override
        public void updateTile() {
            updateSpeed();
            if(grow < (growTime + baseGrow)) grow += speedDelta();
            else {
                grow = growTime + baseGrow + 1;
            }
        }

        @Override
        public void draw() {
            float growUp = grow/(growTime + baseGrow + 1);
            float
                    x = tile.worldx(), y = tile.worldy(),
                    rot = Mathf.randomSeed(tile.pos(), 0, rotRad) * 90 + Mathf.sin(Time.time + x, 50f, 0.5f) + Mathf.sin(Time.time - y, 65f, 0.9f) + Mathf.sin(Time.time + y - x, 85f, 0.9f),
                    w = region.width * region.scl() * growUp, h = region.height * region.scl() * growUp;

            TextureRegion shad = variants == 0 ? customShadowRegion : variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))];

            Draw.color();
            Draw.alpha(shadowAlpha);
            if(shad.found()){
                Draw.z(Layer.power - 1);
                Draw.rect(shad, tile.worldx() + shadowOffset * growUp, tile.worldy() + shadowOffset * growUp, w, h, rot);
            }

            Draw.alpha(1);
            TextureRegion reg = variants == 0 ? region : variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))];

            Draw.z(Layer.power + 1);
            Draw.rectv(reg, x, y, w, h, rot, vec -> vec.add(
                    Mathf.sin(vec.y*3 + Time.time, shakeSel, shakeMag) + Mathf.sin(vec.x*3 - Time.time, 70, 0.8f),
                    Mathf.cos(vec.x*3 + Time.time + 8, shakeSel + 6f, shakeMag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50, 0.2f)
            ));
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(grow);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            grow = read.f();
        }
    }
}

