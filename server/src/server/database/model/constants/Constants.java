package server.database.model.constants;

import shared.interfaces.CharClass;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final int BASE_TIME = 1000;
    public static final float TIME_MULTIPLIER = 2f;

    private static Map<CharClass, Float> HP_CONSTANTS;
    private static Map<CharClass, Float> MANA_CONSTANTS;
    private static Map<CharClass, Float> ATTACK_VELOCITY_CONSTANTS;
    private static Map<CharClass, Float> MOV_VELOCITY_CONSTANTS;
    private static Map<CharClass, Float> EVASION_CONSTANTS;

    static {
        initHP();
        initMana();
        initAttack();
        initMovement();
        initEvasion();
    }

    private static void initMana() {
        MANA_CONSTANTS = new HashMap<>();
        MANA_CONSTANTS.put(CharClass.WARRIOR, 0f);
        MANA_CONSTANTS.put(CharClass.MAGICIAN, 25f);
        MANA_CONSTANTS.put(CharClass.PALADIN, 15f);
        MANA_CONSTANTS.put(CharClass.ROGUE, 16f);
        MANA_CONSTANTS.put(CharClass.CLERIC, 21f);
    }

    private static void initHP() {
        HP_CONSTANTS = new HashMap<>();
        HP_CONSTANTS.put(CharClass.WARRIOR, 50f);
        HP_CONSTANTS.put(CharClass.MAGICIAN, 50f);
        HP_CONSTANTS.put(CharClass.PALADIN, 50f);
        HP_CONSTANTS.put(CharClass.ROGUE, 50f);
        HP_CONSTANTS.put(CharClass.CLERIC, 50f);
    }

    private static void initAttack() {
        ATTACK_VELOCITY_CONSTANTS = new HashMap<>();
        ATTACK_VELOCITY_CONSTANTS.put(CharClass.WARRIOR, 13f);
        ATTACK_VELOCITY_CONSTANTS.put(CharClass.MAGICIAN, 18f);
        ATTACK_VELOCITY_CONSTANTS.put(CharClass.PALADIN, 15f);
        ATTACK_VELOCITY_CONSTANTS.put(CharClass.ROGUE, 23f);
        ATTACK_VELOCITY_CONSTANTS.put(CharClass.CLERIC, 16f);
    }

    private static void initMovement() {
        MOV_VELOCITY_CONSTANTS = new HashMap<>();
        MOV_VELOCITY_CONSTANTS.put(CharClass.WARRIOR, 13f);
        MOV_VELOCITY_CONSTANTS.put(CharClass.MAGICIAN, 11f);
        MOV_VELOCITY_CONSTANTS.put(CharClass.PALADIN, 14f);
        MOV_VELOCITY_CONSTANTS.put(CharClass.ROGUE, 23f);
        MOV_VELOCITY_CONSTANTS.put(CharClass.CLERIC, 17f);
    }

    private static void initEvasion() {
        EVASION_CONSTANTS = new HashMap<>();
        EVASION_CONSTANTS.put(CharClass.WARRIOR, 13f);
        EVASION_CONSTANTS.put(CharClass.MAGICIAN, 11f);
        EVASION_CONSTANTS.put(CharClass.PALADIN, 14f);
        EVASION_CONSTANTS.put(CharClass.ROGUE, 23f);
        EVASION_CONSTANTS.put(CharClass.CLERIC, 17f);
    }

    public static Float getAttackVelocity(CharClass charClass) {
        return ATTACK_VELOCITY_CONSTANTS.get(charClass);
    }

    public static Float getHp(CharClass charClass) {
        return HP_CONSTANTS.get(charClass);
    }

    public static Float getMana(CharClass charClass) {
        return MANA_CONSTANTS.get(charClass);
    }

    public static Float getMovement(CharClass charClass) {
        return MOV_VELOCITY_CONSTANTS.get(charClass);
    }

    public static Float getEvasion(CharClass charClass) {
        return EVASION_CONSTANTS.get(charClass);
    }
}
