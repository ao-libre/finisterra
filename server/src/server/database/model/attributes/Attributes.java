package server.database.model.attributes;


import shared.interfaces.CharClass;

public enum Attributes {

    STRENGTH,
    INTELLIGENCE,
    AGILITY,
    EVASION;

    private static float[][] attributes = new float[CharClass.values().length][Attributes.values().length];

    static {
        attributes[CharClass.WARRIOR.ordinal()] = new float[]{25,0,13,13};
        attributes[CharClass.MAGICIAN.ordinal()] = new float[]{13,25,18,11};
        attributes[CharClass.PALADIN.ordinal()] = new float[]{19,15,15,14};
        attributes[CharClass.ROGUE.ordinal()] = new float[]{16,16,23,23};
        attributes[CharClass.CLERIC.ordinal()] = new float[]{14,21,16,17};
    }

    public float of(CharClass clazz) {
        return attributes[clazz.ordinal()][this.ordinal()];
    }

}
