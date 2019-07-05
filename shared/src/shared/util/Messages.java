package shared.util;

public class Messages {

    // PHYSICAL COMBAT
    public final static String DEAD_CANT_ATTACK = "Estas muerto, no puedes atacar!";
    public final static String CANT_ATTACK_DEAD = "No puedes atacar a un muerto!";

    public final static String CANT_ATTACK_CITIZEN = "No puedes atacar a otro ciudadano, a menos que te quites el seguro";
    public final static String NOT_ENOUGH_ENERGY = "No tienes suficiente energia";

    public final static String ATTACK_FAILED = "Has fallado!!";
    public final static String ATTACKED_AND_FAILED = "%s ha intentado atacarte pero fallo!!";

    public final static String SHIELD_DEFENSE = "Has rechazado con el escudo!!";
    public final static String DEFENDED_WITH_SHIELD = "%s te ha rechazado el ataque con el escudo!!";

    public final static String KILL = "Has matado a %s !!";
    public final static String KILLED = "Has sido matado por %s. Estas muerto!!";

    public static final String USER_CRITIC_HIT = "Has golpeado criticamente a %s por %d";
    public static final String VICTIM_CRITIC_HIT = "%s te ha golpeado criticamente por por %d";

    public static final String USER_STAB_HIT = "Has apuñalado a %s por %d";
    public static final String VICTIM_STAB_HIT = "%s te ha apuñalado por %d";

    public static final String USER_NORMAL_HIT = "Has golpeado a %s por %d";
    public static final String VICTIM_NORMAL_HIT = "%s te ha golpeado por %d";

    // MAGIC COMBAT
    public static final String DAMAGE_TO = "Le has quitado %d puntos de vida a %s";
    public static final String DAMAGED_BY = "%s te ha quitado %d puntos de vida";

    public final static String HEAL_TO = "Has curado a %s por %d puntos de vida !!";
    public final static String HEAL_BY = "Has sido curado por %s, recuperaste %d puntos de vida!!";

    public static final String INVALID_TARGET = "No es un target valido!";
    public static final String NOT_ENOUGHT_MANA = "No tienes mana suficiente.";

    public static final String NOT_PARALYSIS = "No esta paralizado!";
    public static final String CANT_ATTACK_YOURSELF = "No puedes atacarte a vos mismo";

    // MANA
    public static final String MANA_RECOVERED = "Has recuperado %d puntos de mana!";
    public static final String MEDITATE_STOP = "Has terminado de meditar.";
    public static final String MEDITATE_START = "Comienzas a meditar.";
    public static final String MANA_FULL = "Ya tienes toda la mana.";

    // ENTRENAMIENTO
    public static final String EXP_GAIN = "Has ganado %d puntos de experiencia!";

    public static final String CANT_ATTACK = "No puedes atacar tan rápido";
}
