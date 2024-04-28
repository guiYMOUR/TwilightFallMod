package TwilightFall.ui;

import TwilightFall.wiki.Wiki;
import arc.Core;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import mindustry.input.Binding;
import mindustry.ui.dialogs.BaseDialog;

import java.util.Objects;

public class wikiDialogs extends BaseDialog {
    public wikiDialogs() {
        super("wiki");
        addCloseButton();
    }

    public void show(Wiki wiki){
        cont.clear();
        Table table = new Table();
        table.margin(10).defaults().width(Core.graphics.getWidth() - 2);
        table.table(c -> {
            c.add("[accent]" + wiki.displayName + "[]").center().pad(3);
        }).center().pad(10);
        if(wiki.descLength > 0){
            for(int i = 0; i < wiki.descLength; i++){
                table.row();
                int finalI = i;
                table.table(d -> {
                    d.add(wiki.description[finalI]).wrap().fillX().padLeft(5).width(Core.graphics.getWidth() - Scl.scl(12)).padTop(5).left();
                    d.row();

                    var tr = Core.atlas.find(wiki.descPt[finalI]);
                    if(tr.found()){
                        float w = Math.min(tr.width, Math.min(Core.graphics.getWidth() - tr.scl(), 450));
                        d.image(tr).size(w, w * tr.height/tr.width).left().pad(5);
                    }
                }).center().pad(5);
            }
        }
        ScrollPane pane = new ScrollPane(table);
        cont.add(pane);
        show();
    }
}
