package game.ui;

import game.screens.GameScreen;
import game.utils.Skins;
import game.handlers.SpellHandler;
import shared.model.Spell;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.util.Optional;

import static com.artemis.E.E;

public class SpellView extends Window {

    private List<Spell> spells;
    private ScrollPane pane;
    private boolean over;
    public Optional<Spell> toCast = Optional.empty();

    public SpellView() {
        super("Spells", Skins.COMODORE_SKIN, "black");
        spells = new List<>(Skins.COMODORE_SKIN, "black");
        spells.setItems(SpellHandler.getSpells());
        pane = new ScrollPane(spells, Skins.COMODORE_SKIN);
        pane.setTransform(true);
        pane.setSmoothScrolling(false);
        pane.setFlickScroll(false);
        pane.setScrollingDisabled(true, false);
        add(pane).fillX().fillY().row();
        Label cast = new Label("Lanzar", Skins.COMODORE_SKIN);
        cast.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                toCast = Optional.ofNullable(spells.getSelected());
                toCast.ifPresent(spell -> {
                    Pixmap pm = new Pixmap(Gdx.files.internal("data/ui/images/cursor-crosshair.png"));
                    Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, pm.getWidth() / 2, pm.getHeight() / 2));
                    pm.dispose();
                });
            }

        });
        add(cast).align(Align.left);
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

    public boolean isOver() {
        return over;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (GameScreen.getPlayer() >=0) {
            if (E(GameScreen.getPlayer()).manaMax() <= 0) {
                setVisible(false);
                return;
            }
        }
        super.draw(batch, over ? parentAlpha : parentAlpha * 0.5f);
    }
}
