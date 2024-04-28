package TwilightFall.contents;

import TwilightFall.entities.ArataUnitType;
import TwilightFall.entities.bullets.LootBullet;
import TwilightFall.entities.weapons.LoopWeapon;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.entities.abilities.MoveEffectAbility;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.part.HoverPart;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.blocks.environment.Floor;

import static TwilightFall.TwilightFallMod.name;

public class TFUnitTypes {
    static {
        EntityMapping.nameMap.put(name("myrtle"), EntityMapping.idMap[45]);
        EntityMapping.nameMap.put(name("magnolia"), EntityMapping.idMap[45]);
        EntityMapping.nameMap.put(name("marigold"), EntityMapping.idMap[45]);
        EntityMapping.nameMap.put(name("myosotis"), EntityMapping.idMap[45]);
        EntityMapping.nameMap.put(name("mandragora"), EntityMapping.idMap[45]);
    }

    public static UnitType
            myrtle, magnolia, marigold, myosotis, mandragora;

    //only for set
    public static Effect tee(float lifeTime, float rad){
        return new Effect(lifeTime, e -> {
            Floor f = Vars.world.floorWorld(e.x, e.y);
            if(f == null) return;
            Draw.color(f.mapColor);
            Draw.alpha(0.7f);
            Fill.circle(e.x, e.y, rad * e.fout());
        }).layer(Layer.floor + 1f);
    }

    public static void load(){
        myrtle = new ArataUnitType("myrtle"){{
            health = 230;
            armor = 1;
            hovering = true;
            shadowElevation = 0.1f;
            drag = 0.08f;
            speed = 2f;
            accel = 0.1f;
            hitSize = 8f;
            itemCapacity = 0;
            useEngineElevation = false;

            rotateSpeed = 6;

            engineColor = Pal.thoriumPink;
            engineOffset = 6.2f;
            engineSize = 1.8f;
            trailColor = Pal.thoriumPink;
            trailLength = 4;

            Effect te = tee(12, 1.8f);

            abilities.add(
                    new MoveEffectAbility(-6, 0, Color.white, te, 0.1f),
                    new MoveEffectAbility(6, 0, Color.white, te, 0.1f)
            );

            Item[] items = {TFItems.DarkPetal, TFItems.chromium, Items.lead};

            BulletType mbt = new LootBullet(items, 1, true){{
                speed = 2;
                lifetime = 60;
                trailLength = 6;
                trailWidth = 2;
                trailColor = Pal.thoriumPink;
                keepVelocity = false;
                damage = 20;
                hitEffect = despawnEffect = new Effect(24, e -> {
                    Draw.color(Pal.thoriumPink);
                    Lines.stroke(2);
                    Angles.randLenVectors(e.id, 3, 10 * e.fin(), e.rotation, 180, (x, y) -> {
                        Lines.stroke(e.fout());
                        Lines.square(e.x + x, e.y + y, 6.5f * e.fout(), 45);
                    });
                    Angles.randLenVectors(e.id, 5, 5 + e.fin() * 10, (x, y) -> {
                        float ang = Mathf.angle(x, y);
                        Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1);
                    });
                    Drawf.light(e.x, e.y, 20, Pal.thoriumPink, 0.6f * e.fout());
                });
            }

                @Override
                public void draw(Bullet b) {
                    super.draw(b);
                    Lines.stroke(0.5f, Pal.thoriumPink);
                    Lines.circle(b.x, b.y, 3);
                    Lines.circle(b.x, b.y, 1.8f);
                }
            };

            weapons.add(
                    new LoopWeapon(){{
                        bullet = mbt;
                        reload = 30f;
                        mirror = false;
                        x = y = 0;
                        shootSound = Sounds.blaster;
                    }}
            );

            setEnginesMirror(
                    new UnitEngine(-6.2f, 0, 1.5f, 180)
            );

            parts.add(
                    new HoverPart() {{
                        x = 6f;
                        y = 0;
                        mirror = true;
                        radius = 4;
                        phase = 90;
                        stroke = 1.5f;
                        layerOffset = -0.001f;
                        color = Pal.thoriumPink;
                    }},
                    new HoverPart() {{
                        x = 0;
                        y = -6;
                        mirror = false;
                        radius = 4.5f;
                        phase = 180;
                        stroke = 1.5f;
                        layerOffset = -0.001f;
                        color = Pal.thoriumPink;
                    }}
            );
        }};

        magnolia = new ArataUnitType("magnolia"){{
            health = 600;
            armor = 3;
            hovering = true;
            shadowElevation = 0.06f;
            drag = 0.07f;
            speed = 1.2f;
            accel = 0.1f;
            hitSize = 11f;
            itemCapacity = 40;
            useEngineElevation = false;

            Effect tef = tee(15, 2);
            Effect teb = tee(15, 2);

            abilities.add(
                    new MoveEffectAbility(-5, 4f, Color.white, tef, 0.1f),
                    new MoveEffectAbility(5, 4f, Color.white, tef, 0.1f),
                    new MoveEffectAbility(-6.2f, -5.2f, Color.white, teb, 0.1f),
                    new MoveEffectAbility(6.2f, -5.2f, Color.white, teb, 0.1f)
            );

            engineColor = Pal.thoriumPink;
            engineOffset = 2;
            engineSize = 3f;
            trailColor = Pal.thoriumPink;
            trailLength = 10;

            Item[] items = {TFItems.DarkPetal, TFItems.chromium};
            BulletType bt = new LootBullet(items, 2){{
                speed = 5f;
                lifetime = 39f;
                homingDelay = 12;
                homingPower = 0.1f;
                damage = 30;
                trailColor = Pal.thoriumPink;
                trailLength = 9;
                trailWidth = 2;
                keepVelocity = false;

                hitEffect = despawnEffect = new Effect(24, e -> {
                    Draw.color(Pal.thoriumPink);
                    Angles.randLenVectors(e.id, 5, 12 * e.fin(), e.rotation, 180, (x, y) -> {
                        float ang = Mathf.angle(x, y);
                        Drawf.tri(e.x + x, e.y + y, 2, 1.7f, ang + 180 * e.fout());
                    });
                    Angles.randLenVectors(e.id, 3, 2 + e.fin() * 12, (x, y) -> {
                        Fill.square(e.x + x, e.y + y, 5 * e.fout(), 45);
                    });
                    Drawf.light(e.x, e.y, 20, Pal.thoriumPink, 0.6f * e.fout());
                });
            }

                @Override
                public void draw(Bullet b) {
                    super.draw(b);
                    Draw.color(trailColor);
                    Drawf.tri(b.x, b.y, 3, 8, b.rotation());
                }
            };

            weapons.add(
                    new LoopWeapon(name("magnolia-weapon")){{
                        reload = 90;
                        shoot.shots = 3;
                        inaccuracy = 15;
                        shoot.shotDelay = 6;
                        bullet = bt;
                        shootSound = Sounds.sap;
                    }}
            );

            parts.add(
                    new HoverPart() {{
                        x = 5f;
                        y = 4;
                        mirror = true;
                        radius = 5;
                        phase = 120;
                        stroke = 2f;
                        layerOffset = -0.001f;
                        color = Pal.thoriumPink;
                    }},
                    new HoverPart() {{
                        x = 6.2f;
                        y = -5.2f;
                        mirror = true;
                        radius = 5.5f;
                        phase = 120;
                        stroke = 2f;
                        layerOffset = -0.001f;
                        color = Pal.thoriumPink;
                    }}
            );
        }};
    }
}
