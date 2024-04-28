package TwilightFall.world.blocks.defense.turrets;

import TwilightFall.world.blocks.dark.DarkBuilding;
import TwilightFall.world.blocks.dark.DarkConsumer;
import TwilightFall.world.blocks.dark.DarkFitter;
import TwilightFall.world.meta.TFBlockGroup;
import arc.Core;
import arc.graphics.Color;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.ContinuousTurret;

public class DartContinuousTurret extends ContinuousTurret implements DarkFitter {
    public float darkCapacity = 10;

    public DartContinuousTurret(String name) {
        super(name);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("totalDarkEng", (DarkContinuousTurretBuild ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.totalDarkAmount", Strings.autoFixed(ent.dark, 2)),
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

    public class DarkContinuousTurretBuild extends ContinuousTurretBuild implements DarkConsumer, DarkBuilding {
        public float dark;
        public float secEng, visualEng;

        @Override
        public void updateTile() {
            unit.ammo(unit.type().ammoCapacity * dark / darkCapacity);

            super.updateTile();
        }

        @Override
        public boolean canConsume() {
            return super.canConsume() && dark > 1e-3f;
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
        public void consumeDark(float d) {
            dark = Math.max(0, dark - d);
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
