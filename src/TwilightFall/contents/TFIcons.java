package TwilightFall.contents;

import arc.Core;
import arc.graphics.g2d.TextureRegion;

import static TwilightFall.TwilightFallMod.name;

public class TFIcons {
    public static TextureRegion
        dark = Core.atlas.find(name("dark")),
        plant = Core.atlas.find(name("plant"));
    public static String
        darkName = Core.bundle.get("twilight-fall-dark");
}
