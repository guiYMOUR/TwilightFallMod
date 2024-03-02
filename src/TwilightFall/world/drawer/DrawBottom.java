package TwilightFall.world.drawer;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static arc.Core.atlas;

public class DrawBottom extends DrawBlock {
    public Color color;
    public int size = -1;
    public DrawBottom(Color color){
        this.color = color;
    }

    public DrawBottom(Color color, int size){
        this.color = color;
        this.size = size;
    }
    @Override
    public void draw(Building build) {
        if(build.block == null) return;
        Draw.color(color);
        if(size <= 0) Fill.square(build.x, build.y, build.block.size/2f * Vars.tilesize);
        else Fill.square(build.x, build.y, size/2f * Vars.tilesize);
        Draw.color();
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.color(color);
        if(size <= 0) Fill.square(plan.drawx(), plan.drawy(), block.size/2f * Vars.tilesize);
        else Fill.square(plan.drawx(), plan.drawy(), size/2f * Vars.tilesize);
        Draw.color();
    }
}
