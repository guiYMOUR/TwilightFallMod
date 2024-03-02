package TwilightFall.world.blocks.darkEng;

import mindustry.gen.Building;

public interface DarkConsumer {
    void consumeDark(float d);
    float hasEng();

    default boolean checkInput(Building from) {
        return true;
    }
}
