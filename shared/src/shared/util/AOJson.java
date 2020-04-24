package shared.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * Extensión customizada de {@link Json}
 */
public class AOJson extends Json {
    public AOJson() {
        // Output normal JSON, with all its double quotes.
        setOutputType(JsonWriter.OutputType.json);

        // When true, fields in the JSON that are not found on the class
        // will not throw a SerializationException. Default is
        // false.
        setIgnoreUnknownFields(true);

        // Note: By default, the Json class will not write those fields which have values
        // that are identical to a newly constructed instance. If you wish to disable
        // this behavior and include all fields, call Json.setUsePrototypes(false).
        setUsePrototypes(false);
    }
}
