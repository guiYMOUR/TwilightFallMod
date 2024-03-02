package TwilightFall.world.blocks.product;

import TwilightFall.contents.TFGet;
import arc.Core;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.consumers.*;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.*;

import static TwilightFall.TwilightFallMod.name;
import static TwilightFall.contents.TFGet.*;
import static mindustry.Vars.*;

/**
 * @author guiY
 * */

public class AnyMtiCrafter extends Block {
    public Seq<Formula> products = new Seq<>();
    public DrawBlock drawer = new DrawDefault();
    public boolean useBlockDrawer = true;
    public boolean hasDoubleOutput = false;

    public Color liquidTableBack = Pal.gray.cpy().a(0.5f);

    public AnyMtiCrafter(String name) {
        super(name);

        update = true;
        solid = true;
        hasItems = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
        drawArrow = false;

        configurable = true;
        saveConfig = true;

        config(int[].class, (AnyMtiCrafterBuild ent, int[] i) -> {
            if(i.length != 2) return;

            ent.rotation = i[0];

            if(products.size <= 0 || i[1] == -1) ent.formula = null;
            ent.formula = products.get(i[1]);
        });
    }

    @Override
    public void init() {
        if(products.size > 0){
            for(var k : products) k.init();
            for(var k : products){
                if(k.outputLiquids != null) {
                    outputsLiquid = hasLiquids = true;
                    break;
                }
            }
            for(var k : products){
                if(k.outputItems != null){
                    hasItems = true;
                    break;
                }
            }
            for(var k : products){
                if(k.consPower != null){
                    hasPower = true;
                    consume(new ConsumePowerDynamic(b -> b instanceof AnyMtiCrafterBuild ab ? ab.formulaPower() : 0));
                    break;
                }
            }
        }
        super.init();
        hasConsumers = products.size > 0;
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("items");
        removeBar("liquid");
        removeBar("power");
        if(consPower != null){
            addBar("power", (AnyMtiCrafterBuild entity) -> new Bar(
                    () -> Core.bundle.get("bar.twilight-fall-mti-power") + (entity.formulaPower() > 0.01f ? Core.bundle.get("bar.twilight-fall-mti-power-need") : Core.bundle.get("bar.twilight-fall-mti-power-noNeed")),
                    () -> Pal.powerBar,
                    () -> Mathf.zero(consPower.requestedPower(entity)) && entity.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f ? 1f : entity.power.status)
            );
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.output, table -> {
            table.row();
            if(products.size > 0) for(var p : products){
                table.table(Styles.grayPanel, info -> {
                    info.left().defaults().left();
                    Stats stat = new Stats();
                    stat.timePeriod = p.craftTime;
                    if(p.hasConsumers) for(var c : p.consumers){
                        c.display(stat);
                    }
                    if((hasItems && itemCapacity > 0) || p.outputItems != null){
                        stat.add(Stat.productionTime, p.craftTime / 60f, StatUnit.seconds);
                    }

                    if(p.outputItems != null){
                        stat.add(Stat.output, StatValues.items(p.craftTime, p.outputItems));
                    }

                    if(p.outputLiquids != null){
                        stat.add(Stat.output, StatValues.liquids(1f, p.outputLiquids));
                    }
                    info.table(st -> statTurnTable(stat, st)).pad(8).left();
                }).growX().left().pad(10);
                table.row();
            }
        });
    }

    @Override
    public void load() {
        super.load();
        if(useBlockDrawer) drawer.load(this);
        else {
            if(products.size > 0) for(var p : products){
                p.drawer.load(this);
            }
        }
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        if(useBlockDrawer) drawer.drawPlan(this, plan, list);
        else {
            if(products.size > 0) products.get(0).drawer.drawPlan(this, plan, list);
            else super.drawPlanRegion(plan, list);
        }
    }

    @Override
    protected TextureRegion[] icons() {
        return useBlockDrawer ? drawer.icons(this) : products.size > 0 ? products.get(0).drawer.icons(this) : super.icons();
    }

    public class AnyMtiCrafterBuild extends Building{
        public Formula formula = products.size > 0 ? products.get(0) : null;
        public float progress;
        public float totalProgress;
        public float warmup;

