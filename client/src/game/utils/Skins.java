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

    static public final String DEFAULT_CHARS = "\u0000ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz1234567890\"!`?'.,;:()[]{}<>|/@\\^$€-%+=#_&~*\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008F\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009F\u00A0\u00A1\u00A2\u00A3\u00A4\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA\u00AB\u00AC\u00AD\u00AE\u00AF\u00B0\u00B1\u00B2\u00B3\u00B4\u00B5\u00B6\u00B7\u00B8\u00B9\u00BA\u00BB\u00BC\u00BD\u00BE\u00BF\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u00C6\u00C7\u00C8\u00C9\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF\u00D0\u00D1\u00D2\u00D3\u00D4\u00D5\u00D6\u00D7\u00D8\u00D9\u00DA\u00DB\u00DC\u00DD\u00DE\u00DF\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u00E6\u00E7\u00E8\u00E9\u00EA\u00EB\u00EC\u00ED\u00EE\u00EF\u00F0\u00F1\u00F2\u00F3\u00F4\u00F5\u00F6\u00F7\u00F8\u00F9\u00FA\u00FB\u00FC\u00FD\u00FE\u00FF";
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
                    parameter.characters = DEFAULT_CHARS;

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
