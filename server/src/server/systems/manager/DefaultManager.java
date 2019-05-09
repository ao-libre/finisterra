package server.systems.manager;

import com.artemis.BaseSystem;
import server.core.Server;

public abstract class DefaultManager extends BaseSystem {

    private Server server;

    public DefaultManager(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    @Override
    protected void processSystem() {}
}
