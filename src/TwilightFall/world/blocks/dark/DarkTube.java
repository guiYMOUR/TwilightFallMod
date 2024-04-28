package TwilightFall.world.blocks.dark;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;

public class DarkTube extends DarkBlock{
    public int[][] POS =  {{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
    public TextureRegion[][][][] regions = new TextureRegion[2][2][2][2];

    public DarkTube(String name) {
        super(name);
        configurable = true;
        size = 1;
        darkCapacity = 5;
        copyConfig = false;

        config(Point2.class, (DarkTubeBuild tile, Point2 i) -> tile.links.add(Point2.pack(i.x + tile.tileX(), i.y + tile.tileY())));

        config(int[].class, (DarkTubeBuild tile, int[] i) -> {
            tile.links.clear();
            for(int s : i){
                tile.links.add(s);
            }
        });

        config(Integer.class, (DarkTubeBuild tile, Integer i) -> {
            if(tile.links.contains(i)){
                tile.links.removeValue(i);
            } else {
                tile.links.add(i);
            }
        });
    }

    public boolean positionsValid(int x1, int y1, int x2, int y2){
        if(x1 == x2){
            return Math.abs(y1 - y2) <= 1;
        }else if(y1 == y2){
            return Math.abs(x1 - x2) <= 1;
        }else{
            return false;
        }
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans) {
        for(int i = 0; i < plans.size - 1; i++){
            var cur = plans.get(i);
            var next = plans.get(i + 1);
            if(positionsValid(cur.x, cur.y, next.x, next.y)){
                cur.config = new Point2(next.x - cur.x, next.y - cur.y);
            }
        }
    }

    @Override
    public void load() {
        super.load();
        for(int t = 0; t < 16; t++){
            int i = t / 8;
            int j = (t / 4) % 2;
            int k = (t / 2) % 2;
            int l = t % 2;
            regions[i][j][k][l] = Core.atlas.find(name + i + j + k + l);
        }
        regions[0][0][0][0] = Core.atlas.find(name);
    }

    public class DarkTubeBuild extends DarkBuild{
        public IntSeq links = new IntSeq();
        public int lastChange = -2;

        @Override
        public void updateTile() {
            if(lastChange != Vars.world.tileChanges) {
                lastChange = Vars.world.tileChanges;
                for (int i = 0; i < links.size; i++) {
                    int p = links.get(i);
                    if (Vars.world.build(p) instanceof DarkTubeBuild t) {
                        if (!t.links.contains(pos())) t.links.add(pos());
                    }
                }
            }

            dumpDark(2f, -1);
        }

        @Override
        public void pickedUp() {
            super.pickedUp();

            links.clear();
        }

        @Override
        public void draw() {
            int ix = 0;
            for (int[] p : POS) {
                ix *= 10;
                int px = tile.x + p[0], py = tile.y + p[1];
                Building b = Vars.world.build(px, py);
                if (b != null &&
                        ((b instanceof DarkTubeBuild && links.contains(b.pos())) ||
                                (!(b instanceof DarkTubeBuild) && b.block instanceof DarkFitter df && df.hasDark()))) {
                    ix += 1;
                }
            }
            int iv = ix/1000;
            int jv = ix/100;
            int kv = ix/10;
            int lv = ix;

            Draw.rect(regions[iv & 1][jv & 1][kv & 1][lv & 1], x, y);
        }

        public boolean canLink(Building other){
            return other instanceof DarkTubeBuild && other.team == team && proximity.contains(other);
        }

        @Override
        public void drawSelect() {
            super.drawSelect();

            for(int i = 0; i < links.size; i++){
                int p = links.get(i);
                var b = Vars.world.build(p);
                if(b != null && b.block != null){
                    Draw.color(Pal.place);
                    Lines.stroke(1.5f);
                    Lines.square(b.x, b.y, (float)(b.block.size * 8) / 2 + 1);
                    Draw.reset();
                }
            }
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
            if(this == other) {
                tapOnSelf();
                return false;
            }

            if(canLink(other)){
                configure(other.pos());
                if(other instanceof DarkTubeBuild tube){
                    tube.configure(pos());
                }
                return false;
            }

            return true;
        }

        public void tapOnSelf(){
            for (var b : proximity) {
                if (b instanceof DarkTubeBuild tube) {
                    configure(b.pos());
                    tube.configure(pos());
                }
            }
        }

        @Override
        public boolean accDark(Building from) {
            return !(from instanceof DarkTubeBuild tube) || tube.links.contains(pos());
        }

        @Override
        public int[] config() {
            return links.items;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            int l = links.size;
            write.s(l);
            for(int i = 0; i < l; i++){
                write.i(links.get(i));
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            var l = read.s();
            links.clear();
            for(int i = 0; i < l; i++){
                links.add(read.i());
            }
        }
    }
}
