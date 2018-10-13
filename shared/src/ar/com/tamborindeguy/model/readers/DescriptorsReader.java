package ar.com.tamborindeguy.model.readers;

import ar.com.tamborindeguy.model.map.Map;
import ar.com.tamborindeguy.objects.types.Obj;

public interface DescriptorsReader {

    Map loadMap(String map);

    java.util.Map<Integer, Obj> loadObjects(String objects);

}
