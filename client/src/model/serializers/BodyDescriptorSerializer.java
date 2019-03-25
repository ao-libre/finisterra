package model.serializers;

import model.descriptors.BodyDescriptor;
import model.descriptors.Descriptor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class BodyDescriptorSerializer implements Json.Serializer {
    @Override
    public void write(Json json, Object object, Class knownType) {
        // Do nothing
    }

    @Override
    public Object read(Json json, JsonValue jsonData, Class type) {
        BodyDescriptor body = new BodyDescriptor();
        if (jsonData.size == 0) {
            return body;
        }
        readDescriptor(jsonData, body);
        body.headOffsetX = jsonData.getInt("headOffsetX", 0);
        body.headOffsetY = jsonData.getInt("headOffsetY", 0);
        return body;
    }

    private void readDescriptor(JsonValue jsonData, Descriptor descriptor) {
        descriptor.setId(jsonData.getInt("id", 0));
        int[] indexs = new int[4];
        indexs[0] = jsonData.getInt("up");
        indexs[1] = jsonData.getInt("right");
        indexs[2] = jsonData.getInt("down");
        indexs[3] = jsonData.getInt("left");
        descriptor.setIndexs(indexs);
    }
}
