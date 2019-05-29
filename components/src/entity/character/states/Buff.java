package entity.character.states;

import com.artemis.Component;
import entity.character.attributes.Attribute;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Buff extends Component {
    private Map<Attribute,Float> buffedAttributes = new ConcurrentHashMap<>();

    public Buff() {}

    public void addAttribute(Attribute attribute, float duration) {
        buffedAttributes.put(attribute, duration);
    }

    public Map<Attribute,Float> getBuffedAtributes() {
        return buffedAttributes;
    }
}
