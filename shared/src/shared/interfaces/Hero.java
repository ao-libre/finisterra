package shared.interfaces;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Hero {

    WARRIOR(CharClass.WARRIOR.ordinal(), Race.DWARF.ordinal()),
    MAGICIAN(CharClass.MAGICIAN.ordinal(), Race.GNOME.ordinal()),
    ROGUE(CharClass.ROGUE.ordinal(), Race.DROW.ordinal()),
    PALADIN(CharClass.PALADIN.ordinal(), Race.HUMAN.ordinal()),
    BARDIC(CharClass.BARDIC.ordinal(), Race.ELF.ordinal()),
    PRIEST(CharClass.CLERIC.ordinal(), Race.HUMAN.ordinal());

    private static final List<Hero> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Hero getRandom() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    public static List<Hero> getHeros() {
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
