package ar.com.tamborindeguy.client.handlers;

import ar.com.tamborindeguy.client.game.AO;
import ar.com.tamborindeguy.interfaces.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

import java.util.HashMap;

public class SurfaceHandler implements Constants {

    private static HashMap<String, Texture> surfaces = new HashMap<String, Texture>();
    private static String graphicsPath = AO.GAME_GRAPHICS_PATH;

    public static void loadAllTextures() {
        FileHandle file = Gdx.app.getFiles().internal(graphicsPath);
        if (file.isDirectory()) {
            for (FileHandle tmp : file.list()) {
                if (tmp.extension() == AO.GAME_GRAPHICS_EXTENSION) {
                    Gdx.app.debug(SurfaceHandler.class.getSimpleName(), "Cargando " + tmp.name());
                    SurfaceHandler.loadTexture(tmp.nameWithoutExtension());
                }
            }
        }
    }

    public static void loadTexture(String fileName) {
        Texture texture = new Texture(graphicsPath + fileName + AO.GAME_GRAPHICS_EXTENSION);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        SurfaceHandler.add(fileName, texture);
    }

    public static void add(final String key, final Texture texture) {
        if (SurfaceHandler.surfaces.containsKey(key)) {
            return;
        }

        SurfaceHandler.surfaces.put(key, texture);
    }

    public static Texture get(final String key) {
        if (!SurfaceHandler.surfaces.containsKey(key)) SurfaceHandler.loadTexture(key);
        return SurfaceHandler.surfaces.get(key);
    }

    public static void dispose(final String key) {
        if (!SurfaceHandler.surfaces.containsKey(key)) {
            return;
        }

        final Texture t = SurfaceHandler.surfaces.get(key);
        t.dispose();
        SurfaceHandler.surfaces.remove(key);
    }

    public static void disposeAll() {
        for (final Texture t : SurfaceHandler.surfaces.values()) {
            t.dispose();
        }

        SurfaceHandler.surfaces.clear();
    }

}
