package TwilightFall.world.blocks.dark;

import mindustry.gen.Building;

public interface DarkBuilding {
    boolean accDark(Building from);
    float darkGet();
    void handleDark(float amount);
}
