package shared.util;

import java.io.FileReader;
import com.google.gson.Gson;
//import game.utils.Resources;

public class Messages {

    private void init() {
        Gson gson = new Gson();

        // 1. JSON file to Java object
        Object JsonLanguage = gson.fromJson(new FileReader("C:\\resources\\english.json"), Object.class);

        System.out.println(JsonLanguage);

    }

    // PHYSICAL COMBAT
    public final static String DEAD_CANT_ATTACK = JsonLanguage.get("DEAD_CANT_ATTACK");
    public final static String CANT_ATTACK_DEAD = JsonLanguage.get("CANT_ATTACK_DEAD");

    public final static String CANT_ATTACK_CITIZEN = JsonLanguage.get("CANT_ATTACK_CITIZEN");
    public final static String NOT_ENOUGH_ENERGY = JsonLanguage.get("NOT_ENOUGH_ENERGY");

    public final static String ATTACK_FAILED = JsonLanguage.get("ATTACK_FAILED");
    public final static String ATTACKED_AND_FAILED = JsonLanguage.get("ATTACKED_AND_FAILED");

    public final static String SHIELD_DEFENSE = JsonLanguage.get("SHIELD_DEFENSE");
    public final static String DEFENDED_WITH_SHIELD = JsonLanguage.get("DEFENDED_WITH_SHIELD");

    public final static String KILL = JsonLanguage.get("KILL");
    public final static String KILLED = JsonLanguage.get("KILLED");

    public static final String USER_CRITIC_HIT = JsonLanguage.get("USER_CRITIC_HIT");
    public static final String VICTIM_CRITIC_HIT = JsonLanguage.get("VICTIM_CRITIC_HIT");

    public static final String USER_STAB_HIT = JsonLanguage.get("USER_STAB_HIT");
    public static final String VICTIM_STAB_HIT = JsonLanguage.get("VICTIM_STAB_HIT");

    public static final String USER_NORMAL_HIT = JsonLanguage.get("USER_NORMAL_HIT");
    public static final String VICTIM_NORMAL_HIT = JsonLanguage.get("VICTIM_NORMAL_HIT");

    // MAGIC COMBAT
    public static final String DAMAGE_TO = JsonLanguage.get("DAMAGE_TO");
    public static final String DAMAGED_BY = JsonLanguage.get("DAMAGED_BY");

    public final static String HEAL_TO = JsonLanguage.get("HEAL_TO");
    public final static String HEAL_BY = JsonLanguage.get("HEAL_BY");

    public static final String INVALID_TARGET = JsonLanguage.get("INVALID_TARGET");
    public static final String NOT_ENOUGHT_MANA = JsonLanguage.get("NOT_ENOUGHT_MANA");

    public static final String NOT_PARALYSIS = JsonLanguage.get("NOT_PARALYSIS");
    public static final String CANT_ATTACK_YOURSELF = JsonLanguage.get("CANT_ATTACK_YOURSELF");

    // MANA
    public static final String MANA_RECOVERED = JsonLanguage.get("MANA_RECOVERED");
    public static final String MEDITATE_STOP = JsonLanguage.get("MEDITATE_STOP");
    public static final String MEDITATE_START = JsonLanguage.get("MEDITATE_START");
    public static final String MANA_FULL = JsonLanguage.get("MANA_FULL");


    // OTHERS
    public static final String SEE_NOTHING_INTEREST = JsonLanguage.get("SEE_NOTHING_INTEREST");
    public static final String SEE_SOMEONE = JsonLanguage.get("SEE_SOMEONE");

}
