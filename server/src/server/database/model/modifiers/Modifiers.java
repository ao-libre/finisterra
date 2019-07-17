package server.database.model.modifiers;

import server.database.model.attributes.Attributes;
import shared.interfaces.CharClass;

public enum Modifiers {
    WEAPON,
    WEAPON_DAMAGE,
    PROJECTILE,
    PROJECTILE_DAMAGE,
    WRESTLING,
    WRESTLING_DAMAGE,
    EVASION,
    SHIELD,
    HEALTH;


    private static float[][] modifiers = new float[CharClass.values().length][Attributes.values().length];

    static {
        modifiers[CharClass.ASSASSIN.ordinal()] = new float[]{0.9f, 0.9f, 0.75f, 0.8f, 0.9f, 0.9f, 1.1f, 0.8f, 8.5f};
        modifiers[CharClass.ROGUE.ordinal()] = new float[]{0.85f, 0.77f, 0.8f, 0.7f, 0.95f, 1.05f, 0.7f, 2, 9.5f};
        modifiers[CharClass.BARDIC.ordinal()] = new float[]{0.7f, 0.7f, 0.7f, 0.7f, 0.4f, 0.4f, 1.075f, 0.8f, 8.5f};
        modifiers[CharClass.CLERIC.ordinal()] = new float[]{0.85f, 0.8f, 0.7f, 0.7f, 0.4f, 0.4f, 0.8f, 0.85f, 8.5f};
        modifiers[CharClass.DRUID.ordinal()] = new float[]{0.65f, 0.7f, 0.75f, 0.75f, 0.4f, 0.4f, 0.75f, 0, 8.5f};
        modifiers[CharClass.WARRIOR.ordinal()] = new float[]{1.025f, 1, 1, 1.1f, 1, 0.9f, 1, 1, 10};
        modifiers[CharClass.THIEF.ordinal()] = new float[]{0.9f, 0.8f, 1, 0.95f, 1, 1.075f, 1, 1.1f, 10f};
        modifiers[CharClass.MAGICIAN.ordinal()] = new float[]{0.5f, 0.7f, 0.5f, 0.5f, 0.3f, 0.4f, 0.4f, 0, 7.5f};
        modifiers[CharClass.PALADIN.ordinal()] = new float[]{0.95f, 0.925f, 0.75f, 0.8f, 0.95f, 0.9f, 0.9f, 1, 9.5f};
        modifiers[CharClass.PIRATE.ordinal()] = new float[]{1, 1.1f, 0.8f, 1.1f, 0.95f, 0.95f, 0.8f, 0.86f, 11};
        modifiers[CharClass.ARCHER.ordinal()] = new float[]{0.75f, 0.65f, 1.1f, 1.1f, 0.7f, 0.75f, 0.4f, 0.75f, 8.5f};
    }

    public float of(CharClass clazz) {
        return modifiers[clazz.ordinal()][this.ordinal()];
    }

}
