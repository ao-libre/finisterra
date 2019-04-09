package server.database.model.attributes;


import shared.interfaces.Race;

public enum Attributes {

    STRENGTH,
    AGILITY,
    INTELLIGENCE,
    CHARISMA,
    CONSTITUTION;

    private static int[][] attributes = new int[Race.values().length][Attributes.values().length];

    static {
        attributes[Race.HUMAN.ordinal()] = new int[]{1, 1, 0, 0, 2};
        attributes[Race.ELF.ordinal()] = new int[]{-1, 3, 2, 2, 1};
        attributes[Race.DROW.ordinal()] = new int[]{2, 3, 2, -3, 0};
        attributes[Race.DWARF.ordinal()] = new int[]{3, 0, -2, -2, 3};
        attributes[Race.GNOME.ordinal()] = new int[]{-2, 3, 4, 0, -2};
    }

    public int of(Race race) {
        return attributes[race.ordinal()][this.ordinal()];
    }

}
