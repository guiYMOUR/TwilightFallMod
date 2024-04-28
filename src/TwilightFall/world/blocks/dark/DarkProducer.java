package TwilightFall.world.blocks.dark;

public class DarkProducer extends DarkBlock{
    public float outputDark = 10;

    public DarkProducer(String name) {
        super(name);

        outputsDark = true;
    }

    public class DarkProducerBuild extends DarkBuild{
        @Override
        public void updateTile() {
            dark = Math.min(darkCapacity, dark + edelta() * outputDark/60);

            dumpDark(2, -1);
        }

        @Override
        public float darkOutPut() {
            return outputDark;
        }
    }
}
