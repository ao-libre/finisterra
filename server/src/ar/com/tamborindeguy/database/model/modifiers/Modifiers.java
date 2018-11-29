package ar.com.tamborindeguy.database.model.modifiers;

import ar.com.tamborindeguy.database.model.attributes.Attributes;
import ar.com.tamborindeguy.interfaces.CharClass;

public enum Modifiers {
    HEALTH,
    MANA,
    ATACK_VEL,
    MOV_VEL,
    EVASION;

    private static float[][] modifiers = new float[CharClass.values().length][Attributes.values().length];

    static {
        modifiers[CharClass.WARRIOR.ordinal()] = new float[]{1,0,0.7f,1,1};
        modifiers[CharClass.MAGICIAN.ordinal()] = new float[]{0.8f,1,1,1,1};
        modifiers[CharClass.PALADIN.ordinal()] = new float[]{1,0.5f,0.8f,1,1};
        modifiers[CharClass.ROGUE.ordinal()] = new float[]{1,0.6f,1,1,1};
        modifiers[CharClass.CLERIC.ordinal()] = new float[]{1,0.8f,0.7f,1,1};
    }

    public float of(CharClass clazz) {
        return modifiers[clazz.ordinal()][this.ordinal()];
    }

}
