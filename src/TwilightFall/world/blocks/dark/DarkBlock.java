package TwilightFall.world.blocks.dark;

import TwilightFall.world.meta.TFBlockGroup;
import arc.Core;
import arc.graphics.Color;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.TargetPriority;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerGraph;

public class DarkBlock extends Block implements DarkFitter{
    public boolean hasDark;

    public float darkCapacity = 10;
    public boolean outputsDark = false;

    public DarkBlock(String name) {
        super(name);
        hasDark = true;
        update = true;
        destructible = true;
        priority = TargetPriority.transport;
    }

    @Override
    public TFBlockGroup tgroup() {
        return TFBlockGroup.dark;
    }

    @Override
    public boolean hasDark() {
        return hasDark;
    }

    @Override
    public float darkCapacity() {
        return darkCapacity;
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        if(!(other instanceof DarkBlock db)) return false;
        return other.replaceable && (other != this || (rotate && quickRotate)) && db.tgroup() == this.tgroup() &&
                (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || tgroup().anyReplace)));
    }

    @Override
    public void setBars() {
        super.setBars();
        if(outputsDark) addBar("darkEng", (DarkBuild ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.darkAmount", ent.darkOutPut()),
                () -> Color.valueOf("9e78dc"),
                () -> ent.darkOutPut() > 0 ? 1 : 0
        ));

        if(darkCapacity > 0) addBar("darkCap", (DarkBuild ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.totalDarkAmount", Strings.autoFixed(ent.darkGet(), 2)),
                () -> Color.valueOf("9e78dc"),
                () -> ent.darkGet()/darkCapacity
        ));
    }

    public class DarkBuild extends Building implements DarkBuilding{
        public float dark;

        public float darkOutPut(){
            return 0;
        }

        @Override
        public float darkGet() {
            return dark;
        }

        @Override
        public boolean accDark(Building from) {
            return false;
        }

        public boolean canDumpDark(Building to) {
            return true;
        }

        public void dumpDark(float scaling, int outputDir){
            int dump = cdump;
            if (!(dark <= 1e-4f)) {
                for(int i = 0; i < proximity.size; ++i) {
                    this.incrementDump(proximity.size);
                    Building other = proximity.get((i + dump) % proximity.size);
                    if (outputDir == -1 || (outputDir + this.rotation) % 4 == relativeTo(other)) {
                        if (other instanceof DarkBuilding db && other.team == team && other.block instanceof DarkFitter df && df.hasDark() && canDumpDark(other)) {
                            float ofract = db.darkGet() / df.darkCapacity();
                            float fract = dark / darkCapacity;
                            if (ofract < fract) {
                                transferDark(other, (fract - ofract) * darkCapacity / scaling);
                            }
                        }
                    }
                }

            }
        }

        @Override
        public void handleDark(float amount) {
            dark += amount;
        }

        public void transferDark(Building next, float amount) {
            if(!(next instanceof DarkBuilding db) || !(next.block instanceof DarkFitter df)) return;
            float flow = Math.min(df.darkCapacity() - db.darkGet(), amount);
            if (db.accDark(this)) {
                db.handleDark(flow);
                dark -= flow;
            }

        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(dark);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            dark = read.f();
        }
    }
}
