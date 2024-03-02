package TwilightFall.world.blocks.darkEng;

import TwilightFall.world.meta.TFBlockGroup;
import mindustry.world.Block;

public class DarkBlockBase extends Block implements DarkBlock{
    public TFBlockGroup group = TFBlockGroup.dark;

    public DarkBlockBase(String name) {
        super(name);
    }

    public boolean outputDark(){
        return true;
    }

    @Override
    public TFBlockGroup group() {
        return group;
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        if(!(other instanceof DarkBlock)) return false;
        return other.replaceable && (other != this || (rotate && quickRotate)) && ((DarkBlock)other).group() == this.group() &&
                (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
    }
}
