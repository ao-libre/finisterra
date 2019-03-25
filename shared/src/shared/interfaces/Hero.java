package shared.interfaces;

public enum Hero {

    WARRIOR(CharClass.WARRIOR.ordinal(), Race.DWARF.ordinal()),
    MAGICIAN(CharClass.MAGICIAN.ordinal(), Race.GNOME.ordinal()),
    ROGUE(CharClass.ROGUE.ordinal(), Race.DROW.ordinal()),
    PALADIN(CharClass.PALADIN.ordinal(), Race.HUMAN.ordinal()),
    PRIEST(CharClass.CLERIC.ordinal(), Race.ELF.ordinal());

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
