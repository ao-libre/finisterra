package launcher;

import com.badlogic.gdx.Game;

public class DesignCenter extends Game {

    @Override public void create() {
//        SeparateObjByType.run("output/objects/");
        SpellsToJson.run("output/spells/");
    }
}
