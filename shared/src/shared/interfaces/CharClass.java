package shared.interfaces;

import com.artemis.E;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum CharClass {
    ARCHER,
    ASSASSIN,
    BARDIC,
    CLERIC,
    DRUID,
    MAGICIAN,
    PALADIN,
    PIRATE,
    ROGUE,
    THIEF,
    WARRIOR;

    private static final List<CharClass> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();

    public static CharClass getClass(String classString) {
        switch (classString.toLowerCase()) {
            case "mago":
                return MAGICIAN;
            case "paladin":
                return PALADIN;
            case "clerigo":
                return CLERIC;
            case "guerrero":
                return WARRIOR;
            case "asesino":
                return ASSASSIN;
            case "bardo":
                return BARDIC;
            case "druida":
                return DRUID;
            case "pirata":
                return PIRATE;
            case "ladron":
                return THIEF;
            case "bandido":
                return ROGUE;
            case "cazador":
                return ARCHER;
        }
        return null;
    }

    public static CharClass get(E entity) {
        int heroId = entity.getCharHero().heroId;
        Hero hero = Hero.values()[heroId];
        return VALUES.get(hero.getClassId());
    }
}
