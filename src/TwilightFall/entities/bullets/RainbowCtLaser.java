package TwilightFall.entities.bullets;

import TwilightFall.contents.TFGet;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Damage;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;

public class RainbowCtLaser extends ContinuousLaserBulletType {
    public int step = 6;
    public float shiftSpeed = 3f;
    public boolean backFlow = true;
    public boolean hasStart = false;

    public float linTime = 6;
    public int linAmount = 1;
    public int spacing = 5;
    public ChainLightningFade chain = null;

    public RainbowCtLaser(){
        width = 3f;
        length = 25f * 8;
        backLength = 4f;
        colors = new Color[]{Color.valueOf("ff8787").a(0.4f), Color.valueOf("ff8787").a(0.7f), Color.valueOf("ff8787")};

        oscMag = 1f;
        oscScl = 5f;
        incendChance = -1;
        incendAmount = -1;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        if(chain != null){
            float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
            float realLength = Damage.findLength(b, length, laserAbsorb, pierceCap);
            if(b.timer.get(2, linTime)){
                for(int i = 0; i < linAmount; i++) {
                    float sx = b.x + Angles.trnsx(b.rotation(), -backLength), sy = b.y + Angles.trnsy(b.rotation(), -backLength);
                    float ex = b.x + Angles.trnsx(b.rotation(), realLength * fout), ey = b.y + Angles.trnsy(b.rotation(), realLength * fout);
                    chain.linkSpace = realLength / spacing;
                    chain.create(b, b.team, sx, sy, 1, -1, 1, 1, TFGet.v1.set(ex, ey));
                }
            }
        }
    }

    @Override
    public void draw(Bullet b) {

        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
        float realLength = Damage.findLength(b, length, laserAbsorb, pierceCap);
        float rot = b.rotation();

        for(int i = 0; i < colors.length; i++){
            float colorFin = i / (float)(colors.length - 1);
            float baseStroke = Mathf.lerp(strokeFrom, strokeTo, colorFin);
            float stroke = (width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * baseStroke;
            float ellipseLenScl = Mathf.lerp(1 - i / (float)(colors.length), 1f, pointyScaling);
            float start = (hasStart && b.data instanceof Float f) ? f : 0;

            for(int s = 0; s < step; s++) {
                //var cr = TFGet.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)).shiftHue(Time.time * shiftSpeed + 180f/step * s + start);
                var cr = TFGet.c1.set(colors[i]).shiftHue(Time.time * shiftSpeed + 180f/step * (backFlow ? s : step - 1 - s) + start);

                Draw.color(cr);

                if (s == 0) {
                    //back ellipse
                    Drawf.flameFront(b.x, b.y, divisions, rot + 180f, backLength, stroke / 2f);
                } else if (s == step - 1) {
                    //front ellipse
                    Tmp.v1.trnsExact(rot, realLength - frontLength);
                    Drawf.flameFront(b.x + Tmp.v1.x, b.y + Tmp.v1.y, divisions, rot, frontLength * ellipseLenScl, stroke / 2f);
                } else {
                    int st = step - 2;
                    Tmp.v2.trnsExact(rot, (realLength - frontLength) / st * (s - 1));
                    Lines.stroke(stroke);
                    Lines.lineAngle(b.x + Tmp.v2.x, b.y + Tmp.v2.y, rot, (realLength - frontLength) / st, false);
                }
            }
        }

        Tmp.v1.trns(b.rotation(), realLength * 1.1f);

        Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
        Draw.reset();
    }
}
