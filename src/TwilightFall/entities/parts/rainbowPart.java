package TwilightFall.entities.parts;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.RegionPart;

import java.util.Arrays;

import static TwilightFall.contents.TFGet.*;

public class rainbowPart extends RegionPart {
    public int rainbowAmount;
    public float shiftSpeed = 3f;
    public float mul = 180f;
    public PartProgress rainbowProgress = PartProgress.warmup;

    public TextureRegion[][] rainbows = {};
    public Color[] cs = {};

    public rainbowPart(int rainbowAmount, String region){
        super(region);
        this.rainbowAmount = rainbowAmount;
    }

    public rainbowPart(int rainbowAmount, String region, Blending blending, Color color){
        super(region, blending, color);
        this.rainbowAmount = rainbowAmount;
    }

    @Override
    public void draw(PartParams params) {
        if(rainbowAmount > 0) {
            float z = Draw.z();
            if (layer > 0) Draw.z(layer);
            Draw.z(z + 0.001f);
            Draw.z(Draw.z() + layerOffset);

            float rainbowAlpha = rainbowProgress.getClamp(params);
            float prog = progress.getClamp(params), sclProg = growProgress.getClamp(params);
            float mx = moveX * prog, my = moveY * prog, mr = moveRot * prog + rotation,
                    gx = growX * sclProg, gy = growY * sclProg;

            if (moves.size > 0) {
                for (int i = 0; i < moves.size; i++) {
                    var move = moves.get(i);
                    float p = move.progress.getClamp(params);
                    mx += move.x * p;
                    my += move.y * p;
                    mr += move.rot * p;
                    gx += move.gx * p;
                    gy += move.gy * p;
                }
            }

            int len = mirror && params.sideOverride == -1 ? 2 : 1;
            float preXscl = Draw.xscl, preYscl = Draw.yscl;
            Draw.xscl *= xScl + gx;
            Draw.yscl *= yScl + gy;

            for (int s = 0; s < len; s++) {
                for (int r = 0; r < rainbowAmount; r++) {
                    int i = params.sideOverride == -1 ? s : params.sideOverride;

                    TextureRegion region = rainbows[s][r];
                    float sign = (i == 0 ? 1 : -1) * params.sideMultiplier;
                    Tmp.v2.set((x + mx) * sign, y + my).rotateRadExact((params.rotation - 90) * Mathf.degRad);

                    float
                            rx = params.x + Tmp.v2.x,
                            ry = params.y + Tmp.v2.y,
                            rot = mr * sign + params.rotation - 90;

                    Draw.xscl *= sign;

                    if (region.found()) {
                        Draw.color(rainStart(cs[r]).a(rainbowAlpha).shiftHue(Time.time * shiftSpeed + r * mul / rainbowAmount));
                        Draw.rect(region, rx, ry, rot);
                    }

                    Draw.xscl *= sign;
                }
            }

            Draw.color();
            Draw.mixcol();

            Draw.z(z);

            Draw.scl(preXscl, preYscl);
        }

        super.draw(params);
    }

    @Override
    public void load(String name) {
        super.load(name);
        String realName = this.name == null ? name + suffix : this.name;
        if(rainbowAmount > 0){
            rainbows = new TextureRegion[2][rainbowAmount];
            cs = new Color[rainbowAmount];
            Arrays.fill(cs, new Color());
            if(mirror && turretShading){
                for(int i = 0; i < rainbowAmount; i++){
                    rainbows[0][i] = Core.atlas.find(realName + "-r-" + (i + 1));
                    rainbows[1][i] = Core.atlas.find(realName + "-l-" + (i + 1));
                }
            }else{
                for(int i = 0; i < rainbowAmount; i++){
                    rainbows[0][i] = Core.atlas.find(realName + "-" + (i + 1));
                }
            }
        }
    }
}
