package shared.network.lobby;

import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

public class StartGameResponse implements IResponse {

    private String host;
    private int tcpPort;
    private int udpPort;

    private StartGameResponse() {
    }

    public StartGameResponse(String host, int tcpPort, int udpPort) {
        this.host = host;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public String getHost() {
        return host;
    }

    @Override
    public void accept(IResponseProcessor processor) {
        processor.processResponse(this);
    }
}
