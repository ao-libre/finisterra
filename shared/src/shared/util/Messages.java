package shared.util;

import java.io.FileReader;
import java.io.FileNotFoundException;
import com.google.gson.Gson;
//import game.utils.Resources;

public class Messages {

    private class JsonMessages {
        public String DEAD_CANT_ATTACK;
        public String CANT_ATTACK_DEAD;
        public String CANT_ATTACK_CITIZEN;
        public String NOT_ENOUGH_ENERGY;
        public String ATTACK_FAILED;
        public String ATTACKED_AND_FAILED;
        public String SHIELD_DEFENSE;
        public String DEFENDED_WITH_SHIELD;
        public String KILL;
        public String KILLED;
        public String USER_CRITIC_HIT;
        public String VICTIM_CRITIC_HIT;
        public String USER_STAB_HIT;
        public String VICTIM_STAB_HIT;
        public String USER_NORMAL_HIT;
        public String VICTIM_NORMAL_HIT;
        public String DAMAGE_TO;
        public String DAMAGED_BY;
        public String HEAL_TO;
        public String HEAL_BY;
        public String INVALID_TARGET;
        public String NOT_ENOUGHT_MANA;
        public String NOT_PARALYSIS;
        public String CANT_ATTACK_YOURSELF;
        public String MANA_RECOVERED;
        public String MEDITATE_STOP;
        public String MEDITATE_START;
        public String MANA_FULL;
        public String SEE_NOTHING_INTEREST;
        public String SEE_SOMEONE;
    }

    private void init() {
        try
        {
            Gson gson = new Gson();
            JsonMessages JsonLanguageObject = gson.fromJson(new FileReader("C:\\resources\\languages\\english.json"), JsonMessages.class);

            this.DEAD_CANT_ATTACK = JsonLanguageObject.DEAD_CANT_ATTACK;
            this.CANT_ATTACK_DEAD = JsonLanguageObject.CANT_ATTACK_DEAD;
            this.CANT_ATTACK_CITIZEN = JsonLanguageObject.CANT_ATTACK_CITIZEN;
            this.NOT_ENOUGH_ENERGY = JsonLanguageObject.NOT_ENOUGH_ENERGY;
            this.ATTACK_FAILED = JsonLanguageObject.ATTACK_FAILED;
            this.ATTACKED_AND_FAILED = JsonLanguageObject.ATTACKED_AND_FAILED;
            this.SHIELD_DEFENSE = JsonLanguageObject.SHIELD_DEFENSE;
            this.DEFENDED_WITH_SHIELD = JsonLanguageObject.DEFENDED_WITH_SHIELD;
            this.KILL = JsonLanguageObject.KILL;
            this.KILLED = JsonLanguageObject.KILLED;
            this.USER_CRITIC_HIT = JsonLanguageObject.USER_CRITIC_HIT;
            this.VICTIM_CRITIC_HIT = JsonLanguageObject.VICTIM_CRITIC_HIT;
            this.USER_STAB_HIT = JsonLanguageObject.USER_STAB_HIT;
            this.VICTIM_STAB_HIT = JsonLanguageObject.VICTIM_STAB_HIT;
            this.USER_NORMAL_HIT = JsonLanguageObject.USER_NORMAL_HIT;
            this.VICTIM_NORMAL_HIT = JsonLanguageObject.VICTIM_NORMAL_HIT;
            this.DAMAGE_TO = JsonLanguageObject.DAMAGE_TO;
            this.DAMAGED_BY = JsonLanguageObject.DAMAGED_BY;
            this.HEAL_TO = JsonLanguageObject.HEAL_TO;
            this.HEAL_BY = JsonLanguageObject.HEAL_BY;
            this.INVALID_TARGET = JsonLanguageObject.INVALID_TARGET;
            this.NOT_ENOUGHT_MANA = JsonLanguageObject.NOT_ENOUGHT_MANA;
            this.NOT_PARALYSIS = JsonLanguageObject.NOT_PARALYSIS;
            this.CANT_ATTACK_YOURSELF = JsonLanguageObject.CANT_ATTACK_YOURSELF;
            this.MANA_RECOVERED = JsonLanguageObject.MANA_RECOVERED;
            this.MEDITATE_STOP = JsonLanguageObject.MEDITATE_STOP;
            this.MEDITATE_START = JsonLanguageObject.MEDITATE_START;
            this.MANA_FULL = JsonLanguageObject.MANA_FULL;
            this.SEE_NOTHING_INTEREST = JsonLanguageObject.SEE_NOTHING_INTEREST;
            this.SEE_SOMEONE = JsonLanguageObject.SEE_SOMEONE;
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("ERROR: No language file found");
            System.exit(0);
        }

    }

    // PHYSICAL COMBAT
    public static String DEAD_CANT_ATTACK;
    public static String CANT_ATTACK_DEAD;

    public static String CANT_ATTACK_CITIZEN;
    public static String NOT_ENOUGH_ENERGY;

    public static String ATTACK_FAILED;
    public static String ATTACKED_AND_FAILED;

    public static String SHIELD_DEFENSE;
    public static String DEFENDED_WITH_SHIELD;

    public static String KILL;
    public static String KILLED;

    public static String USER_CRITIC_HIT;
    public static String VICTIM_CRITIC_HIT;

    public static String USER_STAB_HIT;
    public static String VICTIM_STAB_HIT;

    public static String USER_NORMAL_HIT;
    public static String VICTIM_NORMAL_HIT;

    // MAGIC COMBAT
    public static String DAMAGE_TO;
    public static String DAMAGED_BY;

    public static String HEAL_TO;
    public static String HEAL_BY;

    public static String INVALID_TARGET;
    public static String NOT_ENOUGHT_MANA;

    public static String NOT_PARALYSIS;
    public static String CANT_ATTACK_YOURSELF;

    // MANA
    public static String MANA_RECOVERED;
    public static String MEDITATE_STOP;
    public static String MEDITATE_START;
    public static String MANA_FULL;


    // OTHERS
    public static String SEE_NOTHING_INTEREST;
    public static String SEE_SOMEONE;

}
