package TwilightFall.world.blocks.environment;

import TwilightFall.world.blocks.environment.GrowAble.GrowAbleBuild;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.*;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.effect.RadialEffect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.ui.MultiReqImage;
import mindustry.ui.ReqImage;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static arc.graphics.g2d.Draw.*;
import static mindustry.Vars.*;
import static mindustry.content.Fx.*;

public class WateringMachine extends Block {
    public float range = 18 * 8f;
    public Effect wateringEffect = new Effect(80f, e -> {
        color(e.color);
        alpha(Mathf.clamp(e.fin() * 2f));

        Fill.circle(e.x, e.y, e.fout() * 1.5f);
    });
    public int waterO = 4;
    public float waterRotation = 0;
    public float waterSpread = 10;
    public Effect waterEffect = new RadialEffect(
            new Effect(60f, e -> {
                color(e.color);
                alpha(0.6f);

                rand.setSeed(e.id);
                for(int i = 0; i < 3; i++){
                    float len = rand.random(6f), rot = rand.range(40f) + e.rotation;

                    e.scaled(e.lifetime * rand.random(0.3f, 1f), b -> {
                        v.trns(rot, len * b.finpow());
                        Fill.circle(e.x + v.x, e.y + v.y, 2f * b.fslope() + 0.2f);
                    });
                }
            }).layer(Layer.flyingUnitLow - 0.1f), 4, 90f, 11f){
        @Override
        public void create(float x, float y, float rotation, Color color, Object data) {
            if(!shouldCreate()) return;

            rotation += rotationOffset;

            for(int i = 0; i < amount; i++){
                effect.create(x + Angles.trnsx(rotation + 45, lengthOffset), y + Angles.trnsy(rotation + 45, lengthOffset), rotation + 45, color, data);
                rotation += rotationSpacing;
            }
        }
    };
    public float rotationSpeed = 1;

    public ObjectMap<Liquid, Float> waterMap = new ObjectMap<>();
    public float consumeAmount = 2/60f;
    public float reload = 60;

    public DrawBlock drawer = new DrawDefault();

    public WateringMachine(String name) {
        super(name);
        hasLiquids = true;
        outputsLiquid = false;
        solid = true;
        update = true;
        canOverdrive = false;
        ambientSound = Sounds.steam;
        ambientSoundVolume = 0.08f;
    }

    @Override
    public void init() {
        super.init();
        var ls = waterMap.keys().toSeq();
        if(ls.size > 0) for(var l : ls){
            liquidFilter[l.id] = true;
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, player.team().color);

        indexer.eachBlock(player.team(), x * tilesize + offset, y * tilesize + offset, range, other -> other instanceof GrowAbleBuild, other -> Drawf.selected(other, Tmp.c1.set(player.team().color).a(Mathf.absin(4f, 1f))));
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("boost", (WateringBuild entity) -> new Bar(() -> ("+" + Mathf.round(Math.max((entity.realBoost() * 100 - 100), 0)) + "%"), () -> Pal.accent, () -> entity.realBoost() > 1 ? 1 : 0));
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.booster, table -> {
            table.row();
            var key = waterMap.keys().toSeq();
            table.row();
            table.table(c -> {
                for(Liquid liquid : key){
                    c.table(Styles.grayPanel, b -> {
                        b.image(liquid.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                        b.table(info -> {
                            info.add(liquid.localizedName).left();
                        });

                        b.table(bt -> {
                            bt.right().defaults().padRight(3).left();
                            bt.add("+" + Strings.autoFixed(waterMap.get(liquid) * 100 - 100, 2) + "%").pad(5);
                        }).right().grow().pad(10f).padRight(15f);
                    }).growX().pad(5).row();
                }
            }).growX().colspan(table.getColumns());
            table.row();
            table.add(Core.bundle.get("stat.twilight-fall-base-consume") + Strings.autoFixed(consumeAmount * 60, 2) + StatUnit.perSecond.localized()).left();
        });
    }

    @Override
    public void load() {
        super.load();
        if(drawer != null)
            drawer.load(this);
    }

