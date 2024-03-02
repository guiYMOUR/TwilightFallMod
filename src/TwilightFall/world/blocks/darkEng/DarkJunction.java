package TwilightFall.world.blocks.darkEng;

import arc.Core;
import arc.graphics.Color;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.Block;

public class DarkJunction extends DarkBlockBase{
    public DarkJunction(String name) {
        super(name);

        size = 1;

        solid = update = destructible = true;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("darkEngV", (DarkJunctionBuild ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.engVertical", ent.eng[1]),
                () -> Color.valueOf("9e78dc"),
                () -> ent.eng[1] > 0 ? 1 : 0
        ));
        addBar("darkEngH", (DarkJunctionBuild ent) -> new Bar(() ->
                Core.bundle.format("bar.twilight-fall.engHorizontal", ent.eng[0]),
                () -> Color.valueOf("9e78dc"),
                () -> ent.eng[0] > 0 ? 1 : 0
        ));
    }

    public class DarkJunctionBuild extends Building implements DarkConsumer{
        //nearby: 0,1,2,3
        public float engVertical = 0, engHorizontal = 0;
        public float[] eng = new float[4];

        @Override
        public void updateTile() {
            for (int i = 0; i < 4; i++) {
                Building b = nearby(i);
                if (b instanceof DarkJunctionBuild) {
                    DarkJunctionBuild j = (DarkJunctionBuild) b;
                    if (j.y == y && j.eng[0] > 0) engHorizontal = j.eng[0];
                    if (j.x == x && j.eng[1] > 0) engVertical = j.eng[1];
                } else if (b instanceof DarkGraph) {
                    DarkGraph darkGraph = ((DarkGraph) b);
                    if (i % 2 == 0) {
                        if (darkGraph.outputEng() > 0 &&
                                ((b.block != null && !b.block.rotate) || b.front() == this) && darkGraph.canOutput(this))
                            engHorizontal = darkGraph.outputEng() / darkGraph.edge();
                    } else {
                        if (((DarkGraph) b).outputEng() > 0 &&
                                ((b.block != null && !b.block.rotate) || b.front() == this) && darkGraph.canOutput(this))
                            engVertical = darkGraph.outputEng() / darkGraph.edge();
                    }
                }
            }

            eng[0] = engHorizontal;
            eng[1] = engVertical;
            engVertical = 0;
            engHorizontal = 0;
        }

        public float outputEng(int r) {
            return eng[r];
        }

        @Override
        public void consumeDark(float d) {

        }

        @Override
        public float hasEng() {
            return Math.max(eng[0], eng[1]);
        }
    }
}
