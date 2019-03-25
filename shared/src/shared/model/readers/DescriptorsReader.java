package shared.model.readers;

import shared.model.Spell;
import shared.model.map.Map;
import shared.objects.types.Obj;

public interface DescriptorsReader {

    Map loadMap(String map);

    java.util.Map<Integer, Obj> loadObjects(String objects);

    java.util.Map<Integer, Spell> loadSpells(String spells);

}
