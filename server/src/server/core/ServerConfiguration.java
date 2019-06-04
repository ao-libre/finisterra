package server.core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import com.esotericsoftware.minlog.Log;
import org.json.*;

public class ServerConfiguration {
    private static final String serverConfigfile = "C:\\Users\\katerina\\Desktop\\Server.json";

    public enum netPortType {
        port_TCP,
        port_UDP
    }

    public static void main(String[] args) {

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
