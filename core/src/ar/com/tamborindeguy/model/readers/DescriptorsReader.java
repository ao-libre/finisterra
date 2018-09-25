package ar.com.tamborindeguy.model.readers;

import ar.com.tamborindeguy.model.Objects;
import ar.com.tamborindeguy.model.map.Map;

public interface DescriptorsReader {

    Map loadMap(String map);

    Objects loadObjects(String objects);

}
