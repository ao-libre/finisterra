package server.configs;

import server.database.model.modifiers.Modifiers;
import shared.interfaces.CharClass;
import shared.util.Pair;

import java.util.ArrayList;

public class CharConfiguration extends BaseConfiguration {

    // Default values
    public static final String PATH = "Chars.json";
    private static final ArrayList<Pair<CharClass, float[]>> DEF_MODIFIERS = new ArrayList<>();

    static {
        DEF_MODIFIERS.add(new Pair<>(CharClass.ASSASSIN, new float[]{0.9f, 0.9f, 0.75f, 0.8f, 0.9f, 0.9f, 1.1f, 0.8f, 8.5f}));
        DEF_MODIFIERS.add(new Pair<>(CharClass.ROGUE, new float[]{0.85f, 0.77f, 0.8f, 0.7f, 0.95f, 1.05f, 0.7f, 2, 9.5f}));
        DEF_MODIFIERS.add(new Pair<>(CharClass.BARDIC, new float[]{0.7f, 0.7f, 0.7f, 0.7f, 0.4f, 0.4f, 1.075f, 0.8f, 8.5f}));
        DEF_MODIFIERS.add(new Pair<>(CharClass.CLERIC, new float[]{0.85f, 0.8f, 0.7f, 0.7f, 0.4f, 0.4f, 0.8f, 0.85f, 8.5f}));
        DEF_MODIFIERS.add(new Pair<>(CharClass.DRUID, new float[]{0.65f, 0.7f, 0.75f, 0.75f, 0.4f, 0.4f, 0.75f, 0, 8.5f}));
        DEF_MODIFIERS.add(new Pair<>(CharClass.WARRIOR, new float[]{1.025f, 1, 1, 1.1f, 1, 0.9f, 1, 1, 10}));
        DEF_MODIFIERS.add(new Pair<>(CharClass.THIEF, new float[]{0.9f, 0.8f, 1, 0.95f, 1, 1.075f, 1, 1.1f, 10f}));
        DEF_MODIFIERS.add(new Pair<>(CharClass.MAGICIAN, new float[]{0.5f, 0.7f, 0.5f, 0.5f, 0.3f, 0.4f, 0.4f, 0, 7.5f}));
        DEF_MODIFIERS.add(new Pair<>(CharClass.PALADIN, new float[]{0.95f, 0.925f, 0.75f, 0.8f, 0.95f, 0.9f, 0.9f, 1, 9.5f}));
        DEF_MODIFIERS.add(new Pair<>(CharClass.PIRATE, new float[]{1, 1.1f, 0.8f, 1.1f, 0.95f, 0.95f, 0.8f, 0.86f, 11}));
        DEF_MODIFIERS.add(new Pair<>(CharClass.ARCHER, new float[]{0.75f, 0.65f, 1.1f, 1.1f, 0.7f, 0.75f, 0.4f, 0.75f, 8.5f}));
    }

    private ArrayList<CharType> charClasses;

    public CharConfiguration() {
        super(PATH);
    }

    public CharType getCharClass(CharClass charClass) {
        return charClasses.stream()
                .filter(config -> config.name.equalsIgnoreCase(charClass.name()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void loadDefaultValues() {
        charClasses = new ArrayList<>();

        DEF_MODIFIERS.forEach(defValue -> {
            CharType charType = new CharType();
            charType.setName(defValue.getKey().name());

            CharType.CharModifier modifier = new CharType.CharModifier();
            modifier.setValues(
                    defValue.getValue()[0], // WEAPON
                    defValue.getValue()[1], // WEAPON_DAMAGE
                    defValue.getValue()[2], // PROJECTILE
                    defValue.getValue()[3], // PROJECTILE_DAMAGE
                    defValue.getValue()[4], // WRESTLING
                    defValue.getValue()[5], // WRESTLING_DAMAGE
                    defValue.getValue()[6], // EVASION
                    defValue.getValue()[7], // SHIELD
                    defValue.getValue()[8] // HEALTH
            );
            charType.modifier = modifier;

            charClasses.add(charType);
        });
    }

    public static class CharType {

        private String name;
        private CharModifier modifier;

        private void setName(String name) {
            this.name = name;
        }

        public CharModifier getModifier() {
            return modifier;
        }

        public static class CharModifier {

            private static final float DEFAULT_VALUE = 1f;

            private float weapon;
            private float weaponDamage;
            private float projectile;
            private float projectileDamage;
            private float wrestling;
            private float wrestlingDamage;
            private float evasion;
            private float shield;
            private float health;

            private void setValues(float weapon, float weaponDamage, float projectile, float projectileDamage,
                                   float wrestling, float wrestlingDamage, float evasion, float shield, float health) {
                this.weapon = weapon;
                this.weaponDamage = weaponDamage;
                this.projectile = projectile;
                this.projectileDamage = projectileDamage;
                this.wrestling = wrestling;
                this.wrestlingDamage = wrestlingDamage;
                this.evasion = evasion;
                this.shield = shield;
                this.health = health;
            }

            public float getValue(Modifiers modifier) {
                switch (modifier) {
                    case WEAPON:
                        return weapon;
                    case WEAPON_DAMAGE:
                        return weaponDamage;
                    case PROJECTILE:
                        return projectile;
                    case PROJECTILE_DAMAGE:
                        return projectileDamage;
                    case WRESTLING:
                        return wrestling;
                    case WRESTLING_DAMAGE:
                        return wrestlingDamage;
                    case EVASION:
                        return evasion;
                    case SHIELD:
                        return shield;
                    case HEALTH:
                        return health;
                }

                return DEFAULT_VALUE;
            }
        }
    }
}
