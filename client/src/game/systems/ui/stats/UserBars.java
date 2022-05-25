package game.systems.ui.stats;

import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import game.ui.WidgetFactory;

public class UserBars extends Table {

    private E player;

    private ProgressBar energyBar;
    private Label energyLabel;
    private ProgressBar manaBar;
    private Label manaLabel;
    private ProgressBar hpBar;
    private Label hpLabel;

    public UserBars(E player) {
        this.player = player;
        createUI();
    }

    private void createUI() {
        Tuple tuple = addBar(WidgetFactory.ProgressBars.UI_ENERGY, WidgetFactory.ImageButtons.UI_ENERGY);
        energyBar = tuple.progressBar;
        energyLabel = tuple.label;
        if (player.hasStamina()) {
            energyBar.setRange(0, player.staminaMax());
            energyBar.setValue(player.staminaMin());
        }
        row();
        tuple = addBar(WidgetFactory.ProgressBars.UI_HP, WidgetFactory.ImageButtons.UI_HP);
        hpLabel = tuple.label;
        hpBar = tuple.progressBar;
        if (player.hasHealth()) {
            hpBar.setRange(0, player.healthMax());
            hpBar.setValue(player.healthMin());
        }
        row();
        tuple = addBar(WidgetFactory.ProgressBars.UI_MANA, WidgetFactory.ImageButtons.UI_MANA);
        manaLabel = tuple.label;
        manaBar = tuple.progressBar;
        if (player.hasMana()) {
            manaBar.setRange(0, player.manaMax());
            manaBar.setValue(player.manaMin());
        }
    }

    private Tuple addBar(WidgetFactory.ProgressBars bar, WidgetFactory.ImageButtons image) {
        Stack stack = new Stack();
        Table table = new Table();
        ImageButton icon = WidgetFactory.createImageButton(image);
        table.add(icon);
        ProgressBar progressBar = WidgetFactory.createProgressBar(bar);
        progressBar.setAnimateDuration(0.35f);
        progressBar.setAnimateInterpolation(Interpolation.fastSlow);
        table.add(progressBar).width(180).padLeft(-24);
        icon.toFront();
        stack.add(table);

        Table table1 = new Table();
        Label label = WidgetFactory.createBarLabel("");
        label.setAlignment(Align.center);
        table1.add(label).growX().padLeft(12);
        stack.add(table1);

        add(stack);
        return new Tuple(label, progressBar);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (player.hasStamina()) {
            if (player.staminaMax() != energyBar.getMaxValue()) {
                energyBar.setRange(0, player.staminaMax());
            }
            if (player.staminaMin() != energyBar.getValue()) {
                energyBar.setValue(player.staminaMin());
            }
            energyLabel.setText(player.staminaMin() + "/" + player.staminaMax());
        }

        if (player.hasHealth()) {
            if (player.healthMax() != hpBar.getMaxValue()) {
                hpBar.setRange(0, player.staminaMax());
            }
            if (player.healthMin() != hpBar.getValue()) {
                hpBar.setValue(player.healthMin());
                hpLabel.setText(player.healthMin() + "/" + player.healthMax());
            }
            hpLabel.setText(player.healthMin() + "/" + player.healthMax());
        }

        if (player.hasMana()) {
            if (player.manaMax() != manaBar.getMaxValue()) {
                manaBar.setRange(0, player.manaMax());
            }
            if (player.manaMin() != manaBar.getValue()) {
                manaBar.setValue(player.manaMin());
                manaLabel.setText(player.manaMin() + "/" + player.manaMax());
            }
            manaLabel.setText(player.manaMin() + "/" + player.manaMax());
        }
    }

    private static class Tuple {
        private final ProgressBar progressBar;
        private final Label label;

        public Tuple(Label label, ProgressBar progressBar) {
            this.label = label;
            this.progressBar = progressBar;
        }
    }
}
