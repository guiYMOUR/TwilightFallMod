package TwilightFall.world.blocks.product;

import TwilightFall.world.blocks.darkEng.DarkConsumer;
import TwilightFall.world.blocks.darkEng.DarkGraph;
import arc.Core;
import arc.graphics.Color;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;


public class DarkCrafter extends GenericCrafter {
    public float darkCapacity = 10;

    public DarkCrafter(String name) {
        super(name);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("totalDarkEng", (DarkCrafterBuile ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.totalDarkAmount", Strings.autoFixed(ent.eng, 2)),
                () -> Color.valueOf("9e78dc"),
                () -> ent.eng/darkCapacity
        ));
        addBar("darkEng", (DarkCrafterBuile ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.darkAccept", ent.visualEng),
                () -> Color.valueOf("9e78dc"),
                () -> ent.visualEng > 0 ? 1 : 0
        ));
    }

    public class DarkCrafterBuile extends GenericCrafterBuild implements DarkConsumer {
        public float eng;
        public float secEng, visualEng;

        @Override
        public void updateTile() {
            super.updateTile();
            secEng = 0;
            for(int i = 0; i < proximity.size; i++){
                autoEng(proximity.get(i));
            }
            visualEng = secEng;
        }

        public void autoEng(Building build){
            if(build instanceof DarkGraph dg && build.block != null){
                if(dg.outputEng() > 0 && (!build.block.rotate || build.front() == this)){
                    eng = Math.min(darkCapacity, eng + (dg.outputEng()/dg.edge()/60) * Time.delta);
                    secEng += dg.outputEng()/dg.edge();
                }
            }
        }

        @Override
        public void consumeDark(float d) {
            eng = Math.max(0, eng - d);
        }

        @Override
        public float hasEng() {
            return eng;
        }

        @Override
        public boolean checkInput(Building from) {
            return eng < darkCapacity || shouldConsume();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(eng);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            eng = read.f();
        }
    }
}
