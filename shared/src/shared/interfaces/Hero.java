package shared.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public enum Hero {

    GUERRERO(CharClass.WARRIOR.ordinal(), Race.DWARF.ordinal()),
    MAGO(CharClass.MAGICIAN.ordinal(), Race.GNOME.ordinal()),
    ASESINO(CharClass.ASSASSIN.ordinal(), Race.DROW.ordinal()),
    PALADIN(CharClass.PALADIN.ordinal(), Race.HUMAN.ordinal()),
    BARDO(CharClass.BARDIC.ordinal(), Race.ELF.ordinal()),
    ARQUERO(CharClass.ARCHER.ordinal(), Race.DWARF.ordinal()),
    CLERIGO(CharClass.CLERIC.ordinal(), Race.HUMAN.ordinal());

    public static final List<Hero> VALUES = Arrays.stream(values()).collect(Collectors.toList());
    private static final Random RANDOM = new Random();
    private final int classId;
    private final int raceId;

    Hero(int classId, int raceId) {
        this.classId = classId;
        this.raceId = raceId;
    }

    public static Hero getRandom() {
        final List<Hero> heroes = getHeroes();
        return heroes.get(RANDOM.nextInt(heroes.size()));
    }

    public static List<Hero> getHeroes() {
        return new ArrayList<>(VALUES);
    }

    public int getClassId() {
        return classId;
    }

    public int getRaceId() {
        return raceId;
    }
}
