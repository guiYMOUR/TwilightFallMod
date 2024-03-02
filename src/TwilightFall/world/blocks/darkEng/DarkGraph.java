package TwilightFall.world.blocks.darkEng;

import mindustry.gen.Building;

public interface DarkGraph {
    float outputEng();
    int edge();
    default boolean canOutput(Building to){
        return true;
    }
}
