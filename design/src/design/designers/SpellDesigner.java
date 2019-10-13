package design.designers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import shared.model.Spell;
import shared.util.AOJson;
import shared.util.SharedResources;
import shared.util.SpellJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static design.designers.SpellDesigner.SpellParameter;

public class SpellDesigner implements IDesigner<Spell, SpellParameter> {

    private HashMap<Integer, Spell> spells = new HashMap<>();
    ;

    public SpellDesigner() {
        load(new SpellParameter());
    }

    @Override
    public void load(SpellParameter params) {
        SpellJson.load(spells, Gdx.files.internal(SharedResources.SPELLS_JSON_FILE));
        spells.forEach((k, v) -> v.setId(k));
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {
        ArrayList<Spell> spells = new ArrayList<>(this.spells.values());
        FileHandle output = Gdx.files.local("output/" + SharedResources.SPELLS_JSON_FILE);
        new AOJson().toJson(spells, ArrayList.class, Spell.class, output);
    }

    @Override
    public Map<Integer, Spell> get() {
        return spells;
    }

    @Override
    public Optional<Spell> get(int id) {
        return Optional.ofNullable(spells.get(id));
    }

    @Override
    public Optional<Spell> create() {
        return Optional.of(new Spell(getFreeId()));
    }

    private int getFreeId() {
        return spells.keySet().stream().max(Integer::compareTo).get() + 1;
    }

    @Override
    public void modify(Spell element, Stage stage) {

    }

    @Override
    public void delete(Spell element) {
        spells.remove(element.getId());
    }

    @Override
    public void add(Spell spell) {
        spells.put(spell.getId(), spell);
    }

    @Override
    public boolean contains(int id) {
        return spells.containsKey(id);
    }

    @Override
    public void markUsedImages() {
    }

    static class SpellParameter implements Parameters<Spell> {
    }
}
