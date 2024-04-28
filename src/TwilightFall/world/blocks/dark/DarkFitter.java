package TwilightFall.world.blocks.dark;

import TwilightFall.world.meta.TFBlockGroup;

public interface DarkFitter {
    TFBlockGroup tgroup();

    float darkCapacity();

    boolean hasDark();
}
