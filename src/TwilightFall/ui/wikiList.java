package TwilightFall.ui;

import TwilightFall.wiki.Wiki;
import arc.Core;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.input.Binding;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static TwilightFall.TwilightFallMod.name;

public class wikiList extends BaseDialog {
    Seq<Wiki> wikis = new Seq<>();

    public wikiList(){
        super("wiki");
        addCloseButton();
    }

    public void addContent(Wiki wiki){
        wikis.add(wiki);
    }

    public void addAll(Wiki... ws){
        wikis.addAll(ws);
    }

    public void reset(){
        wikis.clear();
    }

    public void show(wikiDialogs wd){
        cont.clear();
        Table t = new Table();
        t.margin(8);
        t.table(l -> {
            var logo = Core.atlas.find(name("logo"));
            if(logo.found()) {
                float lw = Core.graphics.getWidth() - logo.scl();
                l.image(logo).size(lw, lw * logo.height / logo.width).center().pad(4);
            }
        }).center().pad(5).margin(8);
        t.row();
        t.table(txt -> txt.add(Core.bundle.get("wiki.twilight-fall-choiceOne")).pad(5)).pad(5);
        if(wikis.size > 0){
            t.row();
            t.table(Styles.grayPanel, tw -> {
                tw.defaults().center();
                int i = 0;
                for(var w : wikis){
                    if(i % 2 == 0) tw.row();
                    tw.table(wk -> {
                        wk.defaults().left().growX().size(Math.max(Core.graphics.getWidth()/4f, 100), 50);
                        wk.button(w.displayName, Styles.flatt, () -> wd.show(w)).pad(4).left();
                    }).center().pad(3).margin(4);
                    i++;
                }
            });
        }

        ScrollPane pane = new ScrollPane(t);
        cont.add(pane);
        show();
    }
}
