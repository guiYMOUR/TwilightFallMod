package TwilightFall.entities.weapons;

import TwilightFall.contents.Wikis;
import TwilightFall.entities.bullets.LootBullet;
import TwilightFall.world.meta.WikiEntry;
import TwilightFall.world.meta.displayImage;
import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.ui.ItemDisplay;
import mindustry.ui.ItemImage;

public class LoopWeapon extends Weapon {
    public LoopWeapon(String name){
        super(name);
    }
    public LoopWeapon(){
        super();
    }

    @Override
    public void addStats(UnitType u, Table t) {
        super.addStats(u, t);
        t.row();
        if(bullet instanceof LootBullet lb) {
            //t.add(Core.bundle.get("wiki.twilight-fall.loot.name"));
            t.add(new WikiEntry(Wikis.loot));
            t.row();
            t.add(
                    lb.plus ?
                            Core.bundle.format("stat.twilight-fall-weapon-loop.plus", lb.amount)
                            :
                            Core.bundle.format("stat.twilight-fall-weapon-loop.random", lb.amount)
            );
            for (var i : lb.items) {
                t.row();
                t.add(new ItemDisplay(i, 0));
            }
            t.row();
        }

    }
}
