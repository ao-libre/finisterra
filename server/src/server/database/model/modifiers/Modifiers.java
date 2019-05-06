package server.database.model.modifiers;

import server.database.model.attributes.Attributes;
import shared.interfaces.CharClass;

public enum Modifiers {

    EVASION,
    WEAPON,
    PROJECTILE,
    WRESTLING,
    WEAPON_DAMAGE,
    PROJECTILE_DAMAGE,
    WRESTLING_DAMAGE,
    SHIELD,
    HEALTH;


    private static float[][] modifiers = new float[CharClass.values().length][Attributes.values().length];

    static {
        modifiers[CharClass.WARRIOR.ordinal()] = new float[]{1, 1, 0.8f, 0.6f, 1.1f, 0.9f, 0.4f, 1f, 10};
        modifiers[CharClass.ARCHER.ordinal()] = new float[]{0.9f, 0.8f, 1f, 0.5f, 0.9f, 1.1f, 0.4f, 0.8f, 9.5f};
        modifiers[CharClass.PALADIN.ordinal()] = new float[]{0.9f, 0.95f, 0.75f, 0.4f, 0.925f, 0.8f, 0.4f, 1f, 9.5f};
        modifiers[CharClass.ROGUE.ordinal()] = new float[]{0.7f, 0.85f, 0.8f, 0.95f, 0.85f, 0.7f, 1.05f, 2f, 9.5f};
        modifiers[CharClass.ASSASSIN.ordinal()] = new float[]{1.1f, 0.9f, 0.75f, 0.4f, 0.9f, 0.8f, 0.4f, 0.8f, 8.5f};
        modifiers[CharClass.PIRATE.ordinal()] = new float[]{1.25f, 0.9f, 0.5f, 0.6f, 0.95f, 0.8f, 0.4f, 0.6f, 9.5f};
        modifiers[CharClass.THIEF.ordinal()] = new float[]{1.1f, 0.8f, 0.85f, 0.8f, 0.75f, 0.85f, 1.05f, 0.7f, 10f};
        modifiers[CharClass.CLERIC.ordinal()] = new float[]{0.8f, 0.85f, 0.7f, 0.4f, 0.8f, 0.7f, 0.4f, 0.85f, 8.5f};
        modifiers[CharClass.BARDIC.ordinal()] = new float[]{1.075f, 0.7f, 0.74f, 0.4f, 0.75f, 0.75f, 0.4f, 0.8f, 8.5f};
        modifiers[CharClass.MAGICIAN.ordinal()] = new float[]{0.4f, 0.5f, 0.5f, 0.3f, 0.5f, 0.5f, 0.4f, 0.6f, 7.5f};
        modifiers[CharClass.DRUID.ordinal()] = new float[]{0.75f, 0.65f, 0.75f, 0.4f, 0.7f, 0.75f, 0.4f, 0.75f, 8.5f};
    }

    public float of(CharClass clazz) {
        return modifiers[clazz.ordinal()][this.ordinal()];
    }

}
