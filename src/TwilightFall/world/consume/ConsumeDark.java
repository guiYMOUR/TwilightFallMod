package TwilightFall.world.consume;

import TwilightFall.world.blocks.dark.DarkBuilding;
import TwilightFall.world.blocks.dark.DarkConsumer;
import TwilightFall.world.meta.TFStatValues;
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
        if(build instanceof DarkBuilding db && build instanceof DarkConsumer dc) {
            if (db.darkGet() >= amount) dc.consumeDark(amount);
        }
    }

    @Override
    public float efficiency(Building build) {
        if(build instanceof DarkBuilding db) {
            if (smooth) {
                float ed = build.edelta() * build.efficiencyScale();
                if (ed <= 1e-4f) return 0;
                return Math.min(db.darkGet() / (amount * ed * multiplier.get(build)), 1);
            } else {
                return build.consumeTriggerValid() || db.darkGet() >= amount ? 1 : 0;
            }
        }
        return 0;
    }

    @Override
    public void display(Stats stats) {
        stats.add(Stat.input, TFStatValues.consumeDark(amount, smooth, stats.timePeriod));
    }
}
