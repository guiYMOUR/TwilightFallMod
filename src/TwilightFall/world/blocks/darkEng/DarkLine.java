package TwilightFall.world.blocks.darkEng;

import TwilightFall.contents.TFBlocks;
import TwilightFall.contents.TFPal;
import TwilightFall.world.TFBlock;
import arc.Core;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.TargetPriority;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.input.Placement;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.DirectionBridge;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;


public class DarkLine extends DarkBlockBase {
    public float maxEng = 200;
    public TextureRegion[][] regions;
    public TextureRegion[] lights;
    public Effect flowEffect = new Effect(30, e -> {
        Draw.alpha(e.fout());
        Draw.color(TFPal.darkEng, Color.white, e.fin());
        Angles.randLenVectors(e.id, 2, 4 * e.finpow(), e.rotation, 180, (x, y) ->{
            Fill.circle(e.x + x, e.y + y, 4 * e.finpow());
        });
    });

    public @Nullable
    Block junctionReplacement, bridgeReplacement;

    public DarkLine(String name) {
        super(name);

        update = true;
        size = 1;
        destructible = true;
        rotate = true;
        priority = TargetPriority.transport;
    }

    @Override
    public void init() {
        super.init();

        if(junctionReplacement == null) junctionReplacement = TFBlocks.darkJunction;
        if(bridgeReplacement == null || !(bridgeReplacement instanceof DarkBridge)) bridgeReplacement = TFBlocks.darkBridge;
    }

    @Override
    public void load() {
        super.load();

        if(Core.atlas == null) return;
        regions = new TextureRegion[4][4];
        lights = new TextureRegion[4];
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++) regions[i][j] = Core.atlas.find(name + "-" + i + "-" + j);
            lights[i] = Core.atlas.find(name + "-light-" + i);
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.abilities, Core.bundle.format("stat.twilight-fall.maxEng", maxEng));
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("darkEng", (DarkLineBuild ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.darkAmount", ent.eng),
                () -> Color.valueOf("9e78dc"),
                () -> ent.eng > 0 ? 1 : 0
        ));
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        if(!rotate){
            super.drawPlanRegion(plan, list);
            return;
        }
        Draw.rect(regions[0][plan.rotation%2], plan.drawx(), plan.drawy());
        Draw.color();
        Draw.scl();
    }

    @Override
    protected TextureRegion[] icons() {
        if(!rotate) return super.icons();
        return new TextureRegion[]{regions[0][0]};
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){
        if(bridgeReplacement == null) return;

        Placement.calculateBridges(plans, (DarkBridge) bridgeReplacement);
    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans){
        if(junctionReplacement == null) return this;

        Boolf<Point2> cont = p -> plans.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof DarkLine || req.block instanceof DarkJunction));
        return cont.get(Geometry.d4(req.rotation)) &&
                cont.get(Geometry.d4(req.rotation - 2)) &&
                req.tile() != null &&
                req.tile().block() instanceof DarkLine &&
                Mathf.mod(req.tile().build.rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    public class DarkLineBuild extends Building implements DarkGraph, DarkConsumer {

        public float eng = 0f;
        public float[] side = new float[4];
        public IntSet cameFrom = new IntSet();
        public long lastUpdate = -1;

        @Override
        public void draw() {
            if(!rotate){
                super.draw();
                return;
            }
            int j = 0;
            IntSeq k = new IntSeq();
            for(int i = 0; i < 4; i++){
                Building b = nearby(i);
                if(checkLink(b)) {
                    j ++;
                    k.add(i);
                }
            }
            Draw.z(Layer.blockUnder);
            if(j == 0) Draw.rect(regions[0][rotation % 2], x, y);
            if(j == 1){
                if(k.contains((rotation + 2)%4)){
                    Draw.rect(regions[0][rotation % 2], x, y);
                }
                if((rotation == 0 && k.contains(1)) || (rotation == 1 && k.contains(0))) {
                    Draw.rect(regions[1][0], x, y);
                }
                if((rotation == 1 && k.contains(2)) || (rotation == 2 && k.contains(1))) {
                    Draw.rect(regions[1][1], x, y);
                }
                if((rotation == 2 && k.contains(3)) || (rotation == 3 && k.contains(2))) {
                    Draw.rect(regions[1][2], x, y);
                }
                if((rotation == 3 && k.contains(0)) || (rotation == 0 && k.contains(3))) {
                    Draw.rect(regions[1][3], x, y);
                }
            }
            if(j == 2){
                if((rotation == 0 && k.contains(1) && k.contains(2)) || (rotation == 1 && k.contains(0) && k.contains(2)) || (rotation == 2 && k.contains(0) && k.contains(1))){
                    Draw.rect(regions[2][0], x, y);
                }
                if((rotation == 1 && k.contains(2) && k.contains(3)) || (rotation == 2 && k.contains(1) && k.contains(3)) || (rotation == 3 && k.contains(1) && k.contains(2))){
                    Draw.rect(regions[2][1], x, y);
                }
                if((rotation == 2 && k.contains(0) && k.contains(3)) || (rotation == 3 && k.contains(0) && k.contains(2)) || (rotation == 0 && k.contains(2) && k.contains(3))){
                    Draw.rect(regions[2][2], x, y);
                }
                if((rotation == 3 && k.contains(1) && k.contains(0)) || (rotation == 0 && k.contains(1) && k.contains(3)) || (rotation == 1 && k.contains(0) && k.contains(3))){
                    Draw.rect(regions[2][3], x, y);
                }
            }
            if(j == 3) Draw.rect(regions[3][0], x, y);
            Draw.reset();
        }

        protected boolean checkLink(Building build){
            if(build != null && build.block instanceof DarkBlock) {
                if(build.block.rotate) return build instanceof DarkLineBuild && build.front() == this && front() != build;
                else return ((DarkBlock) (build.block)).outputDark() && front() != build;
            }
            return false;
        }

        @Override
        public void updateTile() {
            //超过临界时自爆（未完全）
            if(eng > maxEng) kill();

            updateDark();
        }

        public void updateDark(){
            if(lastUpdate == Vars.state.updateId) return;

            lastUpdate = Vars.state.updateId;

            eng = DarkBuildFunc.calculateEng(this, side, cameFrom);
        }

        @Override
        public float outputEng() {
            return eng;
        }

        @Override
        public int edge() {
            return 1;
        }

        @Override
        public void consumeDark(float d) {

        }

        @Override
        public float hasEng() {
            return eng;
        }
    }
}
