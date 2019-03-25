package shared.interfaces;

public enum CharClass {
    MAGICIAN,
    WARRIOR,
    CLERIC,
    PALADIN,
    ASSESIN,
    BARD,
    DRUID,
    PIRATE,
    THIEF,
    ROGUE,
    ARCHER;

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
                return ASSESIN;
            case "bardo":
                return BARD;
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
}
