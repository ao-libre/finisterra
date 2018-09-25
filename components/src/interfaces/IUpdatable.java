package interfaces;

public interface IUpdatable {

    default void processUpdate(IUpdateProcessor processor) {
//            processor.process(this);
    }
}
