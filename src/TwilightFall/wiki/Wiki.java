package TwilightFall.wiki;

import TwilightFall.TwilightFallMod;
import arc.Core;

public class Wiki {
    public String name;
    public String displayName;
    public int descLength;
    public String[] description;
    public String[] descPt;

    public Wiki(String name){
        this.name = name;
        this.displayName = Core.bundle.get("wiki.twilight-fall." + name + ".name");
    }

    public Wiki(String name, int descLength){
        this(name);
        this.descLength = descLength;

        description = new String[descLength];
        descPt = new String[descLength];
        for(int i = 0; i < descLength; i++){
            description[i] = Core.bundle.get("wiki.twilight-fall." + name + ".d" + (i + 1));
            descPt[i] = Core.bundle.get("wiki.twilight-fall." + name + ".p" + (i + 1));
        }

        TwilightFallMod.wikiList.addContent(this);
    }
}
