package TwilightFall.world.drawer;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static TwilightFall.TwilightFallMod.name;

public class SizedBottom extends DrawBlock {
    @Override
    public void draw(Building build) {
        var block = build.block;
        if(block != null){
            Draw.rect(Core.atlas.find(name("bottom") + "-" + block.size), build.x, build.y);
        }
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{Core.atlas.find(name("bottom") + "-" + block.size)};
    }
}
