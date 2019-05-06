package entity.character.states;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

import java.io.Serializable;

@DelayedComponentRemoval
public class Immobile extends Component implements Serializable {
    public Immobile() {
    }

}
