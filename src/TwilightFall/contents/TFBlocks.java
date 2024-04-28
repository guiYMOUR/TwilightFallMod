package TwilightFall.contents;

import TwilightFall.entities.bullets.ChainLightningFade;
import TwilightFall.entities.bullets.RainbowCtLaser;
import TwilightFall.entities.parts.gatherPart;
import TwilightFall.entities.parts.gatherRegionPart;
import TwilightFall.entities.parts.rainbowPart;
import TwilightFall.world.blocks.dark.DarkBlock;
import TwilightFall.world.blocks.dark.DarkBridge;
import TwilightFall.world.blocks.dark.DarkProducer;
import TwilightFall.world.blocks.dark.DarkTube;
import TwilightFall.world.blocks.defense.turrets.DartContinuousTurret;
import TwilightFall.world.blocks.environment.*;
import TwilightFall.world.blocks.product.AnyMtiCrafter;
import TwilightFall.world.blocks.product.DarkCrafter;
import TwilightFall.world.blocks.product.Harvester;
import TwilightFall.world.blocks.product.TFDrill;
import TwilightFall.world.consume.ConsumeDark;
import TwilightFall.world.drawer.DrawBottom;
import TwilightFall.world.drawer.SizedBottom;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.struct.ObjectMap;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.*;

import static TwilightFall.TwilightFallMod.name;
import static TwilightFall.contents.TFGet.base;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.tilesize;
import static mindustry.type.ItemStack.with;

public class TFBlocks {
    public static Block
            //ore
            oreChromium,
            //dark
            darkExtractor, darkLine, darkBridge, darkLimBridge, darkCrafter, darkCrafter2,
            darkFlower, solidChromium, darkTree, chromiumTree,
            pothos,

            //drill
            grindDrillBasic,

            //product
            midFlowerHarvester, flowerHarvester, waetingor, baseMixer,

            //turret
            spread, crossedRainbow;

    //test
    public static Block
        t1, t2;

