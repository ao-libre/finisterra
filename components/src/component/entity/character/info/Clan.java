package component.entity.character.info;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class Clan extends Component {

    public String name;

    public Clan() {
    }

    public Clan(String clan) {
        this.name = clan;
    }

}
