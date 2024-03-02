package TwilightFall.world.blocks.product;

import TwilightFall.contents.TFFx;
import TwilightFall.world.blocks.environment.SolidResources;
import TwilightFall.world.blocks.environment.DarkTree;
import TwilightFall.world.meta.TFStats;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.util.Eachable;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.ui.ItemImage;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.*;
import static arc.math.geom.Geometry.*;

public class Harvester extends Block {
    public float harvestTime = 5 * 60f;
    public TextureRegion[] placeRegion = new TextureRegion[4];
    public TextureRegion bottomRegion;

    private final boolean[] rti = {true, true, false, false};

    public Color heatColor = Pal.engine;

    public Color harvestColor = Pal.sapBullet;
    public Effect harvestEffect = TFFx.harvestEffect;

    public Harvester(String name) {
        super(name);
        solid = true;
        update = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        hasItems = true;
        rotate = true;
        drawArrow = false;
    }

    @Override
    public boolean rotatedOutput(int x, int y) {
        return false;
    }

    @Override
    public void load() {
        super.load();
        if(Core.atlas == null) return;
        for(int i = 0; i < 4; i++){
            placeRegion[i] = Core.atlas.find(name + "-top-" + i);
        }
        bottomRegion = Core.atlas.find(name + "-bottom");
    }

    public Rect getRect(Rect rect, float x, float y, int rotation, int realSize){
        rect.setCentered(x, y, realSize * tilesize);
        int len = tilesize * (rti[rotation] ? realSize : 1);

        rect.height = rotation % 2 == 0 ? tilesize * realSize : tilesize;
        rect.width = rotation % 2 != 0 ? tilesize * realSize : tilesize;

        rect.x += len * d4x[rotation];
        rect.y += len * d4y[rotation];

        return rect;
    }

    public Rect getRect(Rect rect, float x, float y, int rotation){
        return getRect(rect, x, y, rotation, size);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Rect rect = getRect(Tmp.r1, x, y, rotation);

        Drawf.dashRect(valid ? Pal.accent : Pal.remove, rect);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        if(!rotate){
            super.drawPlanRegion(plan, list);
            return;
        }
        Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
        Draw.rect(placeRegion[plan.rotation], plan.drawx(), plan.drawy());
        Draw.color();
        Draw.scl();
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        Rect rect = getRect(Tmp.r2, tile.worldx(), tile.worldy(), rotation, size + 2);
        return indexer.eachBlock(team, rect, b -> b instanceof DarkTree.DarkTreeBuild, b -> {});
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{placeRegion[0]};
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("harvestTimer", (HarvesterBuild ent) -> new Bar(() ->
                Core.bundle.get("bar.twilight-fall-harvestTimer"),
                () -> Pal.accent,
                () -> ent.harT/harvestTime
        ));
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(TFStats.harvest, table -> {
            var bs = content.blocks().select(b -> b instanceof SolidResources);
            table.row();
            table.table(c -> {
                int i = 0;
                for(Block block : bs){
                    c.table(Styles.grayPanel, b -> {
                        b.table(info -> {
                            info.left();
                            info.image(block.uiIcon).size(40).pad(2f).left().scaling(Scaling.fit);
                            info.row();
                            info.add(block.localizedName).left();
                        }).grow().pad(5);
                        b.table(item -> {
                            for(int j = 1; j <= block.requirements.length; j++){
                                var is = block.requirements[j - 1];
                                item.add(new ItemImage(is)).pad(8f);

                                if(j % 3 == 0) item.row();
                            }
                        }).grow().pad(5).left();
                    }).growX().pad(5);
                    if(++i % 2 == 0) c.row();
                }
            }).growX().colspan(table.getColumns());

            table.row();
            table.add(Core.bundle.get("stat.twilight-fall-base-harvest") + Strings.autoFixed(harvestTime/60, 2) + StatUnit.seconds.localized()).left();
        });
    }

    public class HarvesterBuild extends Building{
        public float harT = 0;
        public Rect rect = new Rect();

        public Point2[] face = new Point2[size];

        @Override
        public void updateTile() {
            if(face[0] == null) updateLasers();

            harT = Math.min(harvestTime, harT + edelta());
            if(items.total() < itemCapacity && harT >= harvestTime){
                harvest();
            }

            dumpAccumulate();
        }

        protected void updateLasers(){
            for(int i = 0; i < size; i++){
                if(face[i] == null) face[i] = new Point2();
                nearbySide(tileX(), tileY(), rotation, i, face[i]);
            }
        }

        protected void harvest(){
            boolean has = false;
            for(int p = 0; p < size; p++) {
                Point2 f = face[p];
                int rx = f.x, ry = f.y;
                Tile flower = world.tile(rx, ry);
                if(flower != null && flower.block() instanceof SolidResources df && (flower.team() == team || flower.team() == Team.derelict)){
                    ItemStack[] is = df.requirements;
                    if(is.length > 0) for(var ie : is){
                        items.add(ie.item, ie.amount);
                    }
                    df.breakEffect.at(flower.worldx(), flower.worldy(), harvestColor);
                    df.breakSound.at(flower);
                    Call.removeTile(flower);
                    has = true;
                }
            }

            if(has){
                harT = 0;
                harvestEffect.at(x, y, 0, harvestColor, getRect(rect, x, y, rotation));
            }
        }

        @Override
        public void draw() {
            Draw.rect(bottomRegion, x, y);
            Draw.rect(placeRegion[rotation], x, y);
            Draw.color(heatColor);
            Draw.alpha(harT/harvestTime);
            Draw.rect(Core.atlas.find(name + "-heat"), x, y);
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            Drawf.dashRect(Pal.accent, getRect(rect, x, y, rotation));
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(harT);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            harT = read.f();
        }
    }
}
