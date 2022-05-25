package shared.interfaces;

import com.artemis.E;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// @todo Refactorizar esta clase, CharHero y CharRace
// La lógica de Artemis no va acá
// No soporta i18n
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

    @Deprecated
    public static CharClass of(E entity) {
        int heroId = entity.getCharHero().heroId;
        Hero hero = Hero.values()[heroId];
        return VALUES.get(hero.getClassId());
    }

    // @todo Esto es un arreglo de fortuna para desacoplar la lógica de ECS de la clase
    public static CharClass of(int heroId) {
        Hero hero = Hero.values()[heroId];
        return VALUES.get(hero.getClassId());
    }
}
