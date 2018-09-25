package ar.com.tamborindeguy.model.loaders;

import ar.com.tamborindeguy.model.Graphic;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

public class GraphicsLoader {

    public static void main(String[] args) {
        // read json


    }

//    public static Map<Integer, Graphic> readGraphics() {
//        Json json = new Json();
//        json.setSerializer(Graphic.class, new GraphicsSerializer());
//        try {
//            Map<Integer, Graphic> graphicsMap = new HashMap<>();
//            ArrayList<Graphic> graphics = json.fromJson(ArrayList.class, Graphic.class, fileReader);
//            return graphics.stream().collect(Collectors.toMap(Graphic::getGrhIndex, Function.identity()));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return new HashMap<>();
//    }

    public static ArrayList read(FileHandle file, Class type, Json.Serializer serializer) {
        Json json = new Json();
        json.setSerializer(type, serializer);
        return json.fromJson(ArrayList.class, type, file);
    }

    public static class GraphicsSerializer implements Json.Serializer {

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
}
