package TwilightFall.contents;

import TwilightFall.wiki.Wiki;

public class Wikis {
    public static Wiki
        entry, loot, grow;

    public static void initWiki(){
        entry = new Wiki("entry", 3);
        loot = new Wiki("loot", 4);
        grow = new Wiki("grow", 5);
    }
}
