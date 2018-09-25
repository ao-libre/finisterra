package ar.com.tamborindeguy.model.loaders;

import ar.com.tamborindeguy.model.Objects;
import ar.com.tamborindeguy.objects.factory.ObjectFactory;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.Type;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectLoader extends Loader<Objects>{

    public static final String OBJ = "obj";
    public static final String GRH_INDEX = "GrhIndex";
    public static final String NAME = "Name";
    public static final String OBJTYPE = "ObjType";

    @Override
    public Objects load(DataInputStream file) throws IOException {
        Map<Type, Set<Obj>> objects = new HashMap<>();
        Map<Integer, Obj> objectsById = new HashMap<>();
        Ini iniFile = new Ini();
        Config c = new Config();
        c.setLowerCaseSection(true);
        iniFile.setConfig(c);

        iniFile.load(file);
        int numObjs = Integer.parseInt(iniFile.get("init", "NumOBJs"));

        for (int i = 1; i <= numObjs; i++){
            Profile.Section section = iniFile.get(OBJ + String.valueOf(i));
            if (section == null) {
                continue;
            }
            Integer type = section.get(OBJTYPE, int.class);;
            Integer grhIndex = section.get(GRH_INDEX, int.class);
            String name = section.get(NAME);
            Obj object = ObjectFactory.createObject(type, name, grhIndex);
            ObjectFactory.fillObject(object, section);
            objects.computeIfAbsent(Type.values()[type-1], (e) -> new HashSet<>()).add(object);
            objectsById.put(i, object);
        }
        return new Objects(objects, objectsById);
    }
}
