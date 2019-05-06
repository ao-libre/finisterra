package launcher;

import com.badlogic.gdx.Game;
import json.GraphicsToJson;

public class DesignCenter extends Game {

    public static final String OUTPUT_FOLDER = "/output/";


    @Override
    public void create() {
//        SeparateObjByType.run(OUTPUT_FOLDER);
//        SpellsToJson.run(OUTPUT_FOLDER);
        GraphicsToJson.run(OUTPUT_FOLDER);
    }
}
