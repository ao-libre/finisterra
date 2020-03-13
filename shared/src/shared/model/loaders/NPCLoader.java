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
    private final static Set<NPCLoader.NPCSetter<?>> setters = new HashSet<>();

    static {
        add("AfectaParalisis", ((npc, o) -> npc.setAffectParalysis(Boolean.parseBoolean(o))));
        add("Alineacion", ((npc, o) -> npc.setAlignment(Boolean.parseBoolean(o))));
        add("Attackable", ((npc, o) -> npc.setAttackable(Integer.parseInt(o) == 1)));
        add("PoderAtaque", ((npc, o) -> npc.setAttackPower(Integer.parseInt(o))));
        add("BackUp", ((npc, o) -> npc.setBackup(Boolean.parseBoolean(o))));
        add("Body", ((npc, o) -> npc.setBody(Integer.parseInt(o))));
        add("Ciudad", ((npc, o) -> npc.setCity(Integer.parseInt(o))));
        add("Comercia", ((npc, o) -> npc.setCommerce(Boolean.parseBoolean(o))));
        add("DEF", ((npc, o) -> npc.setDef(Integer.parseInt(o))));
        add("DEFm", ((npc, o) -> npc.setDefM(Integer.parseInt(o))));
        add("Desc", NPC::setDesc);
        add("AtacaDoble", ((npc, o) -> npc.setDobleAttack(Boolean.parseBoolean(o))));
        add("Domable", ((npc, o) -> npc.setDomable(Boolean.parseBoolean(o))));
        add("PoderEvasion", ((npc, o) -> npc.setEvasionPower(Integer.parseInt(o))));
        add("Faccion", ((npc, o) -> npc.setFaction(Boolean.parseBoolean(o))));
        add("GiveEXP", ((npc, o) -> npc.setGiveEXP(Integer.parseInt(o))));
        add("GiveGLD", ((npc, o) -> npc.setGiveGLD(Integer.parseInt(o))));
        add("Head", ((npc, o) -> npc.setHead(Integer.parseInt(o))));
        add("Heading", ((npc, o) -> npc.setHeading(Integer.parseInt(o))));
        add("Hostile", ((npc, o) -> npc.setHostile(Integer.parseInt(o) == 1)));
        add("InvReSpawn", ((npc, o) -> npc.setInvReSpawn(Boolean.parseBoolean(o))));
        add("TierraInValida", ((npc, o) -> npc.setInvalidEarth(Boolean.parseBoolean(o))));
        add("TipoItems", ((npc, o) -> npc.setItemTypes(Integer.parseInt(o))));
        add("MaxHIT", ((npc, o) -> npc.setMaxHit(Integer.parseInt(o))));
        add("MinHIT", ((npc, o) -> npc.setMinHit(Integer.parseInt(o))));
        add("MaxHP", ((npc, o) -> npc.setMaxHP(Integer.parseInt(o))));
        add("MinHP", ((npc, o) -> npc.setMinHP(Integer.parseInt(o))));
        add("Movement", ((npc, o) -> npc.setMovement(Integer.parseInt(o))));
        add("Name", NPC::setName);
        add("NpcType", ((npc, o) -> npc.setNpcType(Integer.parseInt(o))));
        add("Veneno", ((npc, o) -> npc.setPoison(Boolean.parseBoolean(o))));
        add("ReSpawn", ((npc, o) -> npc.setRespawn(Boolean.parseBoolean(o))));
        add("AguaValida", ((npc, o) -> npc.setValidWater(Boolean.parseBoolean(o))));
        add("Snd1", ((npc, o) -> npc.setAttackSnd(Integer.parseInt(o))));
        add("Snd2", ((npc, o) -> npc.setGHitSnd(Integer.parseInt(o))));
        add("Snd3", ((npc, o) -> npc.setDieSound(Integer.parseInt(o))));
    }

    private static void add(String field, BiConsumer<NPC, String> setter) {
        setters.add(new NPCSetter<>(field, setter));
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
