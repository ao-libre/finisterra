package shared.interfaces;

import com.artemis.E;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Race {
    HUMAN,
    GNOME,
    ELF,
    DROW,
    DWARF;

    private static final List<Race> VALUES = Arrays.stream(values()).collect(Collectors.toList());

    public static Race of(E entity) {
        int heroId = entity.getCharHero().heroId;
        Hero hero = Hero.VALUES.get(heroId);
        return VALUES.get(hero.getRaceId());
    }
}
