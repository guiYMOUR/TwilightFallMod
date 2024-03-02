package TwilightFall.world.drawer;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class drawShadow extends DrawBlock {
    public TextureRegion main, shadow;

    @Override
    public void draw(Building build) {

    }

    @Override
    public void load(Block block) {
        main = Core.atlas.find(block.name);
        shadow = Core.atlas.find(block.name + "-shadow");
    }
}
