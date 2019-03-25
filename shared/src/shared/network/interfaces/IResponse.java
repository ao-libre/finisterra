package shared.network.interfaces;

public interface IResponse {
    void accept(IResponseProcessor processor);
}
