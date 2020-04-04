package game.systems.ui.stats;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import game.utils.Skins;
import net.mostlyoriginal.api.system.core.PassiveSystem;

public class StatsSystem extends PassiveSystem {
    public Actor getActor() {
        // TODO
        return new Label("", Skins.COMODORE_SKIN);
    }
}