        public int[] configs = {0, 0};
        public int lastRotation = -1;

        public TextureRegionDrawable[] rotationIcon = {Icon.right, Icon.up, Icon.left, Icon.down};

        @Override
        public void draw() {
            if(formula == null || useBlockDrawer) drawer.draw(this);
            else formula.drawer.draw(this);
        }

        @Override
        public void drawStatus() {
            if (this.block.enableDrawStatus && formula != null && formula.hasConsumers) {
                float multiplier = block.size > 1 ? 1 : 0.64f;
                float brcx = x + (float)(block.size * 8) / 2f - 8f * multiplier / 2f;
                float brcy = y - (float)(block.size * 8) / 2f + 8f * multiplier / 2f;
                Draw.z(71.0F);
                Draw.color(Pal.gray);
                Fill.square(brcx, brcy, 2.5f * multiplier, 45);
                Draw.color(status().color);
                Fill.square(brcx, brcy, 1.5f * multiplier, 45);
                Draw.color();
            }
        }

        public float warmupTarget(){
            return 1f;
        }

        public float formulaPower(){
            if(formula == null) return 0;
            var consumePower = formula.consPower;
            if(consumePower == null) return 0;
            return consumePower.usage;
        }

        @Override
        public void updateTile(){
            if(lastRotation != rotation){
                Fx.placeBlock.at(x, y, size);
                lastRotation = rotation;
            }

            if(formula == null) return;
            if(efficiency > 0){
                progress += getProgressIncrease(formula.craftTime, formula);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), formula.warmupSpeed);

                if(formula.outputLiquids != null){
                    float inc = getProgressIncrease(1f);
                    for(var output : formula.outputLiquids){
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if(wasVisible && Mathf.chanceDelta(formula.updateEffectChance)){
                    formula.updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, formula.warmupSpeed);
            }

            totalProgress += warmup * Time.delta;

            if(progress >= 1f){
                craft(formula);
            }

            dumpOutputs(formula);
        }

        @Override
        public float totalProgress() {
            return totalProgress;
        }

        public float getProgressIncrease(float baseTime, Formula formula){
            if(formula.ignoreLiquidFullness){
                return super.getProgressIncrease(baseTime);
            }

            float scaling = 1f, max = 1f;
            if(formula.outputLiquids != null){
                max = 0f;
                for(var s : formula.outputLiquids){
                    float value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta());
                    scaling = Math.min(scaling, value);
                    max = Math.max(max, value);
                }
            }

            return super.getProgressIncrease(baseTime) * (formula.dumpExtraLiquid ? Math.min(max, 1f) : scaling);
        }

