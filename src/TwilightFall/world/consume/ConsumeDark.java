package TwilightFall.world.consume;

import TwilightFall.world.blocks.darkEng.DarkConsumer;
import TwilightFall.world.meta.TFStatValues;
import arc.Core;
import mindustry.gen.Building;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

public class ConsumeDark extends Consume {
    public float amount;
    public boolean smooth;

    public ConsumeDark(float amount, boolean smooth){
        this.amount = amount;
        this.smooth = smooth;
    }

    public ConsumeDark(float amount){
        this(amount, false);
    }

    //测试时候用的
    public ConsumeDark(){
        this(0);
    }

    @Override
    public void update(Building build) {
        if(!smooth) return;
        if(build instanceof DarkConsumer dc){
            dc.consumeDark((amount/60) * multiplier.get(build) * build.edelta());
        }
    }

    @Override
    public void trigger(Building build) {
        if(smooth) return;
        if(build instanceof DarkConsumer dc) {
            if (dc.hasEng() >= amount) dc.consumeDark(amount);
        }
    }

    @Override
    public float efficiency(Building build) {
        if(build instanceof DarkConsumer dc) {
            if (smooth) {
                float ed = build.edelta() * build.efficiencyScale();
                if (ed <= 0.00001f) return 0;
                return Math.min(dc.hasEng() / (amount * ed * multiplier.get(build)), 1);
            } else {
                return build.consumeTriggerValid() || dc.hasEng() >= amount ? 1 : 0;
            }
        }
        return 0;
    }

    @Override
    public void display(Stats stats) {
        stats.add(Stat.input, TFStatValues.consumeDark(amount, smooth, stats.timePeriod));
    }
}
