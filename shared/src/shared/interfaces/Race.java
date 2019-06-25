package shared.interfaces;

import com.artemis.E;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Race {
    HUMAN,
    GNOME,
    ELF,
    DROW,
    DWARF;

    private static final List<Race> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

    public static Race of(E entity) {
        int heroId = entity.getCharHero().heroId;
        Hero hero = Hero.VALUES.get(heroId);
        return VALUES.get(hero.getRaceId());
    }
}
