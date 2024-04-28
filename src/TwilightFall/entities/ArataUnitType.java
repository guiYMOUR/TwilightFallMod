package TwilightFall.entities;

import TwilightFall.contents.TFItems;
import mindustry.content.Items;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.world.meta.Env;

public class ArataUnitType extends UnitType {
    public ArataUnitType(String name) {
        super(name);
        outlineColor = Pal.darkOutline;
        envDisabled = Env.space;
        ammoType = new ItemAmmoType(TFItems.chromium);
        researchCostMultiplier = 10;
    }
}
