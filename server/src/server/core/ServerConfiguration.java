package server.core;

import com.artemis.BaseSystem;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;

import com.esotericsoftware.minlog.Log;

public class ServerConfiguration extends BaseSystem {
    private static final String serverConfigfile = "C:\\Users\\katerina\\Desktop\\Server.json";

    public enum netPortType {
        port_TCP,
        port_UDP
    }

    public ServerConfiguration() {
        setOutputType(JsonWriter.OutputType.json);
        setIgnoreUnknownFields(true);
    }

    public static void load(String[] args) {

        try {
            // read the json file
            FileReader reader = new FileReader(serverConfigfile);

            JSONParser jsonParser = new JSONParser();
            JSONObject structNetwork = (JSONObject) jsonParser.parse(reader);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Log.info("No se ha encontrado el archivo de configuracion en: " + serverConfigfile);
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.info("No se ha podido acceder al archivo de configuracion en: " + serverConfigfile);
        } catch (ParseException ex) {
            ex.printStackTrace();
            Log.info("No se ha podido parsear el archivo de configuracion en: " + serverConfigfile);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            Log.info("Un hermoso error de puntero nulo al tratar de leer la configuracion del servidor... :'(");
        }

    }

    public string getServerPort(netPortType portType) {

        switch(portType) {
            case netPortType.port_TCP:
                return structNetwork.get("TCP");

            case netPortType.port_UDP:
                return structNetwork.get("UDP");
        }

    }
}
