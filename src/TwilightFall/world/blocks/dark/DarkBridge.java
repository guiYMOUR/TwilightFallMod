package TwilightFall.world.blocks.dark;

import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.input.Placement;
import mindustry.world.Tile;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;


public class DarkBridge extends DarkTube{
    public int minRange = 1;
    public int range = 3;

    public DrawBlock drawer = new DrawDefault();

    public DarkBridge(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(new Stat("twilight-fall-minrange", StatCat.function), minRange, StatUnit.blocks);
        stats.add(Stat.range, range, StatUnit.blocks);
    }

    public boolean positionsValid(int x1, int y1, int x2, int y2){
        if(x1 == x2){
            return Math.abs(y1 - y2) <= range && Math.abs(y1 - y2) >= minRange;
        }else if(y1 == y2){
            return Math.abs(x1 - x2) <= range && Math.abs(x1 - x2) >= minRange;
        }else{
            return false;
        }
    }

    public boolean linkValid(Tile tile, Tile other){
        return positionsValid(tile.x, tile.y, other.x, other.y);
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> Math.max(Math.abs(point.x - other.x), Math.abs(point.y - other.y)) <= range && Math.max(Math.abs(point.x - other.x), Math.abs(point.y - other.y)) >= minRange);
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    protected TextureRegion[] icons() {
        return drawer.icons(this);
    }



    public class DarkBridgeBuild extends DarkTubeBuild{
        public Seq<Building> posBuild = new Seq<>();

        public void initBuild() {
            posBuild.clear();
            for(int[] p : POS){
                for(int j = minRange; j <= range; j++){
                    int tx = tile.x + p[0] * j;
                    int ty = tile.y + p[1] * j;
                    Building b = Vars.world.build(tx, ty);
                    posBuild.add(b);
                }
            }
        }

        @Override
        public void tapOnSelf() {
            initBuild();
            for(var b : posBuild){
                if(b instanceof DarkTubeBuild tube){
                    configure(tube.pos());
                    tube.configure(pos());
                }
            }
        }

        @Override
        public void dumpDark(float scaling, int outputDir) {
            for(int i = 0; i < links.size; i++){
                var p = links.get(i);
                var b = Vars.world.build(p);
                if(b instanceof DarkTubeBuild db && b.block instanceof DarkTube dt && canLink(b)){
                    float ofract = db.darkGet() / dt.darkCapacity();
                    float fract = dark / darkCapacity;
                    if (ofract < fract) {
                        transferDark(b, (fract - ofract) * darkCapacity / scaling);
                    }
                }
            }
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public boolean canLink(Building other) {
            return other != this && other instanceof DarkTubeBuild && other.tile != null && other.team == team && linkValid(tile, other.tile);
        }
    }
}
