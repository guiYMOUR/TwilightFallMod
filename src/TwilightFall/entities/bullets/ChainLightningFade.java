//Each section of lightning causes one damage

package TwilightFall.entities.bullets;

import TwilightFall.contents.TFGet;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Position;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;

public class ChainLightningFade extends BulletType {
    public Color color;

    public float linkSpace;
    public float stroke;
    public boolean large = false;
    public boolean rainbowColor = false;
    public float shift = 3;
    //A very clumsy method to synchronize laser colors...
    public float start = 0;

    public ChainLightningFade(float lifetime, float linkSpace, float stroke, Color color, float damage, Effect hitEffect){
        absorbable = hittable = collides = collidesTiles = keepVelocity = false;
        speed = 0;
        despawnEffect = Fx.none;
        this.lifetime = lifetime;
        this.linkSpace = linkSpace;
        this.stroke = stroke;
        this.color = color;
        this.damage = damage;
        this.hitEffect = hitEffect;
        status = StatusEffects.shocked;
    }

    private void init(chain b) {
        if(!(b.data instanceof Position p) || damage < 0) return;
        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(b.x, b.y, tx, ty);
        Tmp.v1.set(p).sub(b.x, b.y).nor();

        float normx = Tmp.v1.x, normy = Tmp.v1.y;
        int links = Mathf.ceil(dst / linkSpace);
        float spacing = dst / links;

        b.random.setSeed(b.id);
        b.resetPos = new float[links + 1][2];
        int i;

        float ox = b.x, oy = b.y;
        b.resetPos[0] = new float[]{b.x, b.y};
        for(i = 0; i < links; i++){
            float nx, ny;
            if(i == links - 1){
                nx = tx;
                ny = ty;
            }else{
                float len = (i + 1) * spacing;
                Tmp.v1.setToRandomDirection(b.random).scl(linkSpace/2f);
                nx = b.x + normx * len + Tmp.v1.x;
                ny = b.y + normy * len + Tmp.v1.y;
            }

            b.resetPos[i + 1] = new float[]{nx, ny};

            float length = TFGet.v1.set(ox, oy).dst(nx, ny);
            float angle = TFGet.v2.set(ox, oy).angleTo(nx, ny);
            Damage.collideLine(b, b.team, hitEffect, ox, oy, angle, length, large, false);
            ox = nx;
            oy = ny;
        }
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        if(!(b instanceof chain)) return;
        init((chain) b);
    }

    private void draw(chain b){
        if(b.resetPos.length > 0) {
            Lines.stroke(stroke * Mathf.curve(b.fout(), 0, 0.7f));
            Draw.color(Color.white, color, b.fin());

            Fill.circle(b.x, b.y, Lines.getStroke() / 2);

            b.random.setSeed(b.id);
            float fin = Mathf.curve(b.fin(), 0, 0.5f);
            int i;

            for (i = 0; i < (b.resetPos.length - 1) * fin; i++) {
                if(rainbowColor) Draw.color(TFGet.c3.set(TFGet.rainbowRed).shiftHue(Time.time * shift + i * 180f/(b.resetPos.length - 1) + start));
                float ox = b.resetPos[i][0], oy = b.resetPos[i][1];
                float nx = b.resetPos[i + 1][0], ny = b.resetPos[i + 1][1];

                Lines.line(ox, oy, nx, ny);
            }

            Draw.reset();
        }
    }

    @Override
    public void draw(Bullet b) {
        if(!(b instanceof chain)) return;
        draw((chain) b);
    }

    @Override
    public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
        Bullet bullet = chain.create();
        return TFGet.anyOtherCreate(bullet, this, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY);
    }

    public static class chain extends Bullet{
        public final Rand random = new Rand();

        public float[][] resetPos;

        public static chain create() {
            return Pools.obtain(chain.class, chain::new);
        }
    }
}