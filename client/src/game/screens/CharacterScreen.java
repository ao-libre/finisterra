package game.screens;

import com.artemis.E;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import game.handlers.ObjectHandler;
import game.utils.Skins;
import shared.objects.types.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.artemis.E.E;

public class CharacterScreen extends ScreenAdapter {

    private final Chooser chooser;

    enum Part {
        BODY,
        HELMET,
        SHIELD,
        WEAPON
    }

    public static World world;
    private static int player;
    private FPSLogger logger;
    private GameState state;

    public CharacterScreen(World world, int player) {
        CharacterScreen.world = world;
        CharacterScreen.player = player;
        this.logger = new FPSLogger();
        chooser = new Chooser();
    }

    protected void update(float deltaTime) {
        this.logger.log();
        world.setDelta(MathUtils.clamp(deltaTime, 0, 1 / 16f));
        world.process();
    }

    @Override
    public void show() {
        this.state = GameState.RUNNING;
    }

    @Override
    public void render(float delta) {
        this.update(delta);
        drawUI(delta);
    }

    private void drawUI(float delta) {
        chooser.update(delta);
    }

    private static void change(Part part, int index) {
        E entity = E(player);
        switch (part) {
            case BODY:
                entity.removeBody();
                entity.bodyIndex(index);
                break;
            case HELMET:
                entity.removeHelmet();
                entity.helmetIndex(index);
                break;
            case SHIELD:
                entity.removeShield();
                entity.shieldIndex(index);
                break;
            case WEAPON:
                entity.removeWeapon();
                entity.weaponIndex(index);
                break;
        }
    }

    @Override
    public void pause() {
        if (this.state == GameState.RUNNING) {
            this.state = GameState.PAUSED;
        }
    }

    @Override
    public void resume() {
        if (this.state == GameState.PAUSED) {
            this.state = GameState.RUNNING;
        }
    }

    private static class Chooser extends Window {

        private final Stage stage;

        Chooser() {
            super("Choose", Skins.COMODORE_SKIN, "black");
            stage = new Stage();
            createUI();
            Gdx.input.setInputProcessor(stage);
        }

        private void createUI() {
            Table table = new Table(Skins.COMODORE_SKIN);
            table.setFillParent(true);
            table.right();
            for (Part part : Part.values()) {
                Label label = new Label(part.name(), Skins.COMODORE_SKIN);
                table.add(label).left().padTop(20).row();
                SelectBox<Integer> select = new SelectBox<>(Skins.COMODORE_SKIN);
                Integer[] items = getItems(part).toArray(new Integer[0]);
                Arrays.sort(items);
                select.setItems(items);
                select.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (actor == select) {
                            CharacterScreen.change(part, select.getSelected());
                        }
                    }
                });
                table.add(select).right().width(100).row();
            }
            stage.addActor(table);
            stage.setKeyboardFocus(table);
        }

        private Set<Integer> getItems(Part part) {
            switch (part) {
                case WEAPON:
                    return ObjectHandler.getTypeObjects(Type.WEAPON).stream().map(WeaponObj.class::cast).map(WeaponObj::getAnimationId).collect(Collectors.toSet());
                case SHIELD:
                    return ObjectHandler.getTypeObjects(Type.SHIELD).stream().map(ShieldObj.class::cast).map(ShieldObj::getAnimationId).collect(Collectors.toSet());
                case BODY:
                    return ObjectHandler.getTypeObjects(Type.ARMOR).stream().map(ArmorObj.class::cast).map(ArmorObj::getBodyNumber).collect(Collectors.toSet());
                case HELMET:
                    return ObjectHandler.getTypeObjects(Type.HELMET).stream().map(HelmetObj.class::cast).map(HelmetObj::getAnimationId).collect(Collectors.toSet());
            }
            return Collections.emptySet();
        }

        public void update(float delta) {
            stage.act(delta);
            stage.draw();
        }
    }

}
