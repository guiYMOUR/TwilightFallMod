package TwilightFall.world.blocks.darkEng;

import TwilightFall.world.meta.TFBlockGroup;
import arc.Core;
import arc.graphics.Color;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;


public class DarkEngProductor extends GenericCrafter implements DarkBlock{
    public float outputEng = 60;

    public DarkEngProductor(String name) {
        super(name);

        destructible = true;
        update = solid = true;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("darkEng", (DarkProductorBuild ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.darkAmount", outputEng),
                () -> Color.valueOf("9e78dc"),
                () -> 1
        ));
    }

    @Override
    public TFBlockGroup group() {
        return TFBlockGroup.none;
    }

    @Override
    public boolean outputDark() {
        return true;
    }


    public class DarkProductorBuild extends GenericCrafterBuild implements DarkGraph{

        @Override
        public float outputEng() {
            return outputEng;
        }

        @Override
        public int edge() {
            return DarkBuildFunc.baseEdge(this);
        }
    }
}
