package shared.interfaces;

import com.artemis.E;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final List<CharClass> VALUES = Arrays.stream(values()).collect(Collectors.toList());

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

    public static CharClass of(E entity) {
        int heroId = entity.getCharHero().heroId;
        Hero hero = Hero.values()[heroId];
        return VALUES.get(hero.getClassId());
    }

}
