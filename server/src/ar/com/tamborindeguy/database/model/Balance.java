package ar.com.tamborindeguy.database.model;

import ar.com.tamborindeguy.interfaces.CharClass;
import ar.com.tamborindeguy.interfaces.Race;

import java.util.HashMap;
import java.util.Map;

public class Balance {

    private final static Map<Race, AttributeModifier> attributeModifiers;
    private final static Map<CharClass, Float> evasionModifiers;
    private final static Map<CharClass, Modifier> weaponModifiers;
    private final static Map<CharClass, Modifier> projectileModifiers;
    private final static Map<CharClass, Modifier> wrestlingModifiers;
    private final static Map<CharClass, Float> healthModifier;
    private final static Map<CharClass, Float> shieldModifier;

    static {
        attributeModifiers = attributes();
        evasionModifiers = evasion();
        weaponModifiers = weapon();
        projectileModifiers = projectile();
        wrestlingModifiers = wrestling();
        healthModifier = health();
        shieldModifier = shield();
    }

    private static Map<CharClass,Float> shield() {
        Map<CharClass, Float> result = new HashMap<>();
        result.put(CharClass.WARRIOR, 1f);
        result.put(CharClass.ARCHER, 0.8f);
        result.put(CharClass.PALADIN, 1f);
        result.put(CharClass.ROGUE, 2f);
        result.put(CharClass.ASSESIN, 0.8f);
        result.put(CharClass.PIRATE, 0.6f);
        result.put(CharClass.THIEF, 0.7f);
        result.put(CharClass.CLERIC, 0.85f);
        result.put(CharClass.BARD, 0.8f);
        result.put(CharClass.MAGICIAN, 0.6f);
        result.put(CharClass.DRUID, 0.75f);
        return result;
    }

    private static Map<CharClass, Float> health() {
        Map<CharClass, Float> result = new HashMap<>();
        result.put(CharClass.WARRIOR, 10f);
        result.put(CharClass.ARCHER, 9.5f);
        result.put(CharClass.PALADIN, 9.5f);
        result.put(CharClass.ROGUE, 9.5f);
        result.put(CharClass.ASSESIN, 8.5f);
        result.put(CharClass.PIRATE, 9.5f);
        result.put(CharClass.THIEF, 10f);
        result.put(CharClass.CLERIC, 8.5f);
        result.put(CharClass.BARD, 8.5f);
        result.put(CharClass.MAGICIAN, 7.5f);
        result.put(CharClass.DRUID, 8.5f);
        return result;
    }

    private static Map<CharClass, Modifier> wrestling() {
        Map<CharClass, Modifier> result = new HashMap<>();
        result.put(CharClass.WARRIOR, new Modifier(0.6f, 0.4f));
        result.put(CharClass.ARCHER, new Modifier(0.5f, 0.4f));
        result.put(CharClass.PALADIN, new Modifier(0.4f, 0.4f));
        result.put(CharClass.ROGUE, new Modifier(0.95f, 1.05f));
        result.put(CharClass.ASSESIN, new Modifier(0.4f, 0.4f));
        result.put(CharClass.PIRATE, new Modifier(0.5f, 0.4f));
        result.put(CharClass.THIEF, new Modifier(0.8f, 1.05f));
        result.put(CharClass.CLERIC, new Modifier(0.4f, 0.4f));
        result.put(CharClass.BARD, new Modifier(0.4f, 0.4f));
        result.put(CharClass.MAGICIAN, new Modifier(0.3f, 0.4f));
        result.put(CharClass.DRUID, new Modifier(0.4f, 0.4f));
        return result;
    }

    private static Map<CharClass, Modifier> projectile() {
        Map<CharClass, Modifier> result = new HashMap<>();
        result.put(CharClass.WARRIOR, new Modifier(0.8f, 0.9f));
        result.put(CharClass.ARCHER, new Modifier(1f, 1.1f));
        result.put(CharClass.PALADIN, new Modifier(0.75f, 0.8f));
        result.put(CharClass.ROGUE, new Modifier(0.8f, 0.7f));
        result.put(CharClass.ASSESIN, new Modifier(0.75f, 0.8f));
        result.put(CharClass.PIRATE, new Modifier(0.9f, 0.8f));
        result.put(CharClass.THIEF, new Modifier(0.85f, 0.85f));
        result.put(CharClass.CLERIC, new Modifier(0.7f, 0.7f));
        result.put(CharClass.BARD, new Modifier(0.7f, 0.7f));
        result.put(CharClass.MAGICIAN, new Modifier(0.5f, 0.5f));
        result.put(CharClass.DRUID, new Modifier(0.75f, 0.75f));
        return result;
    }

    private static Map<CharClass, Modifier> weapon() {
        Map<CharClass, Modifier> result = new HashMap<>();
        result.put(CharClass.WARRIOR, new Modifier(1f, 1.1f));
        result.put(CharClass.ARCHER, new Modifier(0.8f, 0.9f));
        result.put(CharClass.PALADIN, new Modifier(0.95f, 0.925f));
        result.put(CharClass.ROGUE, new Modifier(0.85f, 0.85f));
        result.put(CharClass.ASSESIN, new Modifier(0.9f, 0.9f));
        result.put(CharClass.PIRATE, new Modifier(0.9f, 0.95f));
        result.put(CharClass.THIEF, new Modifier(0.8f, 0.75f));
        result.put(CharClass.CLERIC, new Modifier(0.85f, 0.8f));
        result.put(CharClass.BARD, new Modifier(0.7f, 0.75f));
        result.put(CharClass.MAGICIAN, new Modifier(0.5f, 0.5f));
        result.put(CharClass.DRUID, new Modifier(0.65f, 0.7f));
        return result;
    }

    private static Map<CharClass, Float> evasion() {
        Map<CharClass, Float> result = new HashMap<>();
        result.put(CharClass.WARRIOR, 1f);
        result.put(CharClass.ARCHER, 0.9f);
        result.put(CharClass.PALADIN, 0.9f);
        result.put(CharClass.ROGUE, 0.7f);
        result.put(CharClass.ASSESIN, 1.1f);
        result.put(CharClass.PIRATE, 1.25f);
        result.put(CharClass.THIEF, 1.1f);
        result.put(CharClass.CLERIC, 0.8f);
        result.put(CharClass.BARD, 1.075f);
        result.put(CharClass.MAGICIAN, 0.4f);
        result.put(CharClass.DRUID, 0.75f);
        return result;
    }

    private static Map<Race, AttributeModifier> attributes() {
        Map<Race, AttributeModifier> modifiers = new HashMap<>();
        modifiers.put(Race.HUMAN, new AttributeModifier(1, 1, 0, 0, 2));
        modifiers.put(Race.ELF, new AttributeModifier(-1, 3, 2, 2, 1));
        modifiers.put(Race.DROW, new AttributeModifier(2, 3, 2, -3, 0));
        modifiers.put(Race.GNOME, new AttributeModifier(-2, 3, 4, 1, 0));
        modifiers.put(Race.GNOME, new AttributeModifier(3, 0, -2, -2, 3));
        return modifiers;
    }

    private static class Modifier {
        float probability;
        float damage;

        Modifier(float probability, float damage) {
            this.probability = probability;
            this.damage = damage;
        }
    }

    private static class AttributeModifier {
        int strength;
        int agility;
        int intelligence;
        int charisma;
        int constitution;

        AttributeModifier(int strength, int agility, int intelligence, int charisma, int constitution) {
            this.strength = strength;
            this.agility = agility;
            this.intelligence = intelligence;
            this.charisma = charisma;
            this.constitution = constitution;
        }
    }

}
