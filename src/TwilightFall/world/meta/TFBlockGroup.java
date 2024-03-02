package TwilightFall.world.meta;

public enum TFBlockGroup {
    none, dark;

    public boolean anyReplace;
    TFBlockGroup(boolean anyReplace){
        this.anyReplace = anyReplace;
    }
    TFBlockGroup(){
        this(true);
    }
}
