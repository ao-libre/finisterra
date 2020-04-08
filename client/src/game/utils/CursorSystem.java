package game.utils;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import net.mostlyoriginal.api.system.core.PassiveSystem;

@Wire
public class CursorSystem extends PassiveSystem {

    private static ObjectMap<AOCursor, Cursor> cursors = new ObjectMap<>();
    private static AOCursor current;

    public CursorSystem() {
        // load cursors on current context (screen size)
        createCursors();
    }

    private void createCursors() {
        for (AOCursor cursor : AOCursor.values()) {
            cursors.put(cursor, loadCursor(cursor.name));
        }
    }

    @Override
    protected void initialize() {
        setCursor(AOCursor.HAND);
    }

    public void reload() {
        cursors.forEach(v -> v.value.dispose());
        createCursors();
        initialize();
    }

    public void setCursor(AOCursor cursor) {
        current = cursor;
        updateCursor();
    }

    private Cursor loadCursor(String name) {
        Texture texture = new Texture(Resources.GAME_UI_PATH + "cursors/" + name + ".png");
        texture.getTextureData().prepare();

        Pixmap pixmap = texture.getTextureData().consumePixmap();
        Pixmap out = Pixmaps.outline(pixmap, new Color(0, 0, 0, 0.1f));
        Pixmap out2 = Pixmaps.scale(out, getScale());

        out.dispose();
        pixmap.dispose();

        return Gdx.graphics.newCursor(out2, out2.getWidth() / 2, out2.getHeight() / 2);
    }

    private int getScale() {
        return Gdx.graphics.getWidth() > 2000 ? 4 : 2;
    }

    private void updateCursor() {
        if (current != null) {
            Gdx.graphics.setCursor(cursors.get(current));
        }
    }

    public enum AOCursor {
        ARROW("arrow"),
        AXE("axe"),
        CURSOR("cursor"),
        HAND("hand"),
        SELECT("select"),
        ALTERNATIVE_SELECT("select3"),
        IBAR("ibar"),
        PICKAXE("pickaxe");

        private String name;

        AOCursor(String fileName) {
            this.name = fileName;
        }

    }
}

