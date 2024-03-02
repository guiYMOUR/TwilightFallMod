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

public class DarkTree extends GrowAble {
    public float flowerTimer = 6 * 60;
    public int flowerAmount = 2;

    public Block darkFlower;
    public Effect flowerEffect = Fx.none;
    public Sound flowerSound = Sounds.mud;

    public boolean evn = false;

    public DarkTree(String name) {
        super(name);
        size = 3;

        breakEffect = new Effect(23, e -> {
            float scl = Math.max(e.rotation, 1);
            color(TFPal.darkEng);
            randLenVectors(e.id, 9, 19f * e.finpow() * scl, (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 3.5f * scl + 0.3f));
        }).layer(Layer.debris);
        breakSound = destroySound = Sounds.rockBreak;
        destroyEffect = new Effect(23, e -> {
            color(TFPal.darkEng);
            randLenVectors(e.id, 12, size * 12 * e.finpow(), (x, y) -> Fill.square(e.x + x, e.y + y, e.fout() * 4.3f));
        }).layer(Layer.debris);
    }

    @Override
    public void init() {
        super.init();
        if(darkFlower == null) darkFlower = TFBlocks.darkFlower;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("flowerGrow", (DarkTreeBuild ent) -> new Bar(() ->
                Core.bundle.get("bar.twilight-fall-GROW-FLOWER"),
                () -> Pal.sapBullet,
                () -> ent.flower/flowerTimer
        ));
    }

    @Override
    public void setStats() {
        super.setStats();
        if(darkFlower == null) return;
        stats.add(TFStats.result, tb -> {
            tb.row();
            tb.table(Styles.grayPanel, t -> {
                t.table(c -> {
                    c.add(new ItemImage(darkFlower.uiIcon, flowerAmount)).pad(4f);
                    c.add(darkFlower.localizedName + "\n" + "[lightgray]" + Strings.autoFixed(flowerAmount / (flowerTimer / 60f), 2) + StatUnit.perSecond.localized()).padLeft(2).padRight(5).style(Styles.outlineLabel);
                }).fill().padTop(5).padBottom(5);
                t.row();
                t.add(Strings.autoFixed(flowerTimer / 60, 2) + StatUnit.perSecond.localized()).left().pad(4f);
            });
        });

    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Drawf.dashSquare(Pal.accent, x, y, (size + 2) * tilesize);
    }

    public class DarkTreeBuild extends GrowAbleBuild{
        public final int fz = (size + 1) * 4;
        public int[] edgeX = new int[fz];
        public int[] edgeY = new int[fz];
        public float flower = 0;
        public boolean inited = false;

        @Override
        public void updateTile() {
            updateSpeed();
            if(!inited){
                int tx = (tileX() - Mathf.ceil(size / 2f)),
                        ty = (tileY() - Mathf.ceil(size / 2f));
                int s = 0;
                for (var d : Geometry.d4) {
                    for (int i = 0; i < size + 1; i++) {
                        edgeX[s] = tx;
                        edgeY[s] = ty;
                        tx += d.x;
                        ty += d.y;
                        s++;
                    }
                }
                inited = true;
            }
            if (size % 2 == 0) return;
            if(grow < (growTime + baseGrow)) grow += speedDelta();
            else {
                grow = growTime + baseGrow + 1;
                flower = Math.min(flower + speedDelta(), flowerTimer);
                if(flower >= flowerTimer){
                    for(int i = 0; i < flowerAmount; i++){
                        int pos = Mathf.random(fz - 1);
                        int l = pos, r = pos;
                        Tile t = world.tile(edgeX[pos], edgeY[pos]);
                        while (t != null && !t.block().isAir() && l >= 0){
                            t = world.tile(edgeX[l], edgeY[l]);
                            l--;
                        }
                        while (t != null && !t.block().isAir() && r < fz){
                            t = world.tile(edgeX[r], edgeY[r]);
                            r++;
                        }
                        if(t != null && t.block().isAir()){
                            Call.setTile(t, darkFlower, evn ? Team.derelict : team, 0);
                            if(flowerEffect != Fx.none) flowerEffect.at(t);
                            if(flowerSound != Sounds.none) flowerSound.at(t);
                            flower = 0;
                        }
                    }
                }
            }
        }

        @Override
        public void drawSelect(){
            super.drawSelect();

            Drawf.dashSquare(Pal.accent, x, y, (size + 2) * tilesize);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(flower);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            flower = read.f();
        }
    }
}
