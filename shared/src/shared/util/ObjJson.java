package shared.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import shared.objects.types.*;
import shared.objects.types.common.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ObjJson extends Json {

    public ObjJson() {
        setOutputType(JsonWriter.OutputType.json);
        setIgnoreUnknownFields(true);
        Arrays.stream(Type.values()).forEach(type -> {
            final Class clazz = getClassForType(type);
            if (clazz!= null) {
                addClassTag(type.name().toLowerCase(), clazz);
            }
        });
    }

    public static Class getClassForType(Type type) {
        switch (type) {
            case HELMET:
                return HelmetObj.class;
            case WEAPON:
                return WeaponObj.class;
            case SHIELD:
                return ShieldObj.class;
            case POTION:
                return PotionObj.class;
            case ARMOR:
                return ArmorObj.class;
            case GEM:
                return GemObj.class;
            case BOAT:
                return BoatObj.class;
            case BOOK:
                return BookObj.class;
            case DOOR:
                return DoorObj.class;
            case FOOD:
                return Food.class;
            case GOLD:
                return GoldObj.class;
            case KEYS:
                return KeyObj.class;
            case TREE:
                return TreeObj.class;
            case WOOD:
                return WoodObj.class;
            case ANVIL:
                return AnvilObj.class;
            case ARROW:
                return ArrowObj.class;
            case DRINK:
                return DrinkObj.class;
            case FORGE:
                return ForgeObj.class;
            case FORUM:
                return ForumObj.class;
            case JEWEL:
                return JewelObj.class;
            case SPELL:
                return SpellObj.class;
            case STAIN:
                return StainObj.class;
            case FLOWER:
                return FlowerObj.class;
            case POSTER:
                return PosterObj.class;
            case BONFIRE:
                return BonfireObj.class;
            case DEPOSIT:
                return DepositObj.class;
            case MUSICAL:
                return MusicalObj.class;
            case TELEPORT:
                return TeleportObj.class;
            case CONTAINER:
                return ContainerObj.class;
            case FURNITURE:
                return FurnitureObj.class;
            case BOTTLE:
                return DrinkObj.class;
        }
        return null;
    }

    public static void loadObjectsByType(Map<Integer, Obj> objects, FileHandle folder) {
        ObjJson json = new ObjJson();
        Arrays.stream(Type.values()).forEach(type -> {
            final Class classForType = json.getClassForType(type);
            final FileHandle jsonFile = folder.child(type.name().toLowerCase() + ".json");
            if (jsonFile.exists() && !jsonFile.isDirectory()) {
                final ArrayList<? extends Obj> listObjs = json.fromJson(ArrayList.class, classForType, jsonFile);
                listObjs.forEach(obj -> {
                    objects.put(obj.getId(), obj);
                });
            }
        });
    }
}
