package component.entity.character.states;

import com.artemis.Component;
import com.artemis.annotations.PooledWeaver;
import component.entity.character.attributes.Attribute;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PooledWeaver
public class Buff extends Component {
    private Map<Attribute, Float> buffedAttributes = new ConcurrentHashMap<>();

    public Buff() {
    }

    public void addAttribute(Attribute attribute, float duration) {
        buffedAttributes.put(attribute, duration);
    }

    public Map<Attribute, Float> getBuffedAtributes() {
        return buffedAttributes;
    }
}
