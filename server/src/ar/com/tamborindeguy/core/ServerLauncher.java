package ar.com.tamborindeguy.core;

import com.esotericsoftware.minlog.Log;

public class ServerLauncher {

    private static WorldServer worldServer;

    public static void main(String[] args) {
        initWorld();
        System.out.println("Server started!");
    }

    private static void initWorld() {
        Log.set(Log.LEVEL_DEBUG);
        worldServer = new WorldServer();
        worldServer.initSystems();
        worldServer.createWorld();
        worldServer.start();

    }
}
