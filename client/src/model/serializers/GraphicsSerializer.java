package model.serializers;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import shared.model.Graphic;

public class GraphicsSerializer implements Json.Serializer {

    @Override
    public void write(Json json, Object object, Class knownType) {
        // Do nothing
    }

    @Override
    public Object read(Json json, JsonValue jsonData, Class type) {
        Graphic graphic = new Graphic();
        if (jsonData.size == 0) {
            return graphic;
        }
        graphic.setGrhIndex(jsonData.getInt("grhIndex", 0));
        graphic.setFileNum(jsonData.getInt("fileNum", 0));
        graphic.setHeight(jsonData.getInt("height", 0));
        graphic.setWidth(jsonData.getInt("width", 0));
        graphic.setX(jsonData.getInt("x", 0));
        graphic.setY(jsonData.getInt("y", 0));
        graphic.setSpeed(jsonData.getFloat("speed", 0));

        JsonValue frames = jsonData.get("frames");
        if (frames != null) {
            graphic.setFrames(frames.asIntArray());
        }
        return graphic;
    }
}