package game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

public class Cursors {

    private static ObjectMap<String, Cursor> cursors = new ObjectMap<>();
    private static String cursor;

    public static void setCursor(String name){
        cursor = name;
        updateCursor();
    }

    private static Cursor loadCursor(String name){
        Cursor cursor = cursors.get(name);

        if(cursor == null){
            Texture texture = new Texture(Resources.GAME_UI_PATH + "cursors/"+name+".png");
            texture.getTextureData().prepare();

            Pixmap pixmap = texture.getTextureData().consumePixmap();
            Pixmap out = Pixmaps.outline(pixmap, new Color(0, 0, 0, 0.1f));
            Pixmap out2 = Pixmaps.scale(out, 4);

            out.dispose();
            pixmap.dispose();

            cursor = Gdx.graphics.newCursor(out2, out2.getWidth()/2, out2.getHeight()/2);
            cursors.put(name, cursor);
        }

        return cursor;
    }

    private static void updateCursor(){
        if(cursor != null){
            Gdx.graphics.setCursor(loadCursor(cursor));
        }
    }
}
