package shared.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class AOJson extends Json {

    public AOJson() {
        setOutputType(JsonWriter.OutputType.json);
        setIgnoreUnknownFields(true);
    }

}
