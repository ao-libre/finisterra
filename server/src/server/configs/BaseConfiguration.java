package server.configs;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import shared.util.AOJson;

public abstract class BaseConfiguration {

    private final String path;

    public BaseConfiguration(String path) {
        this.path = path;
    }

    public void save() {
        Json json = new AOJson();
        json.toJson(this, new FileHandle(path));
    }

    public abstract void loadDefaultValues();
}
