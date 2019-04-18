package entity.character.info;

import com.artemis.Component;

import java.io.Serializable;

public class CharHero extends Component implements Serializable {
    public int heroId;

    public CharHero() {}

    public CharHero(int heroId) {
        this.heroId = heroId;
    }

    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }
}
