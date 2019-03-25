package shared.network.interfaces;

public interface IRequest {

    void accept(IRequestProcessor processor, int connectionId);

}
