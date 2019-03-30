package server.manager;

import server.core.Server;

public abstract class DefaultManager implements IManager {

    private Server server;

    public DefaultManager(Server server) {
        this.server = server;
        init();
    }

    @Override
    public Server getServer() {
        return server;
    }

    public abstract void init();


}
