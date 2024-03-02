package TwilightFall.world.blocks.environment;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.world.Block;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;

public class SolidResources extends Block {
    public Color desColor = Color.valueOf("9e78dc");

    public SolidResources(String name) {
        super(name);
        destructible = true;
        buildCostMultiplier = 8f;
        alwaysReplace = false;
        alwaysUnlocked = true;

        targetable = false;
        underBullets = true;

        breakEffect = Fx.breakProp;
        breakSound = destroySound = Sounds.rockBreak;

        drawTeamOverlay = false;

        destroyEffect = new Effect(23, e -> {
            color(desColor);
            randLenVectors(e.id, 9, size * 12f * e.finpow(), (x, y) -> {
                Fill.square(e.x + x, e.y + y, e.fout() * 4.3f);
            });
        }).layer(Layer.debris);
    }
}
