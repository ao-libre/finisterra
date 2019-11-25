package server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.esotericsoftware.minlog.Log;
import server.core.Finisterra;
import server.database.model.modifiers.Modifiers;
import server.manager.ConfigurationManager;

public class ServerLauncher {

    public static void main(String[] arg) {
        Log.info("asd " + ConfigurationManager.getInstance().getCharClassConfig().charClasses.get(0).getModifier().getValue(Modifiers.HEALTH));
        Log.info("asd " + ConfigurationManager.getInstance().getCharClassConfig().charClasses.get(5).getModifier().getValue(Modifiers.HEALTH));

        // Launch application
        new HeadlessApplication(new Finisterra(ConfigurationManager.getInstance().getServerConfig()));
    }
}
