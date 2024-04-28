package TwilightFall.world.blocks.product;

import TwilightFall.world.blocks.dark.DarkBuilding;
import TwilightFall.world.blocks.dark.DarkConsumer;
import TwilightFall.world.blocks.dark.DarkFitter;
import TwilightFall.world.meta.TFBlockGroup;
import arc.Core;
import arc.graphics.Color;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;


public class DarkCrafter extends GenericCrafter implements DarkFitter {
    public float darkCapacity = 10;

    public DarkCrafter(String name) {
        super(name);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("totalDarkEng", (DarkCrafterBuile ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.totalDarkAmount", Strings.autoFixed(ent.darkGet(), 2)),
                () -> Color.valueOf("9e78dc"),
                () -> ent.dark/darkCapacity
        ));
    }

    @Override
    public TFBlockGroup tgroup() {
        return TFBlockGroup.none;
    }

    @Override
    public float darkCapacity() {
        return darkCapacity;
    }

    @Override
    public boolean hasDark() {
        return darkCapacity > 0;
    }

    public class DarkCrafterBuile extends GenericCrafterBuild implements DarkConsumer, DarkBuilding {
        public float dark;

        @Override
        public void consumeDark(float d) {
            dark = Math.max(0, dark - d);
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

        @Override
        public boolean accDark(Building from) {
            return true;
        }

        @Override
        public float darkGet() {
            return dark;
        }

        @Override
        public void handleDark(float amount) {
            dark += amount;
        }
    }
}
