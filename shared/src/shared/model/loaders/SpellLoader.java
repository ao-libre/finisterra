package shared.model.loaders;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;
import shared.model.Spell;
import shared.model.readers.Loader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class SpellLoader extends Loader<Map<Integer, Spell>> {

    public static final Set<SpellSetter<?>> setters;
    private static final String HECHIZO = "hechizo";

    static {
        setters = new HashSet<>();
        setters.add(new SpellSetter<>("Nombre", Spell::setName));
        setters.add(new SpellSetter<>("Tipo", Spell::setType));
        setters.add(new SpellSetter<>("PalabrasMagicas", Spell::setMagicWords));
        setters.add(new SpellSetter<>("HechizeroMsg", Spell::setOriginMsg));
        setters.add(new SpellSetter<>("PropioMsg", Spell::setOwnerMsg));
        setters.add(new SpellSetter<>("TargetMsg", Spell::setTargetMsg));
        setters.add(new SpellSetter<>("WAV", Spell::setWav));
        setters.add(new SpellSetter<>("FXgrh", Spell::setFxGrh));
        setters.add(new SpellSetter<>("Loops", Spell::setLoops));
        setters.add(new SpellSetter<>("MinSkill", Spell::setMinSkill));
        setters.add(new SpellSetter<>("ManaRequerido", Spell::setRequiredMana));
        setters.add(new SpellSetter<>("StaRequerido", Spell::setRequiredStamina));
        setters.add(new SpellSetter<>("Target", Spell::setTarget));
        setters.add(new SpellSetter<>("SubeHP", Spell::setSumHP));
        setters.add(new SpellSetter<>("MinHP", Spell::setMinHP));
        setters.add(new SpellSetter<>("MaxHP", Spell::setMaxHP));
        setters.add(new SpellSetter<>("SubeMana", Spell::setSumMana));
        setters.add(new SpellSetter<>("MinMana", Spell::setMinMana));
        setters.add(new SpellSetter<>("MaxMana", Spell::setMaxMana));
        setters.add(new SpellSetter<>("SubeSta", Spell::setSumStamina));
        setters.add(new SpellSetter<>("MinSta", Spell::setMinSta));
        setters.add(new SpellSetter<>("MaxSta", Spell::setMaxSta));
        setters.add(new SpellSetter<>("SubeHam", Spell::setSumHungry));
        setters.add(new SpellSetter<>("MinHam", Spell::setMinHungry));
        setters.add(new SpellSetter<>("MaxHam", Spell::setMaxHungry));
        setters.add(new SpellSetter<>("SubeSed", Spell::setSumThirsty));
        setters.add(new SpellSetter<>("MinSed", Spell::setMinThirsty));
        setters.add(new SpellSetter<>("MaxSed", Spell::setMaxThirsty));
        setters.add(new SpellSetter<>("SubeAG", Spell::setSumAgility));
        setters.add(new SpellSetter<>("MinAG", Spell::setMinAgility));
        setters.add(new SpellSetter<>("MaxAG", Spell::setMaxAgility));
        setters.add(new SpellSetter<>("SubeFU", Spell::setSumStrength));
        setters.add(new SpellSetter<>("MinFU", Spell::setMinStrength));
        setters.add(new SpellSetter<>("MaxFU", Spell::setMaxStrength));
        setters.add(new SpellSetter<>("SubeCA", Spell::setSumCA));
        setters.add(new SpellSetter<>("MinCA", Spell::setMinCA));
        setters.add(new SpellSetter<>("MaxCA", Spell::setMaxCA));
        setters.add(new SpellSetter<>("Invisibilidad", Spell::setInvisibility));
        setters.add(new SpellSetter<>("Paraliza", Spell::setParalyze));
        setters.add(new SpellSetter<>("Inmoviliza", Spell::setImmobilize));
        setters.add(new SpellSetter<>("RemoverParalisis", Spell::setRemoveParalysis));
        setters.add(new SpellSetter<>("RemoverEstupidez", Spell::setRemoveStupid));
        setters.add(new SpellSetter<>("RemueveInvisibilidadParcial", Spell::setRemovePartialInvisibility));
        setters.add(new SpellSetter<>("CuraVeneno", Spell::setHealPoison));
        setters.add(new SpellSetter<>("Envenena", Spell::setPoison));
        setters.add(new SpellSetter<>("Revivir", Spell::setRevive));
        setters.add(new SpellSetter<>("Ceguera", Spell::setBlindness));
        setters.add(new SpellSetter<>("Estupidez", Spell::setStupid));
        setters.add(new SpellSetter<>("Invoca", Spell::setInvokes));
        setters.add(new SpellSetter<>("NumNpc", Spell::setNumNpc));
        setters.add(new SpellSetter<>("Cant", Spell::setCount));
        setters.add(new SpellSetter<>("Mimetiza", Spell::setMimetize));
        setters.add(new SpellSetter<>("Materializa", Spell::setMaterialize));
        setters.add(new SpellSetter<>("itemindex", Spell::setItemIndex));
        setters.add(new SpellSetter<>("StaffAffected", Spell::setStaffAffected));
        setters.add(new SpellSetter<>("NeedStaff", Spell::setNeedStaff));
        setters.add(new SpellSetter<>("Resis", Spell::setResis));
    }

    @Override
    public Map<Integer, Spell> load(DataInputStream file) throws IOException {
        Map<Integer, Spell> spells = new HashMap<>();
        Ini iniFile = new Ini();
        Config c = new Config();
        c.setLowerCaseSection(true);
        iniFile.setConfig(c);

        iniFile.load(file);
        int numSpells = Integer.parseInt(iniFile.get("init", "NumeroHechizos"));

        for (int i = 0; i < numSpells; i++) {
            Profile.Section section = iniFile.get(HECHIZO + String.valueOf(i));
            if (section == null) {
                continue;
            }
            Spell spell = new Spell(i);
            setters.forEach(setter -> setter.accept(spell, section));
            spells.put(i, spell);
        }

        return spells;
    }

    public static class SpellSetter<T> {
        private String field;
        private BiConsumer<Spell, T> setter;

        public SpellSetter(String field, BiConsumer<Spell, T> setter) {
            this.field = field;
            this.setter = setter;
        }

        public void accept(Spell spell, Profile.Section section) {
            T u = (T) section.get(field);
            if (u == null) {
                return;
            }
            setter.accept(spell, u);
        }
    }
}
