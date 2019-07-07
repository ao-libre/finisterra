package launcher;

import com.badlogic.gdx.Game;
import game.utils.Resources;
import json.MapsToJson;

public class DesignCenter extends Game {

    public static final String OUTPUT_FOLDER = "output/";
    public static final String DATA_GRAFICOS = Resources.GAME_GRAPHICS_PATH;
    public static final String DATA_GRAFICOS_2_X = Resources.GAME_DATA_PATH + "graficos2x/";

    @Override
    public void create() {
        MapsToJson.transformToJson();
    }


}
