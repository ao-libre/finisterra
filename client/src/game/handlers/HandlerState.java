package game.handlers;

/**
 * Each resource handler should keep its state updated
 */
public enum HandlerState {

    LOADING,

    LOADED,

    UNLOADING,

    UNLOADED,

    FAIL_TO_LOAD
}
