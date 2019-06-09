package launcher;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import design.Scale2x;
import game.utils.Resources;
import graphics.GraphicScalator;
import json.MapsToJson;
import map.MapsToWorld;
import json.DoorsToJson;
import json.SeparateObjByType;

import java.io.File;
import java.util.Objects;
import java.util.stream.Stream;

public class DesignCenter extends Game {

    public static final String OUTPUT_FOLDER = "output/";
    public static final String DATA_GRAFICOS = Resources.GAME_GRAPHICS_PATH;
    public static final String DATA_GRAFICOS_2_X = Resources.GAME_DATA_PATH + "graficos2x/";

    @Override
    public void create() {
        MapsToJson.transformToJson();
    }



}
