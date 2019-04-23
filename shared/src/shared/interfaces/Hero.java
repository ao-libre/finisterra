package shared.interfaces;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Hero {

    GUERRERO(CharClass.WARRIOR.ordinal(), Race.DWARF.ordinal()),
    MAGO(CharClass.MAGICIAN.ordinal(), Race.GNOME.ordinal()),
    ASESINO(CharClass.ASSASSIN.ordinal(), Race.DROW.ordinal()),
    PALADIN(CharClass.PALADIN.ordinal(), Race.HUMAN.ordinal()),
    BARDO(CharClass.BARDIC.ordinal(), Race.ELF.ordinal()),
    ARQUERO(CharClass.ARCHER.ordinal(), Race.DWARF.ordinal()),
    CLERIGO(CharClass.CLERIC.ordinal(), Race.HUMAN.ordinal());

    private static final List<Hero> VALUES;
    private static final int SIZE;
    private static final Random RANDOM = new Random();

    static {
        final List<Hero> list = Arrays.asList(values());
        list.remove(ARQUERO);
        VALUES = Collections.unmodifiableList(list);
        SIZE =  VALUES.size();
    }

    public static Hero getRandom() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    public static List<Hero> getHeroes() {
        return VALUES;
    }

    private final int classId;

    private final int raceId;

    Hero(int classId, int raceId) {
        this.classId = classId;
        this.raceId = raceId;
    }

    public int getClassId() {
        return classId;
    }

    public int getRaceId() {
        return raceId;
    }
}