    public static void load() {

        oreChromium = new TTOre("ore-chromium", TFItems.chromium){{
            variants = 3;
            variantsLarge = 2;
            oreScale = 23.95f;
        }};

        darkExtractor = new DarkProducer("dark-extractor") {{
            requirements(Category.power, with());
            outputDark = 10;
            size = 2;
        }};
        darkLine = new DarkTube("dark-line") {{
            requirements(Category.power, with());
        }};
        darkBridge = new DarkBridge("dark-bridge"){{
            requirements(Category.power, with());
        }};
        darkLimBridge = new DarkBridge("dark-lim-bridge"){{
            requirements(Category.power, with());
            darkCapacity = 10;
            range = 8;
            minRange = 4;
            drawer = new DrawMulti(new SizedBottom(), new DrawDefault(), new DrawRegion("-top"));
        }};

        darkCrafter = new DarkCrafter("d1") {{
            requirements(Category.crafting, with());
            size = 2;
            outputItem = new ItemStack(TFItems.DarkEng, 1);
            craftTime = 60;

            consume(new ConsumeDark(5));
        }};
        darkCrafter2 = new DarkCrafter("d2") {{
            requirements(Category.crafting, with());
            size = 2;
            outputItem = new ItemStack(TFItems.DarkEng, 1);
            craftTime = 60;

            consume(new ConsumeDark(5, true));
        }};

        pothos = new Pothos("pothos"){{
            requirements(Category.effect, ItemStack.with(TFItems.DarkPetal, 180));
            size = 3;
            range = 20;
            healPercent = 4/60f;
            outputsLiquid = true;
            outputLiquid = new LiquidStack(Liquids.water, 6f / 60f);
            baseColor = Pal.heal;
        }};

        darkFlower = new SolidResources("dark-flower"){{
            requirements(Category.effect, ItemStack.with(TFItems.DarkPetal, 4));
            variants = 2;
        }};
        solidChromium = new SolidResources("solid-chromium"){{
            requirements(Category.effect, ItemStack.with(TFItems.chromium, 2));
            variants = 2;
            placeSound = breakSound = Sounds.breaks;
            desColor = TFItems.chromium.color;
            placeEffect = breakEffect = new Effect(16, e -> {
                color(TFItems.chromium.color);
                stroke(3f - e.fin() * 2f);
                Lines.square(e.x, e.y, tilesize / 2f * e.rotation + e.fin() * 3f);
            });
        }};

        darkTree = new DarkTree("dark-tree"){{
            requirements(Category.effect, ItemStack.with(TFItems.DarkPetal, 80));
            health = 800;
            growTime = 20 * 60f;
            flowerEffect = new Effect(23, e -> {
                color(TFPal.darkEng);
                randLenVectors(e.id, 4, 19f * e.finpow(), (x, y) -> {
                    Fill.square(e.x + x, e.y + y, e.fout() * 4.3f);
                });
            }).layer(Layer.debris);
        }};
        chromiumTree = new DarkTree("chromium-tree"){{
            requirements(Category.effect, ItemStack.with(TFItems.DarkPetal, 100, TFItems.DarkEng, 50, TFItems.chromium, 80));
            health = 1000;
            growTime = 30 * 60f;
            flowerTimer = 12 * 60f;
            flowerAmount = 3;
            darkFlower = solidChromium;
            flowerEffect = new Effect(23, e -> {
                color(TFItems.chromium.color);
                randLenVectors(e.id, 4, 19f * e.finpow(), (x, y) -> {
                    Fill.square(e.x + x, e.y + y, e.fout() * 4.3f);
                });
            }).layer(Layer.debris);
            breakEffect = new Effect(23, e -> {
                float scl = Math.max(e.rotation, 1);
                color(TFItems.chromium.color);
                randLenVectors(e.id, 9, 19f * e.finpow() * scl, (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 3.5f * scl + 0.3f));
            }).layer(Layer.debris);
        }};


        grindDrillBasic = new TFDrill("grind-drill-basic"){{
            requirements(Category.production, with(TFItems.chromium, 32));
            size = 4;
            drillTime = 180;
            hardnessDrillMultiplier = 0;
            tier = 3;
            drawRim = true;
            heatColor = Color.valueOf("BF92F9");
            drawer = new DrawMulti(
                    new DrawBottom(Pal.darkerGray, 3),
                    new DrawFrames(){{
                        frames = 7;
                        sine = false;
                    }},
                    new DrawDefault()
            );

            liquidBoostIntensity = 1.5f;
            consumeLiquid(TFLiquids.lube, 3/60f).boost();
        }};


        midFlowerHarvester = new Harvester("mid-flower-harvester"){{
            requirements(Category.production, with(TFItems.chromium, 32));
            size = 2;
            harvestTime = 9 * 60f;
            itemCapacity = 10;
        }};
        flowerHarvester = new Harvester("flower-harvester"){{
            requirements(Category.production, with(TFItems.chromium, 40, TFItems.DarkEng, 45));
            size = 3;
            harvestTime = 6 * 60f;
            itemCapacity = 20;
        }};

        waetingor = new WateringMachine("wateringor"){{
            requirements(Category.effect, with(TFItems.chromium, 40, TFItems.DarkEng, 45));
            size = 3;
            waterMap = ObjectMap.of(
                    Liquids.water, 1.2f,
                    TFLiquids.nutrient, 2f
            );
            consumeAmount = 2/60f;
            rotationSpeed = 2;

            drawer = new DrawMulti(
                    new DrawBottom(Color.valueOf("#3c3837")),
                    new DrawLiquidTile(),
                    new DrawRegion("-rot", 1),
                    new DrawRegion("-top"),
                    new DrawDefault()
            );
        }};


        baseMixer = new AnyMtiCrafter("base-mix"){{
            requirements(Category.crafting, with(TFItems.chromium, 40, TFItems.DarkEng, 45));
            size = 3;
            useBlockDrawer = false;


            liquidCapacity = 20f;
            products.addAll(
                    new Formula(){{
                        consumeItem(TFItems.DarkPetal, 2);
                        consumeLiquid(Liquids.water, 12/60f);
                        outputLiquids = LiquidStack.with(TFLiquids.nutrient, 12/60f);
                        craftTime = 120f;
                        drawer = new DrawMulti(
                                new DrawRegion("-bottom"),
                                new DrawLiquidTile(Liquids.water),
                                new DrawLiquidTile(TFLiquids.nutrient),
                                base(1.5f)
                        );
                    }},
                    new Formula(){{
                        consumeItem(TFItems.chromium, 1);
                        consumeLiquid(Liquids.oil, 6/60f);
                        outputLiquids = LiquidStack.with(TFLiquids.lube, 6/60f);
                        craftTime = 90f;
                        drawer = new DrawMulti(
                                new DrawRegion("-bottom"),
                                new DrawLiquidTile(Liquids.oil),
                                new DrawLiquidTile(TFLiquids.lube),
                                base(2.5f)
                        );
                    }}
            );
        }};

        spread = new ItemTurret("spread"){{
            requirements(Category.turret, with(TFItems.chromium, 90, Items.lead, 110));

            size = 2;
            scaledHealth = 300;

            reload = 30;
            range = 27.5f * 8f;
            inaccuracy = 5;
            shoot = new ShootBarrel(){{
                barrels = new float[]{
                        -5, -1f, 0,
                        -1.5f, -1, 0,
                        1.5f, -1, 0,
                        5, -1f, 0
                };
                shots = 4;
                shotDelay = 6;
            }};

            outlineColor = Pal.darkOutline;

            shootEffect = Fx.none;

            ammo(
                    Items.lead, new BulletType(){{
                        reloadMultiplier = 0.8f;
                        trailColor = Items.lead.color;
                        trailLength = 10;
                        trailWidth = 3;
                        speed = 7.2f;
                        lifetime = 60f;
                        damage = 26;
                        splashDamage = 20;
                        splashDamageRadius = 3 * 8;
                        hitEffect = despawnEffect = new Effect(24, e -> {
                            Draw.color(Items.lead.color);
                            Angles.randLenVectors(e.id, 7, 32 * e.finpow(), e.rotation, 180, (x, y) -> Fill.square(e.x + x, e.y + y, 6 * e.foutpow()));
                        });
                        shootEffect = Fx.none;
                        smokeEffect = new Effect(30, e -> {
                            Draw.color(Items.lead.color);
                            Angles.randLenVectors(e.id, 4, 40 * e.finpow(), e.rotation, 0, (x, y) -> Fill.square(e.x + x, e.y + y, 5 * e.foutpow()));
                        });
                        hitSound = despawnSound = Sounds.bang;
                        ammoMultiplier = 4;
                    }

                        @Override
                        public void init(Bullet b) {
                            super.init(b);
                            b.lifetime = b.lifetime * 2f;
                        }

                        @Override
                        public void update(Bullet b) {
                            super.update(b);
                            b.initVel(b.rotation(), speed * b.foutpow());
                        }

                        @Override
                        public void draw(Bullet b) {
                            super.draw(b);
                            Draw.color(Items.lead.color);
                            for(int i = 0; i < 4; i++){
                                float r = i * 90 + b.time * 6;
                                float rs = i * 90 + 45 + b.time * 6;
                                Drawf.tri(b.x, b.y, 3f, 8, b.rotation() + r);
                                Drawf.tri(b.x, b.y, 4f, 5, b.rotation() + rs);
                            }
                        }
                    },
                    TFItems.chromium, new BulletType(){{
                        damage = 17;
                        pierceArmor = true;
                        speed = 6.5f;
                        lifetime = 45f;
                        homingDelay = 12f;
                        homingRange = 12 * 8;
                        homingPower = 0.1f;
                        trailColor = TFItems.chromium.color;
                        trailLength = 10;
                        trailWidth = 2.3f;
                        status = TFStatusEffects.pierce;
                        statusDuration = 3 * 60f;
                        hitEffect = despawnEffect = new Effect(24, e -> {
                            Draw.color(TFItems.chromium.color);
                            Angles.randLenVectors(e.id, 7, 32 * e.finpow(), e.rotation, 0, (x, y) -> Fill.square(e.x + x, e.y + y, 6 * e.foutpow()));
                        });
                        reloadMultiplier = 1.2f;
                        shootEffect = Fx.none;
                        smokeEffect = new Effect(30, e -> {
                            Draw.color(TFItems.chromium.color);
                            Angles.randLenVectors(e.id, 4, 40 * e.finpow(), e.rotation, 90, (x, y) -> Fill.square(e.x + x, e.y + y, 4 * e.foutpow()));
                        });
                        hitSound = despawnSound = Sounds.back;
                    }

                        @Override
                        public void draw(Bullet b) {
                            super.draw(b);
                            Draw.color(trailColor);
                            Drawf.tri(b.x, b.y, 3f, 8, b.rotation());
                            Drawf.tri(b.x, b.y, 2f, 7, b.rotation() + 150);
                            Drawf.tri(b.x, b.y, 2f, 7, b.rotation() - 150);
                        }

                        @Override
                        public void update(Bullet b) {
                            super.update(b);
                            b.initVel(b.rotation(), speed * b.finpow());
                        }
                    }
            );

            drawer = new DrawTurret("reinforced-"){{
                parts.add(new RegionPart("-mid"){{
                    progress = PartProgress.recoil;
                    under = false;
                    moveY = -1.3f;
                }});
            }};

            shootSound = Sounds.sap;
            coolant = consume(new ConsumeLiquid(TFLiquids.lube, 6f / 60f));
            alwaysUnlocked = true;
        }};

        crossedRainbow = new DartContinuousTurret("crossed-rainbow"){{
            requirements(Category.turret, with(TFItems.chromium, 90, Items.lead, 110));
            alwaysUnlocked = true;
            size = 5;
            scaledHealth = 200;
            shootSound = Sounds.malignShoot;
            loopSound = Sounds.techloop;
            shootY = 28f;
            range = 30 * 8f;
            minWarmup = 0.9f;
            shootWarmupSpeed = 0.08f;
            rotateSpeed = 0.8f;
            canOverdrive = false;

            consume(new ConsumeDark(5f, true));

            shootType = new RainbowCtLaser(){{
                colors = new Color[]{Color.valueOf("ff8787").a(0.6f), Color.valueOf("ff8787").a(0.8f), Color.valueOf("ff8787")};

                width = 5;
                length = 30 * 8;
                backLength = 24;
                frontLength = 88;
                step = 12;
                damage = 80;
                backFlow = false;
                shiftSpeed = 5;
                hitEffect = new Effect(24, e -> {
                    Draw.color(TFGet.c2.set(TFGet.rainbowRed).shiftHue(e.time * 12));
                    Angles.randLenVectors(e.id, 3, 32 * e.finpow(), e.rotation, 90, (x, y) -> Fill.square(e.x + x, e.y + y, 6 * e.foutpow()));
                });
                //linAmount = 2;
                spacing = 8;
                pierceArmor = true;
                chain = new ChainLightningFade(12, -1, 1.7f, TFGet.rainbowRed, damage/3, hitEffect){{
                    rainbowColor = true;
                    shift = shiftSpeed;
                    start = 160;
                }};
            }};

            drawer = new DrawTurret("reinforced-"){{
                parts.addAll(
                        new rainbowPart(5, "-blade-left"){{
                            progress = PartProgress.warmup;
                            mirror = false;
                            moveX = -2.2f;
                            moveY = 0.8f;
                            under = true;
                        }},
                        new rainbowPart(5, "-blade-right"){{
                            progress = PartProgress.warmup;
                            mirror = false;
                            moveX = 2.2f;
                            moveY = 0.8f;
                            under = true;
                        }},
                        new rainbowPart(13, "-mid"){{
                            progress = PartProgress.recoil;
                            mirror = false;
                            under = true;
                            shiftSpeed = 4;
                            mul = 150;
                        }},
                        new RegionPart("-wing-left"){{
                            progress = PartProgress.warmup;
                            mirror = false;
                            moveRot = -45f;
                            moveX = 2f;
                            moveY = 6.5f;
                            under = true;
                        }},
                        new RegionPart("-wing-right"){{
                            progress = PartProgress.warmup;
                            mirror = false;
                            moveRot = 45f;
                            moveX = -2f;
                            moveY = 6.5f;
                            under = true;
                        }},
                        new gatherRegionPart(){{
                            gatherRM = 0.85f;
                            rd = 20;
                            x = y = 0;
                            moveY = 20;
                            text = "RAINBOW";
                            width = 6.2f;
                            height = 7f;
                            mainH = 0f;
                            mainW = 0.8f;
                            shiftSpeed = 3;
                            layerLow = Layer.turret;
                        }},
                        new gatherPart(){{
                            gatherRM = 0.85f;
                            rd = 16;
                            x = y = 0;
                            moveY = 20;
                            skEnd = 0f;
                            sk = 1f;
                            layerLow = Layer.turret;
                            shiftSpeed = 3;
                        }},
                        new gatherPart(){{
                            gatherRM = 0.85f;
                            rd = 25;
                            x = y = 0;
                            moveY = 20;
                            skEnd = 0f;
                            sk = 1f;
                            layerLow = Layer.turret;
                            shiftSpeed = 3;
                        }},
                        new gatherRegionPart(){{
                            gatherRM = 0.85f;
                            rd = 20;
                            x = y = 0;
                            moveY = 0.3f;
                            text = "RAINBOW";
                            centerRot = false;
                        }},
                        new gatherPart(){{
                            gatherRM = 0.8f;
                            rd = 17;
                            x = y = 0;
                            moveY = -0.3f;
                            sk = 5.3f;
                            amount = 45;
                            skEnd = 0;
                            alpha = 0.12f;
                            layerHigh = Layer.turret;
                        }},
                        new gatherPart(){{
                            gatherRM = 0.85f;
                            rd = 20;
                            x = y = 0;
                            moveY = -5;
                        }},
                        new gatherPart(){{
                            gatherRM = 0.85f;
                            rd = 20;
                            x = y = 0;
                            moveY = 4.5f;
                        }}
                );
                outlineColor = Pal.darkOutline;
                canOverdrive = false;
            }};
        }};
    }
}