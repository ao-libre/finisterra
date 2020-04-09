package game.systems.ui.stats;

import com.artemis.E;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.utils.Resources;
import game.utils.Skins;
import net.mostlyoriginal.api.system.core.PassiveSystem;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StatsSystem extends PassiveSystem {

    private IntervalUI actor;

    public void calculate(int entityId) {
        actor = new IntervalUI(E.E(entityId));
    }

    public Actor getActor() {
        return actor;
    }

    public class IntervalUI extends Table {

        private IntervalItem attackIntervalUI;

        private IntervalItem useIntervalUI;
        private E player;

        public IntervalUI(E player) {
            this.player = player;
            attackIntervalUI = new IntervalItem(new Texture(Gdx.files.local(Resources.GAME_GRAPHICS_PATH + "16016.png")));
            useIntervalUI = new IntervalItem(new Texture(Gdx.files.local(Resources.GAME_GRAPHICS_PATH + "23002.png")));
            add(attackIntervalUI).left().row();
            add(useIntervalUI).left();
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (player.hasAttackInterval()) {
                attackIntervalUI.setVisible(true);
                attackIntervalUI.setTime(player.attackIntervalValue());
            } else {
                attackIntervalUI.setVisible(false);
            }

            if (player.hasUseInterval()) {
                useIntervalUI.setVisible(true);
                useIntervalUI.setTime(player.useIntervalValue());
            } else {
                useIntervalUI.setVisible(false);
            }
        }
    }

    public class IntervalItem extends Table {

        public final Texture image;
        public final Label time;

        public IntervalItem(Texture image) {
            this.image = image;
            this.time = new Label("", Skins.COMODORE_SKIN);
            add(new Image(image)).left();
            add(time).right();
        }

        public void setTime(float segs) {
            time.setText(round(segs, 2) + "s");
        }

        BigDecimal round(float d, int decimalPlace) {
            BigDecimal bd = new BigDecimal(Float.toString(d));
            bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
            return bd;
        }

    }
}
