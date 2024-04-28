package TwilightFall.entities.bullets;

import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.Item;

public class LootBullet extends BulletType {
    public Item[] items;
    public int amount;
    public boolean plus;

    public boolean trueDam = false;

    public LootBullet(Item[] items, int amount, boolean plus){
        this.items = items;
        this.amount = amount;
        this.plus = plus;
    }

    public LootBullet(Item[] items, int amount){
        this(items, amount, false);
    }

    public LootBullet(Item[] items){
        this(items, 1);
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        if(entity instanceof Unit u && !u.dead && (trueDam || (u.shield <= 0 && (pierceArmor || u.armor <= damage/2)))) {
            if (amount > 0 && items.length > 0 && b.team.core() != null) {
                if (plus) {
                    Item i = items[Mathf.random(items.length - 1)];
                    for (int a = 0; a < amount; a++) {
                        b.team.core().handleItem(null, i);
                        Fx.itemTransfer.at(b.x, b.y, b.rotation(), Pal.thoriumPink, b.team.core());
                    }
                } else {
                    for (int a = 0; a < amount; a++) {
                        Item i = items[Mathf.random(items.length - 1)];
                        b.team.core().handleItem(null, i);
                        Fx.itemTransfer.at(b.x, b.y, b.rotation(), Pal.thoriumPink, b.team.core());
                    }
                }
            }
        }
        super.hitEntity(b, entity, health);
    }
}
