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
import game.systems.resources.ObjectSystem;
import game.utils.Skins;
import shared.objects.types.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static com.artemis.E.E;

public class CharacterScreen extends ScreenAdapter {

    public static World world;
    private static int player;
    private final Chooser chooser;
    private FPSLogger logger;
    private GameState state;

    public CharacterScreen(World world, int player) {
        CharacterScreen.world = world;
        CharacterScreen.player = player;
        this.logger = new FPSLogger();
        chooser = new Chooser();
    }

    private static void change(Part part, Obj obj) {
        E entity = E(player);
        switch (part) {
            case BODY:
                entity.removeBody();
                entity.armorIndex(obj.getId());
                entity.body().getBody().index = ((ArmorObj) obj).getBodyNumber();
                break;
            case HELMET:
                entity.removeHelmet();
                entity.helmetIndex(obj.getId());
                break;
            case SHIELD:
                entity.removeShield();
                entity.shieldIndex(obj.getId());
                break;
            case WEAPON:
                entity.removeWeapon();
                entity.weaponIndex(obj.getId());
                break;
        }
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

    enum Part {
        BODY,
        HELMET,
        SHIELD,
        WEAPON
    }

    private static class Chooser extends Window {
        private final Stage stage;
        private ObjectSystem objectSystem;

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
                SelectBox<Obj> select = new SelectBox<>(Skins.COMODORE_SKIN);
                Obj[] items = getItems(part).toArray(new Obj[0]);
                Arrays.sort(items, Comparator.comparingInt(Obj::getId));
                select.setItems(items);
                select.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (actor == select) {
                            CharacterScreen.change(part, select.getSelected());
                        }
                    }
                });
                table.add(select).right().width(200).row();
            }
            stage.addActor(table);
            stage.setKeyboardFocus(table);
        }

        private Set<Obj> getItems(Part part) {
            switch (part) {
                case WEAPON:
                    return objectSystem.getTypeObjects(Type.WEAPON).stream().map(WeaponObj.class::cast).collect(Collectors.toSet());
                case SHIELD:
                    return objectSystem.getTypeObjects(Type.SHIELD).stream().map(ShieldObj.class::cast).collect(Collectors.toSet());
                case BODY:
                    return objectSystem.getTypeObjects(Type.ARMOR).stream().map(ArmorObj.class::cast).collect(Collectors.toSet());
                case HELMET:
                    return objectSystem.getTypeObjects(Type.HELMET).stream().map(HelmetObj.class::cast).collect(Collectors.toSet());
            }
            return Collections.emptySet();
        }

        public void update(float delta) {
            stage.act(delta);
            stage.draw();
        }
    }

}
