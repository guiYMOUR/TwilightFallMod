package TwilightFall.world.blocks.darkEng;

import arc.Core;
import arc.graphics.Color;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.Block;

public class DarkRouter extends DarkBlockBase {

    public DarkRouter(String name) {
        super(name);
        update = solid = destructible = true;
        size = 1;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("darkEng", (DarkRouterBuild ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.darkAmount", ent.eng),
                () -> Color.valueOf("9e78dc"),
                () -> ent.eng > 0 ? 1 : 0
        ));
    }

    public class DarkRouterBuild extends Building implements DarkGraph, DarkConsumer{
        public float eng = 0;

        @Override
        public void updateTile() {
            eng = autoEng();
        }

        protected float autoEng(){
            float add = 0;
            for(int i = 0; i < proximity.size; i++) {
                Building build = proximity.get(i);
                if (build instanceof DarkGraph && build.block != null) {
                    DarkGraph dg = ((DarkGraph) build);
                    if (dg.outputEng() > 0 && (!build.block.rotate || build.front() == this)) {
                        add += dg.outputEng() / (dg.edge() + (dg instanceof DarkRouterBuild ? 1 : 0));
                    }
                }
            }
            return add;
        }

        @Override
        public float outputEng() {
            return eng;
        }

        @Override
        public int edge() {
            return DarkBuildFunc.baseEdge(this);
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
