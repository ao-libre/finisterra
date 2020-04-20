package server.utils;

import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;
import component.entity.character.info.Bag;

public class BagJsonSerializer implements JsonSerializer<Bag> {

    @Override
    public void write(Json json, Bag bag, Class knownType) {
        json.writeObjectStart("Bag");
        json.writeArrayStart("items");
        for (Bag.Item item : bag.items) {
            json.writeFields(item);
        }
        json.writeArrayEnd();
        json.writeObjectEnd();
    }

    @Override
    public Bag read(Json json, JsonValue jsonData, Class type) {
        Bag bag = new Bag();
        JsonValue items = jsonData.child;
        if (items != null) {
            int i = 0;
            for (JsonValue value : items) {
                Bag.Item item = null;
                if (!value.type().equals(JsonValue.ValueType.nullValue)) {
                    item = new Bag.Item();
                    json.readFields(item, value);
                }
                bag.set(i, item);
                i++;
            }
        }
        return bag;
    }
}
