package shared.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import shared.model.Spell;

import java.util.HashMap;
import java.util.Map;

public class SpellJson extends Json {

    public SpellJson() {
        setOutputType(JsonWriter.OutputType.json);
        setIgnoreUnknownFields(true);
    }

    public static void load(Map<Integer, Spell> spells, FileHandle file) {
        Json spellJson = new SpellJson();
        final HashMap<String, Spell> loadedSpells = spellJson.fromJson(HashMap.class, Spell.class, file);
        loadedSpells.forEach((k, v) -> spells.put(Integer.parseInt(k), v));
    }
}
