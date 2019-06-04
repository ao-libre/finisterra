package server.core;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import shared.util.SharedResources;

public class ServerConfiguration extends BaseSystem {

    public int port_TCP;
    public int port_UDP;

    public static ServerConfiguration loadConfig(String path) {
        Json configObject = new Json();

        setOutputType(JsonWriter.OutputType.json);// esto hace que cuando escribas con el toJson lo guarde en formato json)
        setIgnoreUnknownFields(true); // hace que si no conoce un campo, lo ignore

        return configObject.fromJson(Gdx.files.internal(SharedResources.SERVER_CONFIGURATION_FILE), ServerConfiguration.class)

    }

    // -------------------------------------------------------------------
    // S  E  T
    // -------------------------------------------------------------------

    public void setPort_UDP(int port_UDP) {
        this.port_UDP = port_UDP;
    }

    public void setPort_TCP(int port_TCP) {
        this.port_TCP = port_TCP;
    }

    // -------------------------------------------------------------------
    // G  E  T
    // -------------------------------------------------------------------
    public int getPort_TCP() {
        return port_TCP;
    }

    public int getPort_UDP() {
        return port_UDP;
    }


}