        public void craft(Formula formula){
            consume();

            if(formula.outputItems != null){
                for(var output : formula.outputItems){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }

            if(wasVisible){
                formula.craftEffect.at(x, y);
            }
            progress %= 1f;
        }

        public void dumpOutputs(Formula formula){
            if(formula.outputItems != null && timer(timerDump, dumpTime / timeScale)){
                for(ItemStack output : formula.outputItems){
                    dump(output.item);
                }
            }

            if(formula.outputLiquids != null){
                for(int i = 0; i < formula.outputLiquids.length; i++){
                    int dir = formula.liquidOutputDirections.length > i ? formula.liquidOutputDirections[i] : -1;

                    dumpLiquid(formula.outputLiquids[i].liquid, 2f, dir);
                }
            }
        }

        @Override
        public boolean shouldConsume(){
            if(formula == null) return false;
            if(formula.outputItems != null){
                for(var output : formula.outputItems){
                    if(items.get(output.item) + output.amount > itemCapacity){
                        return false;
                    }
                }
            }
            if(formula.outputLiquids != null && !formula.ignoreLiquidFullness){
                boolean allFull = true;
                for(var output : formula.outputLiquids){
                    if(liquids.get(output.liquid) >= liquidCapacity - 0.001f){
                        if(!formula.dumpExtraLiquid){
                            return false;
                        }
                    }else{
                        allFull = false;
                    }
                }
                if(allFull){
                    return false;
                }
            }
            return enabled;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if(formula == null) return false;
            return formula.getConsumeItem(item) && this.items.get(item) < this.getMaximumAccepted(item);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if(formula == null) return false;
            return this.block.hasLiquids && formula.getConsumeLiquid(liquid);
        }

        @Override
        public void consume() {
            if(formula == null) return;
            Consume[] c = formula.consumers;

            if(c.length > 0) for(Consume cons : c) {
                cons.trigger(this);
            }
        }

        public void displayConsumption(Table table) {
            if(formula == null) return;
            table.left();
            Formula[] lastFormula = {formula};
            table.table(t -> {
                table.update(() -> {
                    if (lastFormula[0] != formula) {
                        rebuild(t);
                        lastFormula[0] = formula;
                    }
                });
                rebuild(t);
            });
        }

        private void rebuild(Table table){
            table.clear();
            Consume[] c = formula.consumers;
            if(c.length > 0) for (Consume cons : c) {
                if (!cons.optional || !cons.booster) {
                    cons.build(this, table);
                }
            }
        }

        @Override
        public void drawSelect() {
            if(formula == null) return;

            if(formula.outputLiquids != null){
                for(int i = 0; i < formula.outputLiquids.length; i++){
                    int dir = formula.liquidOutputDirections.length > i ? formula.liquidOutputDirections[i] : -1;

                    if(dir != -1){
                        Draw.rect(
                                formula.outputLiquids[i].liquid.fullIcon,
                                x + Geometry.d4x(dir + rotation) * (size * tilesize / 2f + 4),
                                y + Geometry.d4y(dir + rotation) * (size * tilesize / 2f + 4),
                                6f, 8f
                        );
                    }
                }
            }

            var canBar = Core.atlas.find(name("can"));
            float width = 0, height = 32, pad = 4, tw = 32;
            for(int i = 0; i < formula.liquidFilter.size; i++) width += tw;
            if(formula.outputLiquids != null) for(int i = 0; i < formula.outputLiquids.length; i++) width += tw;
            if(width > 0){
                Draw.color(liquidTableBack);
                float realW = width + pad * 2, realH = height + pad * 2;
                float realX = x + size/2f * tilesize - realW/2;
                float realY = y + size/2f * tilesize;
                float vts = tw/4f;
                float boPad = 1;
                Fill.rect(x, realY + realH/2, realW, realH);
                for(var l : content.liquids()){
                    if(formula.getConsumeLiquid(l)) {
                        float ly = realY + pad;
                        Draw.color();
                        Draw.rect(l.uiIcon, realX, ly);
                        TFGet.drawTiledFramesBar(vts, (height * liquids.get(l)/liquidCapacity), realX + vts/2f, ly, l, 1);
                        Draw.color();
                        Draw.alpha(1);
                        Draw.rect(canBar, realX + vts, ly + height/2f, vts + boPad, height + boPad);
                        Fonts.outline.draw(l.localizedName, realX, ly - 1, Color.white, 0.2f, false, Align.center);
                        realX += tw;
                    }
                }
                if(formula.outputLiquids != null){
                    for(int i = 0; i < formula.outputLiquids.length; i++){
                        var ls = formula.outputLiquids[i];
                        float ly = realY + pad;
                        Draw.color();
                        Draw.rect(ls.liquid.uiIcon, realX, ly);
                        TFGet.drawTiledFramesBar(vts, (height * liquids.get(ls.liquid)/liquidCapacity), realX + vts/2f, ly, ls.liquid, 1);
                        Draw.color();
                        Draw.alpha(1);
                        Draw.rect(canBar, realX + vts/2f + vts/2f, ly + height/2f, vts + 1, height + 2);
                        Fonts.outline.draw(ls.liquid.localizedName, realX, ly - 1, Color.white, 0.2f, false, Align.center);
                        realX += tw;
                    }
                }
            }
        }

        @Override
        public void displayBars(Table table) {
            super.displayBars(table);
            if(formula == null) return;
            Formula[] lastFormula = {formula};
            table.table(t -> {
                table.update(() -> {
                    if (lastFormula[0] != formula) {
                        rebuildBar(t);
                        lastFormula[0] = formula;
                    }
                });
                rebuildBar(t);
            });
        }

        private void rebuildBar(Table table){
            table.clear();
            if(formula == null) return;
            if(formula.barMap.size > 0) for(var bar : formula.listBars()){
                var result = bar.get(self());
                if(result == null) continue;
                table.add(result).growX();
                table.row();
            }
        }

        @Override
        public boolean shouldAmbientSound(){
            return efficiency > 0;
        }

        public void updateConsumption() {
            if(formula == null) return;
            if (formula.hasConsumers && !cheating()) {
                if (!enabled) {
                    potentialEfficiency = efficiency = optionalEfficiency = 0;
                } else {
                    boolean update = shouldConsume() && productionValid();
                    float minEfficiency = 1f;
                    efficiency = optionalEfficiency = 1f;
                    Consume[] c = formula.nonOptionalConsumers;
                    int cl = c.length;

                    int i;
                    Consume cons;
                    for(i = 0; i < cl; i++) {
                        cons = c[i];
                        minEfficiency = Math.min(minEfficiency, cons.efficiency(this));
                    }

                    c = formula.optionalConsumers;
                    cl = c.length;

                    for(i = 0; i < cl; i++) {
                        cons = c[i];
                        optionalEfficiency = Math.min(optionalEfficiency, cons.efficiency(this));
                    }

                    efficiency = minEfficiency;
                    optionalEfficiency = Math.min(optionalEfficiency, minEfficiency);
                    potentialEfficiency = efficiency;
                    if (!update) {
                        efficiency = optionalEfficiency = 0.0F;
                    }

                    updateEfficiencyMultiplier();
                    if (update && efficiency > 0.0F) {
                        c = formula.updateConsumers;
                        cl = c.length;

                        for(i = 0; i < cl; i++) {
                            cons = c[i];
                            cons.update(this);
                        }
                    }

                }
            } else {
                potentialEfficiency = enabled && productionValid() ? 1f : 0f;
                efficiency = optionalEfficiency = shouldConsume() ? potentialEfficiency : 0f;
                updateEfficiencyMultiplier();
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            Table rtc = new Table();
            rtc.left().defaults().size(55);

            Table cont = new Table().top();
            cont.left().defaults().left().growX();

            Runnable rebuild = () -> {
                rtc.clearChildren();
                if (hasDoubleOutput) {
                    for (int i = 0; i < rotationIcon.length; i++) {
                        ImageButton button = new ImageButton();
                        int I = i;
                        button.table(img -> img.image(rotationIcon[I]).color(Color.white).size(40).pad(10f));
                        button.changed(() -> {
                            configs[0] = I;
                            configure(configs);
                        });
                        button.update(() -> button.setChecked(rotation == I));
                        button.setStyle(Styles.clearNoneTogglei);
                        rtc.add(button).tooltip("" + i * 90 + "°");
                    }
                }

                cont.clearChildren();
                if(products.size > 0) for(var f : products){
                    ImageButton button = new ImageButton();
                    button.table(info -> {
                        info.left();
                        info.table(from -> {
                            Stats stat = new Stats();
                            stat.timePeriod = f.craftTime;
                            if(f.hasConsumers) for(var c : f.consumers){
                                c.display(stat);
                            }
                            statToTable(stat, from);
                        }).left().pad(6);
                        info.row();
                        info.table(to -> {
                            if(f.outputItems != null){
                                StatValues.items(f.craftTime, f.outputItems).display(to);
                            }

                            if(f.outputLiquids != null){
                                StatValues.liquids(1f, f.outputLiquids).display(to);
                            }
                        }).left().pad(6);
                    }).grow().left().pad(5);
                    button.setStyle(Styles.clearNoneTogglei);
                    button.changed(() -> {
                        configs[1] = products.indexOf(f);
                        configure(configs);
                    });
                    button.update(() -> button.setChecked(formula == f));
                    cont.add(button);
                    cont.row();
                }
            };

            rebuild.run();

            Table main = new Table().background(Styles.black6);

            main.add(rtc).left().row();

            ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
            pane.setScrollingDisabled(true, false);

            if(block != null){
                pane.setScrollYForce(block.selectScroll);
                pane.update(() -> block.selectScroll = pane.getScrollY());
            }

            pane.setOverscroll(false, false);
            main.add(pane).maxHeight(100 * 5);
            table.top().add(main);
        }

        @Override
        public int[] config() {
            return configs;
        }

        @Override
        public void configure(Object value) {
            super.configure(value);
            deselect();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.f(warmup);
            write.i(lastRotation);
            write.i(formula == null || !products.contains(formula) ? -1 : products.indexOf(formula));
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            warmup = read.f();
            lastRotation = read.i();
            int i = read.i();
            formula = i == -1 ? null : products.get(i);
            configs[0] = rotation;
            configs[1] = i;
        }
    }

    public static class Formula{
        public Consume[] consumers = {}, optionalConsumers = {}, nonOptionalConsumers = {}, updateConsumers = {};
        protected Seq<Consume> consumeBuilder = new Seq<>();
        public @Nullable
        ConsumePower consPower;
        public float craftTime;
        public boolean hasConsumers;

        public ItemStack[] outputItems;
        public LiquidStack[] outputLiquids;
        public int[] liquidOutputDirections = {-1};
        public boolean ignoreLiquidFullness = false;
        public boolean dumpExtraLiquid = true;

        public float warmupSpeed = 0.02f;

        public float updateEffectChance = 0.05f;
        public Effect updateEffect = Fx.none;
        public Effect craftEffect = Fx.none;

        public DrawBlock drawer = new DrawDefault();

        public ObjectMap<Item, Boolean> itemFilter = new ObjectMap<>();
        public ObjectMap<Liquid, Boolean> liquidFilter = new ObjectMap<>();
        //TODO may use?
        protected OrderedMap<String, Func<Building, Bar>> barMap = new OrderedMap<>();

        public void init(){
            consumers = consumeBuilder.toArray(Consume.class);
            optionalConsumers = consumeBuilder.select(consume -> consume.optional && !consume.ignore()).toArray(Consume.class);
            nonOptionalConsumers = consumeBuilder.select(consume -> !consume.optional && !consume.ignore()).toArray(Consume.class);
            updateConsumers = consumeBuilder.select(consume -> consume.update && !consume.ignore()).toArray(Consume.class);
            hasConsumers = consumers.length > 0;
        }

        public void setApply(UnlockableContent content){
            if(content instanceof Item item){
                itemFilter.put(item, true);
            }
            if(content instanceof Liquid liquid){
                liquidFilter.put(liquid, true);
            }
        }

        public Iterable<Func<Building, Bar>> listBars(){
            return barMap.values();
        }

        public void addBar(String name, Func<Building, Bar> sup){
            barMap.put(name, sup);
        }

        public boolean getConsumeItem(Item item){
            return itemFilter.containsKey(item) && itemFilter.get(item);
        }

        public boolean getConsumeLiquid(Liquid liquid){
            return liquidFilter.containsKey(liquid) && liquidFilter.get(liquid);
        }

        public void consumeLiquid(Liquid liquid, float amount){
            setApply(liquid);
            consume(new ConsumeLiquid(liquid, amount));
        }

        public void consumeLiquids(LiquidStack... stacks){
            if(stacks.length > 0) for(var s : stacks) setApply(s.liquid);
            consume(new ConsumeLiquids(stacks));
        }

        public void consumePower(float powerPerTick){
            consume(new ConsumePower(powerPerTick, 0.0f, false));
        }

        public void consumeItem(Item item){
            setApply(item);
            consumeItem(item, 1);
        }

        public void consumeItem(Item item, int amount){
            setApply(item);
            consume(new ConsumeItems(new ItemStack[]{new ItemStack(item, amount)}));
        }

        public void consumeItems(ItemStack... items){
            if(items.length > 0) for(var s : items) setApply(s.item);
            consume(new ConsumeItems(items));
        }

        public <T extends Consume> void consume(T consume){
            if(consume instanceof ConsumePower){
                consumeBuilder.removeAll(b -> b instanceof ConsumePower);
                consPower = (ConsumePower)consume;
            }
            consumeBuilder.add(consume);
        }
    }
}