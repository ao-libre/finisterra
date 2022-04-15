package game.systems.ui.stats;

import com.artemis.E;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import game.ui.WidgetFactory;

public class UserHeader  extends Table {

    private E player;
    private Label level;
    private ProgressBar exp;
    private Label expLabel;
    private ImageButton levelButton;

    public UserHeader(E player) {
        this.player = player;
        createUI();
    }

    private void createUI() {
        // add label
        add(WidgetFactory.createUserLabel(player.nameText())).colspan(2).center().row();

        Stack levelStack = new Stack();
        // add background
        levelButton = WidgetFactory.createImageButton(WidgetFactory.ImageButtons.UI_EXP);
        levelStack.add(levelButton);
        // add level
        level = WidgetFactory.createBarLabel(player.levelLevel() + "");
        level.setAlignment(Align.center);
        levelStack.add(level);
        add(levelStack);
        // create stack
        Stack barStack = new Stack();
        Table expTable = new Table();
        // add bar
        exp = WidgetFactory.createProgressBar(WidgetFactory.ProgressBars.UI_EXP);
        exp.setAnimateInterpolation(Interpolation.fastSlow);
        exp.setAnimateDuration(0.35f);
        expTable.add(exp).width(180);
        // add text
        Table labelTable = new Table();
        expLabel = WidgetFactory.createBarLabel("");
        expLabel.setAlignment(Align.center);
        labelTable.add(expLabel).width(168).padLeft(12);
        barStack.add(expTable);
        barStack.add(labelTable);

        add(barStack).padLeft(-24);
        levelStack.toFront();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        try {
            if (player.hasLevel() && player.levelLevel() != Integer.parseInt(level.getText().toString())) {
                level.setText(player.levelLevel() + "");
            }
        } catch (Exception e) {
            // Do nothing
        }
        if (player.hasLevel()) {
            if (player.levelExpToNextLevel() != exp.getMaxValue()) {
                exp.setRange(0, player.levelExpToNextLevel());
            }
            if (player.levelExp() != exp.getValue()) {
                exp.setValue(player.levelExp());
            }
            expLabel.setText(player.levelExp() + "/" + player.levelExpToNextLevel());
        }
    }
}
