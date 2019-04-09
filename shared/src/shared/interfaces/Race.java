package shared.interfaces;

import com.artemis.E;

public enum Race {
    HUMAN,
    GNOME,
    ELF,
    DROW,
    DWARF;

    public static Race of(E entity) {
        int heroId = entity.getCharHero().heroId;
        Hero hero = Hero.getHeros().get(heroId);
        return values()[hero.getRaceId()];
    }
}
