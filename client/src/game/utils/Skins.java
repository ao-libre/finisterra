package game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Skins {

    private static final Skin AO_SKIN = new AOSkin(Gdx.files.internal(Resources.GAME_UI_PATH + "ao-skin-2/" + "ao-skin.json"));
    public static final Skin COMODORE_SKIN = AO_SKIN;

    public static final class AOSkin extends Skin {

        public AOSkin(FileHandle fileHandle) {
            super(fileHandle);
        }

        @Override
        protected Json getJsonLoader(final FileHandle skinFile) {
            Json json = super.getJsonLoader(skinFile);
            final Skin skin = this;

            json.setSerializer(FreeTypeFontGenerator.class, new Json.ReadOnlySerializer<FreeTypeFontGenerator>() {
                @Override
                public FreeTypeFontGenerator read(Json json,
                                                  JsonValue jsonData, Class type) {
                    String path = json.readValue("font", String.class, jsonData);
                    jsonData.remove("font");

                    Hinting hinting = Hinting.valueOf(json.readValue("hinting", String.class, "AutoMedium", jsonData));
                    jsonData.remove("hinting");

                    TextureFilter minFilter = TextureFilter.valueOf(json.readValue("minFilter", String.class, "Nearest", jsonData));
                    jsonData.remove("minFilter");

                    TextureFilter magFilter = TextureFilter.valueOf(json.readValue("magFilter", String.class, "Nearest", jsonData));
                    jsonData.remove("magFilter");

                    FreeTypeFontParameter parameter = json.readValue(FreeTypeFontParameter.class, jsonData);
                    parameter.hinting = hinting;
                    parameter.minFilter = minFilter;
                    parameter.magFilter = magFilter;
                    parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;

                    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(skinFile.parent().child(path));
                    BitmapFont font = generator.generateFont(parameter);
                    skin.add(jsonData.name, font);
                    if (parameter.incremental) {
                        generator.dispose();
                        return null;
                    } else {
                        return generator;
                    }
                }
            });

            return json;
        }
    }
}
