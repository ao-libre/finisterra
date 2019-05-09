package shared.model.loaders;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;
import shared.model.npcs.NPC;
import shared.model.readers.Loader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class NPCLoader extends Loader<Map<Integer, NPC>> {

    private static final String NPC_STRING = "NPC";
    public static final Set<NPCLoader.NPCSetter<?>> setters;

    static {
        setters = new HashSet<>();
//        add("AfectaParalisis", ((npc, o) -> npc.setAffectParalysis(Boolean.parseBoolean(o))));
//        add("Alineacion", ((npc, o) -> npc.setAlignment(Boolean.parseBoolean(o))));
//        add("Attackable", ((npc, o) -> npc.setAttackable(Boolean.parseBoolean(o))));
//        add("PoderAtaque", ((npc, o) -> npc.setAttackPower(Integer.parseInt(o))));
//        add("BackUp", NPC::setBackup);
        add("Body", ((npc, o) -> npc.setBody(Integer.parseInt(o))));
//        add("Ciudad", NPC::setCity);
//        add("Comercia", NPC::setCommerce);
//        add("DEF", NPC::setDef);
//        add("DEFm", NPC::setDefM);
//        add("Desc", NPC::setDesc);
//        add("AtacaDoble", NPC::setDobleAttack);
//        add("Domable", NPC::setDomable);
//        add("PoderEvasion", NPC::setEvasionPower);
//        add("Faccion", NPC::setFaction);
//        add("GiveEXP", NPC::setGiveEXP);
//        add("GiveGLD", NPC::setGiveGLD);
//        add("Head", NPC::setHead);
        add("Heading", ((npc, o) -> npc.setHeading(Integer.parseInt(o))));
//        add("Hostile", NPC::setHostile);
//        add("InvReSpawn", NPC::setInvReSpawn);
//        add("TierraInValida", NPC::setInvalidEarth);
//        add("TipoItems", NPC::setItemTypes);
//        add("MaxHIT", NPC::setMaxHit);
//        add("MinHIT", NPC::setMinHit);
//        add("MaxHP", NPC::setMaxHP);
//        add("MinHP", NPC::setMinHP);
//        add("Movement", NPC::setMovement);
        add("Name", NPC::setName);
//        add("NpcType", NPC::setNpcType);
//        add("Veneno", NPC::setPoison);
//        add("ReSpawn", NPC::setRespawn);
//        add("AguaValida", NPC::setValidWater);
//        add("Snd1", NPC::addSound);
//        add("Snd2", NPC::addSound);
//        add("Snd3", NPC::addSound);
    }

    private static void add(String field, BiConsumer<NPC, String> setter) {
        setters.add(new NPCSetter(field, setter));
    }

    @Override
    public Map<Integer, NPC> load(DataInputStream file) throws IOException {
        Map<Integer, NPC> npcs = new HashMap<>();
        Ini iniFile = new Ini();
        Config c = new Config();
        c.setLowerCaseSection(true);
        iniFile.setConfig(c);
        iniFile.load(file);
        int numNPCs = Integer.parseInt(iniFile.get("init", "NumNPCs"));
        for (int i = 1; i <= numNPCs; i++) {
            Profile.Section section = iniFile.get("npc" + String.valueOf(i));

            if (section == null) {
                continue;
            }

            NPC npc = new NPC(i);
            setters.forEach(setter -> setter.accept(npc, section));
            if (section.containsKey("LanzaSpells")) {
                int spellsCount = section.get("LanzaSpells", Integer.class);
                for (int j = 1; j < spellsCount; j++) {
                    npc.addSpells(section.get("Sp" + j, Integer.class));
                }
            }
            if (section.containsKey("NroCriaturas")) {
                int npcCount = section.get("NroCriaturas", Integer.class);
                for (int j = 1; j < npcCount; j++) {
                    npc.addNPCtoSpawn(section.get("CI" + j, Integer.class), section.get("CN" + j));
                }
            }
            if (section.containsKey("NROEXP")) {
                int expressions = section.get("NROEXP", Integer.class);
                for (int j = 1; j < expressions; j++) {
                    npc.addExpression(section.get("Exp" + j));
                }
            }
            if (section.containsKey("NROITEMS")) {
                int objCount = section.get("NROITEMS", Integer.class);
                for (int j = 1; j < objCount; j++) {
                    String obj = section.get("Obj" + j);
                    if (obj != null) {
                        String[] args = obj.split("-");
                        npc.addObj(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                    }
                }
            }

            int drops = 20;
            for (int j = 1; j < drops; j++) {
                if (section.containsKey("Drop" + j)) {
                    String drop = section.get("Drop" + j);
                    String[] args = drop.split("-");
                    npc.addDrops(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                }
            }
            npcs.put(i, npc);
        }
        return npcs;
    }

    public static class NPCSetter<T> {
        private String field;
        private BiConsumer<NPC, T> setter;

        NPCSetter(String field, BiConsumer<NPC, T> setter) {
            this.field = field;
            this.setter = setter;
        }

        public void accept(NPC npc, Profile.Section section) {
            T u = (T) section.get(field);
            if (u == null) {
                return;
            }
            setter.accept(npc, u);
        }
    }
}