    @Override
    protected TextureRegion[] icons() {
        return drawer == null ? super.icons() : drawer.icons(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        if(drawer == null) super.drawPlanRegion(plan, list);
        else drawer.drawPlan(this, plan, list);
    }

    public class WateringBuild extends Building{
        public int lastChange = -2;
        public Seq<GrowAbleBuild> trees = new Seq<>();
        public float charge = Mathf.random(reload);
        public float totalProgress = 0;

        public void updateFindTree(){
            trees.clear();
            indexer.allBuildings(x, y, range, b -> {
                if(b instanceof GrowAbleBuild dt && (b.team == team || b.team == Team.derelict)){
                    trees.addUnique(dt);
                }
            });
        }

        public void updateConsume(){
            Liquid liq = getConsumed();
            if(liq != null){
                liquids.remove(liq, consumeAmount * trees.size * edelta());
            }
        }

        public float realBoost(){
            Liquid cl = getConsumed();
            return cl != null && waterMap.containsKey(cl) ? waterMap.get(cl) * efficiency : 1;
        }

        public void updateWaterEffect(Liquid cl, float mt){
            if(waterEffect == Fx.none || cl == null) return;
            for(int i = 0; i < waterO; i++){
                float O = 360f/waterO * i + waterRotation;
                if(Angles.within(totalProgress%360, O, waterSpread) && Mathf.chance(mt)) waterEffect.at(x, y, 0, cl.color);
            }
        }

        @Override
        public void updateTile() {
            if(lastChange != world.tileChanges){
                lastChange = world.tileChanges;
                updateFindTree();
            }

            Liquid cl = getConsumed();
            if(cl != null && trees.size > 0) {
                float mt = liquids.get(cl)/liquidCapacity;
                charge += Time.delta;
                totalProgress += (Time.delta * rotationSpeed * mt);
                updateConsume();
                if (charge >= reload) {
                    charge = 0;
                    trees.removeAll(b -> b == null || b.dead);
                    if (trees.size > 0) {
                        for (GrowAbleBuild b : trees) {
                            b.applySpeed(realBoost(), (reload + 1) * mt);
                            if (b.block != null){
                                for(int i = 0; i < 12 * mt; i++){
                                    wateringEffect.at(
                                            b.x + Mathf.random(-b.block.size * tilesize, b.block.size * tilesize),
                                            b.y + Mathf.random(-b.block.size * tilesize, b.block.size * tilesize),
                                            cl.color
                                    );
                                }
                            }
                        }
                    }
                }
                updateWaterEffect(cl, mt);
            }
        }

        public @Nullable
        Liquid getConsumed(){
            if(waterMap.containsKey(liquids.current()) && liquids.currentAmount() > 0){
                return liquids.current();
            }

            var liqs = content.liquids();

            for(int i = 0; i < liqs.size; i++){
                var liq = liqs.get(i);
                if(waterMap.containsKey(liq) && liquids.get(liq) > 0){
                    return liq;
                }
            }
            return null;
        }

        @Override
        public void draw() {
            if(drawer != null) drawer.draw(this);
            else super.draw();
        }

        @Override
        public void displayConsumption(Table table) {
            super.displayConsumption(table);
            Seq<Liquid> list = content.liquids().select(l -> !l.isHidden() && waterMap.containsKey(l));
            MultiReqImage image = new MultiReqImage();
            list.each(liquid -> image.add(new ReqImage(liquid.uiIcon, () -> getConsumed() == liquid)));

            table.add(image).size(8 * 4);
        }

        @Override
        public void drawSelect(){
            Liquid cl = getConsumed();
            if(cl != null) indexer.eachBlock(this, range, other -> other instanceof GrowAbleBuild, other -> Drawf.selected(other, Tmp.c1.set(cl.color).a(Mathf.absin(4f, 1f))));

            Drawf.dashCircle(x, y, range, team.color);
        }

        @Override
        public float totalProgress() {
            return totalProgress;
        }
    }
}
