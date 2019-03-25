package server.core;

public class ServerLauncher {

    private static WorldServer worldServer;

    public static void main(String[] args) {
        initWorld();
        System.out.println("Server started!");
    }

    private static void initWorld() {
        worldServer = new WorldServer();
        worldServer.initSystems();
        worldServer.createWorld();
        worldServer.start();

    }
}
