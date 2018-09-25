package ar.com.tamborindeguy.core;

import com.esotericsoftware.minlog.Log;

public class ServerLauncher {

    private static WorldServer worldServer;

    public static void main(String[] args) {
        initWorld();
    }

    private static void initWorld() {
        worldServer = new WorldServer();
        System.out.println("Initializing systems");
        worldServer.initSystems();
        System.out.println("Creating world");
        Log.set(Log.LEVEL_DEBUG);
        worldServer.createWorld();
        worldServer.start();
    }
}
