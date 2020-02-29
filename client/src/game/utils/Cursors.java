package game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Clase para reemplazar el cursor de windows por un cursor grafico
 */
public class Cursors {

    private static ObjectMap<String, Cursor> cursors = new ObjectMap<>();
    private static String cursor;

    /**
     * Setea un cursor grafico
     * @param name
     */
    public static void setCursor(String name) {
        cursor = name;
        updateCursor();
    }

    /**
     * Metodo que recibe por parametros el nombre de un cursor para cargarlo.
     * @param name
     * @return
     */
    private static Cursor loadCursor(String name) {
        Cursor cursor = cursors.get(name);

        //Si cursor esta vacio, cargamos un nuevo cursor...
        if (cursor == null) {
            //Cargamos el grafico del nuevo cursor
            Texture texture = new Texture(Resources.GAME_UI_PATH + "cursors/" + name + ".png");
            texture.getTextureData().prepare();

            Pixmap pixmap = texture.getTextureData().consumePixmap();
            Pixmap out = Pixmaps.outline(pixmap, new Color(0, 0, 0, 0.1f));
            Pixmap out2 = Pixmaps.scale(out, 2);

            out.dispose();
            pixmap.dispose();

            cursor = Gdx.graphics.newCursor(out2, out2.getWidth() / 2, out2.getHeight() / 2);
            cursors.put(name, cursor);
        }

        return cursor;
    }

    /**
     * Actualiza un cursor grafico
     */
    private static void updateCursor() {
        //¿Hay un cursor? Si lo hay, lo cargamos
        if (cursor != null) {
            Gdx.graphics.setCursor(loadCursor(cursor));
        }
    }
}
