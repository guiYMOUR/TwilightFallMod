package TwilightFall.world.blocks.darkEng;

import TwilightFall.contents.TFPal;
import TwilightFall.world.blocks.product.DarkCrafter;
import TwilightFall.world.meta.TFBlockGroup;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.core.Renderer;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.DirectionBridge;
import mindustry.world.blocks.distribution.ItemBridge;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class DarkBridge extends ItemBridge implements DarkBlock{

    public DarkBridge(String name) {
        super(name);
        hasItems = hasLiquids = false;
        range = 4;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("darkEng", (DarkBridgeBuild ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.darkAccept", ent.eng),
                () -> Color.valueOf("9e78dc"),
                () -> ent.eng > 0 ? 1 : 0
        ));
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        if(!(other instanceof DarkBlock)) return false;
        return other.replaceable && ((DarkBlock)other).group() == this.group() &&
                (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
    }

    @Override
    public boolean outputDark() {
        return true;
    }

    @Override
    public TFBlockGroup group() {
        return TFBlockGroup.dark;
    }

    public class DarkBridgeBuild extends ItemBridgeBuild implements DarkGraph, DarkConsumer{
        public float eng, secEng;
        public Seq<Building> froms = new Seq<>();

        @Override
        public void updateTile() {
            checkIncoming();

            //all reset
            secEng = 0;
            froms.clear();

            if(incoming.size > 0){
                for(int i : incoming.items){
                    Building build = world.build(i);
                    if(build instanceof DarkBridgeBuild){
                        froms.addUnique(build);
                        froms.addAll(((DarkBridgeBuild)build).froms);
                    }
                }
            }
            for(int i = 0; i < proximity.size; i++){
                Building b = proximity.get(i);
                if(b instanceof DarkBridgeBuild && ((DarkBridgeBuild)b).link != pos() && link == -1 && froms.contains(b)) {
                    eng = secEng;
                    return;
                }
            }

            Tile other = world.tile(link);
            if(other != null && other.build != null && froms.contains(other.build)){
                eng = secEng;
                return;
            }

            if(incoming.size > 0){
                for(int i : incoming.items){
                    Building build = world.build(i);
                    if(build instanceof DarkBridgeBuild){
                        secEng += ((DarkBridgeBuild)build).eng;
                    }
                }
            }

            if(!linkValid(tile, other)){
                warmup = 0f;
                link = -1;
            }else{
                if(other == null) return;
                IntSeq inc = ((DarkBridgeBuild)other.build).incoming;
                int pos = tile.pos();
                if(!inc.contains(pos)){
                    inc.add(pos);
                }

                warmup = Mathf.approachDelta(warmup, 1, 1f / 30f);

                for(int i = 0; i < proximity.size; i++){
                    autoEng(proximity.get(i));
                }
            }
            eng = secEng;
        }

        public void autoEng(Building build){
            if(build instanceof DarkGraph dg && build.block != null){
                if(dg.outputEng() > 0 && (!build.block.rotate || build.front() == this) && checkInput(build)){
                    secEng += dg.outputEng()/dg.edge();
                }
            }
        }

        @Override
        public void draw() {
            //super
            if (this.block.variants != 0 && this.block.variantRegions != null) {
                Draw.rect(this.block.variantRegions[Mathf.randomSeed(this.tile.pos(), 0, Math.max(0, this.block.variantRegions.length - 1))], this.x, this.y, this.drawrot());
            } else {
                Draw.rect(this.block.region, this.x, this.y, this.drawrot());
            }

            Tile other = world.tile(link);
            if(!linkValid(tile, other)) return;

            if(Mathf.zero(Renderer.bridgeOpacity)) return;

            int i = relativeTo(other.x, other.y);

            this.drawTeamTop();

            int dist = Math.max(Math.abs(other.x - tile.x), Math.abs(other.y - tile.y)) - 1;

            Draw.color(TFPal.darkEng);

            int arrows = (int)(dist * tilesize / arrowSpacing), dx = Geometry.d4x(i), dy = Geometry.d4y(i);

            for(int a = 0; a < arrows; a++){
                Draw.alpha(Mathf.absin(a - Time.time / arrowTimeScl, arrowPeriod, 1f) * warmup * Renderer.bridgeOpacity);
                Drawf.tri(
                        x + dx * (tilesize / 2f + a * arrowSpacing + arrowOffset),
                        y + dy * (tilesize / 2f + a * arrowSpacing + arrowOffset),
                        size * 4f,
                        size * 4 * 0.8f,
                        angleTo(other)
                        );
            }

            Draw.reset();
        }

        public boolean checkInput(Building from){
            return team == from.team && checkAccept(from, world.tile(link));
        }

        @Override
        public boolean canOutput(Building to) {
            return checkDump(to);
        }

        @Override
        public float outputEng() {
            Tile other = world.tile(link);
            if(linkValid(tile, other)) return 0;
            return eng;
        }

        @Override
        public int edge() {
            int e = 0;
            for(int i = 0; i < proximity.size; i++){
                Building b = proximity.get(i);
                if(b instanceof DarkConsumer
                        && ((DarkConsumer)b).hasEng() > 0
                        && (b.block != null && (!b.block.rotate || b.front() != this))
                        && (!(b instanceof DarkBridgeBuild) || ((DarkBridgeBuild)b).link != this.pos()))
                    e++;
            }
            return e == 0 ? 1 : e;
        }

        @Override
        public void consumeDark(float d) {

        }

        @Override
        public float hasEng() {
            if(link == -1) return 0;
            return eng;
        }
    }
}
