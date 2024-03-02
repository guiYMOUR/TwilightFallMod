package TwilightFall.contents;

import TwilightFall.world.blocks.darkEng.DarkGraph;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Mover;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.entities.pattern.ShootSpread;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Velc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.Stats;

import static arc.Core.atlas;
import static mindustry.Vars.*;
import static mindustry.Vars.tilesize;

/**unfinished*/

public class TFGet {
    public static Color rainbowRed = Color.valueOf("ff8787");

    public static Color c1 = new Color();
    public static Color c2 = new Color();
    public static Color c3 = new Color();
    public static Color c4 = new Color();

    public static Vec2 v1 = new Vec2();
    public static Vec2 v2 = new Vec2();
    public static Vec2 v3 = new Vec2();

    public static Position pos(float x, float y){
        return new Position() {
            @Override
            public float getX() {
                return x;
            }

            @Override
            public float getY() {
                return y;
            }
        };
    }

    public static float dx(float px, float r, float angel){
        return px + r * (float) Math.cos(angel * Math.PI/180);
    }

    public static float dy(float py, float r, float angel){
        return py + r * (float) Math.sin(angel * Math.PI/180);
    }

    public static void statToTable(Stats stat, Table table){
        var m = stat.toMap().keys().toSeq();
        for(int i = 0; i < m.size; i++){
            var s = stat.toMap().get(m.get(i)).keys().toSeq();
            for(int j = 0; j < s.size; j++){
                var v = stat.toMap().get(m.get(i)).get(s.get(j));
                for(int k = 0; k < v.size; k++){
                    v.get(k).display(table);
                }
            }
        }
    }

    public static void statTurnTable(Stats stats, Table table){
        for(StatCat cat : stats.toMap().keys()){
            OrderedMap<Stat, Seq<StatValue>> map = stats.toMap().get(cat);

            if(map.size == 0) continue;

            if(stats.useCategories){
                table.add("@category." + cat.name).color(Pal.accent).fillX();
                table.row();
            }

            for(Stat stat : map.keys()){
                table.table(inset -> {
                    inset.left();
                    inset.add("[lightgray]" + stat.localized() + ":[] ").left().top();
                    Seq<StatValue> arr = map.get(stat);
                    for(StatValue value : arr){
                        value.display(inset);
                        inset.add().size(10f);
                    }

                }).fillX().padLeft(10);
                table.row();
            }
        }
    }

    public static void drawTiledFramesBar(float w, float h, float x, float y, Liquid liquid, float alpha){
        TextureRegion region = renderer.fluidFrames[liquid.gas ? 1 : 0][liquid.getAnimationFrame()];

        Draw.color(liquid.color, liquid.color.a * alpha);
        Draw.rect(region, x + w/2f, y + h/2f, w, h);
        Draw.color();
    }

    public static Bullet anyOtherCreate(Bullet bullet, BulletType bt, Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY){
        bullet.type = bt;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0f;
        bullet.originX = x;
        bullet.originY = y;
        if(!(aimX == -1f && aimY == -1f)){
            bullet.aimTile = world.tileWorld(aimX, aimY);
        }
        bullet.aimX = aimX;
        bullet.aimY = aimY;

        bullet.initVel(angle, bt.speed * velocityScl);
        if(bt.backMove){
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        }else{
            bullet.set(x, y);
        }
        bullet.lifetime = bt.lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = bt.drag;
        bullet.hitSize = bt.hitSize;
        bullet.mover = mover;
        bullet.damage = (damage < 0 ? bt.damage : damage) * bullet.damageMultiplier();
        //reset trail
        if(bullet.trail != null){
            bullet.trail.clear();
        }
        bullet.add();

        if(bt.keepVelocity && owner instanceof Velc) bullet.vel.add(((Velc)owner).vel());

        return bullet;
    }

    public static DrawBlock base(float rotSpeed) {
        return new DrawMulti(
                new DrawRegion("-rotator", rotSpeed),
                new DrawDefault(),
                new DrawRegion("-top")
        );
    }

    public static Color rainStart(Color c){
        return c.set(rainbowRed);
    }
}
