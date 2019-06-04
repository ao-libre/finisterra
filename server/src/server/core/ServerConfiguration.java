package server.core;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class ServerConfiguration extends BaseSystem {

    private Network network;

    public static ServerConfiguration loadConfig(String path) {
        Json configObject = new Json();

        configObject.setOutputType(JsonWriter.OutputType.json);// esto hace que cuando escribas con el toJson lo guarde en formato json)
        configObject.setIgnoreUnknownFields(true); // hace que si no conoce un campo, lo ignore

        return configObject.fromJson(ServerConfiguration.class, Gdx.files.internal(""));
    }

    // ---------------------------------------------------------------
    // Aca obtenes los valores de las propiedades en el Server.json
    // ---------------------------------------------------------------

    public int getTcpPort() {
        return network.ports.tcp;
    }

    public int getUdpPort() {
        return network.ports.udp;
    }

    // ---------------------------------------------------------------
    // Esto no lo toques...
    // ---------------------------------------------------------------
    @Override
    protected void processSystem() {
        // DO NOTHING
    }

    // ---------------------------------------------------------------------------
    // Aca se auto-setean los valores de las propiedades leidos de el Server.json
    // ---------------------------------------------------------------------------
    // Cada 'Class' es un objeto en el JSON
    // ---------------------------------------------------------------------------

    private static class Network {
        private Ports ports;

        /*
            "network": {
                // Estas acá.
            }
         */
    }

    private static class Ports {
        private final int tcp;
        private final int udp;

        /*
            "network": {
                "ports": {
                    // Estás acá.
                    "TCP": tcp,
                    "UDP": udp
                }
            }
         */

        public Ports(int tcp, int udp) {
            this.tcp = tcp;
            this.udp = udp;
        }

        public int getTcp() {
            return tcp;
        }

        public int getUdp() {
            return udp;
        }
    }
}