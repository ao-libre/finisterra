package game.systems.ui.stats;

import com.artemis.E;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import game.ui.WidgetFactory;

public class UserStats extends Table {

    private E player;

    private Label shieldLabel;
    private Label weaponLabel;
    private Label bodyLabel;
    private Label helmetLabel;

    public UserStats(E player) {
        this.player = player;
        createUI();
    }

    private void createUI() {
        bodyLabel = createStat(WidgetFactory.ImageButtons.UI_ARMOR, false);
        helmetLabel = createStat(WidgetFactory.ImageButtons.UI_HELMET, true);
        row();
        weaponLabel = createStat(WidgetFactory.ImageButtons.UI_WEAPON, false);
        shieldLabel = createStat(WidgetFactory.ImageButtons.UI_SHIELD, true);
    }

    private Label createStat(WidgetFactory.ImageButtons image, boolean isRight) {
        Label result = WidgetFactory.createStatLabel("12/10");
        result.setAlignment(isRight ? Align.left : Align.right);
        Table table = new Table();
        final ImageButton imageButton = WidgetFactory.createImageButton(image);
        if (isRight) {
            table.add(result).width(70).height(32);
            table.add(imageButton).padLeft(-24);
        } else {
            table.add(imageButton);
            table.add(result).width(70).height(32).padLeft(-24);
        }
        add(table).spaceLeft(5).spaceRight(5);
        imageButton.toFront();
        return result;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (player.hasBody()) {

        }
    }

}
