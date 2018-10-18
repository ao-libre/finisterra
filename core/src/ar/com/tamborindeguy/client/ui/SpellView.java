package ar.com.tamborindeguy.client.ui;

import ar.com.tamborindeguy.client.handlers.SpellHandler;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.client.utils.Skins;
import ar.com.tamborindeguy.model.Spell;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import static com.artemis.E.E;

public class SpellView extends Window {

    private List<Spell> spells;
    private ScrollPane pane;
    private boolean over;

    public SpellView() {
        super("Spells", Skins.COMODORE_SKIN, "black");
        padTop(15 * Inventory.ZOOM);
        spells = new List<>(Skins.COMODORE_SKIN, "black");
        spells.setItems(SpellHandler.getSpells());
        pane = new ScrollPane(spells, Skins.COMODORE_SKIN);
        pane.setTransform(true);
        pane.setSmoothScrolling(false);
        add(pane).fillX().height(80).row();
        Label lanzar = new Label("Lanzar", Skins.COMODORE_SKIN);
        lanzar.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                E(GameScreen.getPlayer()).fXAddFx(spells.getSelected().getFxGrh() - 1);
            }
        });
        add(lanzar).align(Align.left);
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                over = isOver();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                over = isOver();
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, over ? parentAlpha : 0.5f);
    }
}
