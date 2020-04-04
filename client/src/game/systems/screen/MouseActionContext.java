package game.systems.screen;

import game.utils.CursorSystem;
import component.position.WorldPos;

import java.util.function.Consumer;

public class MouseActionContext {

    private CursorSystem.AOCursor cursor;
    private Consumer<WorldPos> action;

    public MouseActionContext(CursorSystem.AOCursor cursor, Consumer<WorldPos> action) {
        this.cursor = cursor;
        this.action = action;
    }

    public CursorSystem.AOCursor getCursor() {
        return cursor;
    }

    public void run(WorldPos pos) {
        this.action.accept(pos);
    }
}

