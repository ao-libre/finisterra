package game.handlers;

/**
 * Each resource handler should keep its state updated
 */
public enum StateHandler {

    LOADING,

    LOADED,

    UNLOADING,

    UNLOADED,

    FAIL_TO_LOAD
}
