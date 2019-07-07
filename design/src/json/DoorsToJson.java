package json;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import model.readers.AODescriptorsReader;
import shared.objects.types.DoorObj;
import shared.objects.types.DoorObj2;
import shared.objects.types.Obj;
import shared.objects.types.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DoorsToJson {

    public static void doYourMagic() {
        // Read doors
        AODescriptorsReader reader = new AODescriptorsReader();
        Map<Integer, Obj> objs = reader.loadObjects("obj");

        List<DoorObj2> newDoors = objs
                .values()
                .stream()
                .filter(o -> o.getType().equals(Type.DOOR))
                .map(DoorObj.class::cast)
                .map(d -> toNewDoor(d, objs.values()))
                .collect(Collectors.toList());

        // Save to json

        Json objJson = new Json();
        objJson.setOutputType(JsonWriter.OutputType.json);
        String s = objJson.toJson(newDoors, ArrayList.class, DoorObj2.class);
        System.out.println(s);
    }

    private static DoorObj2 toNewDoor(DoorObj door, Collection<Obj> doors) {
        DoorObj2 newDoor = new DoorObj2(door.getId(), door.getName(), door.getGrhIndex());
        doors
                .stream()
                .filter(d -> d.getId() == door.getCloseIndex())
                .map(Obj::getGrhIndex)
                .findFirst().ifPresent(newDoor::setClosedGrh);

        newDoor.setOpen(door.getOpenDoor());
        return newDoor;
    }
}
