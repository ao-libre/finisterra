package shared.objects.factory;

import org.ini4j.Profile;
import shared.objects.types.*;
import shared.objects.types.common.*;

public class ObjectFactory {

    public static final String NUM_ROPAJE = "NumRopaje";
    public static final String RAZA_ENANA = "RazaEnana";
    public static final String MAX_DEF = "MaxDef";
    public static final String MIN_DEF = "MinDef";
    public static final String MUJER = "Mujer";
    public static final String MAX_HIT = "MaxHit";
    public static final String MIN_HIT = "MinHit";
    public static final String MIN_AGU = "MinAgu";
    public static final String MIN_HAM = "MinHam";
    public static final String ANIM = "Anim";
    public static final String TIPO_POCION = "TipoPocion";
    public static final String DURACION_EFECTO = "DuracionEfecto";
    public static final String MAX_MODIFICADOR = "MaxModificador";
    public static final String MIN_MODIFICADOR = "MinModificador";
    public static final String RAZA_ENANA_ANIM = "RazaEnanaAnim";
    public static final String HECHIZO_INDEX = "HechizoIndex";
    public static final String TEXTO = "Texto";
    public static final String V_GRANDE = "VGrande";
    public static final String CLAVE = "CLAVE";
    public static final String LINGOTEINDEX = "Lingoteindex";

    public ObjectFactory() {
    }

    public static void fillCommon(Obj obj, Profile.Section section) {
        boolean collectable = section.get("Agarrable", int.class) == 1;
        int value = section.get("Valor", int.class);
        boolean crucial = section.get("Crucial", int.class) == 1;
        boolean newbie = section.get("Newbie", int.class) == 1;
        boolean notDrop = section.get("NoSeCae", int.class) == 1;

        obj.setCollectable(collectable);
        obj.setValue(value);
        obj.setCrucial(crucial);
        obj.setNewbie(newbie);
        obj.setNotDrop(notDrop);
    }

    public static void fill(ArmorObj obj, Profile.Section section) {
        obj.setBodyNumber(section.get(NUM_ROPAJE, int.class));
        obj.setDwarf(section.get(RAZA_ENANA, int.class) == 1);
        obj.setMaxDef(section.get(MAX_DEF, int.class));
        obj.setMinDef(section.get(MIN_DEF, int.class));
        obj.setWomen(section.get(MUJER, int.class) == 1);
    }

    public static void fill(ArrowObj obj, Profile.Section section) {
        obj.setMaxHit(section.get(MAX_HIT, int.class));
        obj.setMinHit(section.get(MIN_HIT, int.class));
    }

    public static void fill(BoatObj obj, Profile.Section section) {
        obj.setBodyIndex(section.get(NUM_ROPAJE, int.class));
        obj.setMaxDef(section.get(MAX_DEF, int.class));
        obj.setMinDef(section.get(MIN_DEF, int.class));
        obj.setMaxHit(section.get(MAX_HIT, int.class));
        obj.setMinHit(section.get(MIN_HIT, int.class));
    }

    public static void fill(DepositObj obj, Profile.Section section) {
        obj.setMineralIndex(section.get("MineralIndex", int.class));
    }

    public static void fill(DoorObj obj, Profile.Section section) {
        obj.setOpenDoor(section.get("PuertaAbierta", int.class) == 1);
        obj.setCloseIndex(section.get("IndexCerrada", int.class));
        obj.setHasKey(section.get("TieneLlave", int.class) == 1);
        obj.setCloseKeyIndex(section.get("IndexCerradaLlave", int.class));
        obj.setOpenIndex(section.get("IndexAbierta", int.class));
    }

    public static void fill(DrinkObj obj, Profile.Section section) {
        obj.setMin(section.get(MIN_AGU, int.class));
    }

    public static void fill(Food obj, Profile.Section section) {
        obj.setMin(section.get(MIN_HAM, int.class));
    }

    public static void fill(HelmetObj obj, Profile.Section section) {
        obj.setBodyNumber(section.get(NUM_ROPAJE, int.class));
        obj.setAnimationId(section.get(ANIM, int.class));
        obj.setMinDef(section.get(MIN_DEF, int.class));
        obj.setMaxDef(section.get(MAX_DEF, int.class));
    }

    public static void fill(KeyObj obj, Profile.Section section) {
        obj.setKey(section.get(CLAVE, int.class));
    }

    public static void fill(MagicObj obj, Profile.Section section) {
        obj.setMaxDef(section.get(MAX_DEF, int.class));
        obj.setMinDef(section.get(MIN_DEF, int.class));
        obj.setMaxHit(section.get(MAX_HIT, int.class));
        obj.setMinHit(section.get(MIN_HIT, int.class));

    }

    public static void fill(MineralObj obj, Profile.Section section) {
        obj.setIngotIndex(section.get(LINGOTEINDEX, int.class));
    }

    public static void fill(PosterObj obj, Profile.Section section) {
        obj.setText(section.get(TEXTO));
        obj.setBig(section.get(V_GRANDE, int.class));
    }

    public static void fill(PotionObj obj, Profile.Section section) {
        obj.setKind(section.get(TIPO_POCION, int.class));
        obj.setEffecTime(section.get(DURACION_EFECTO, int.class));
        obj.setMax(section.get(MAX_MODIFICADOR, int.class));
        obj.setMin(section.get(MIN_MODIFICADOR, int.class));
    }

    public static void fill(ShieldObj obj, Profile.Section section) {
        obj.setAnimationId(section.get(ANIM, int.class));
        obj.setBodyNumber(section.get(NUM_ROPAJE, int.class));
        obj.setMaxDef(section.get(MAX_DEF, int.class));
        obj.setMinDef(section.get(MIN_DEF, int.class));
    }

    public static void fill(SpellObj obj, Profile.Section section) {
        obj.setSpellIndex(section.get(HECHIZO_INDEX, int.class));
    }

    public static void fill(WeaponObj obj, Profile.Section section) {
        obj.setStab(section.get("Apuñala", int.class) == 1);
        obj.setAnimationId(section.get(ANIM, int.class));
        obj.setMaxHit(section.get(MAX_HIT, int.class));
        obj.setMinHit(section.get(MIN_HIT, int.class));
        obj.setDwarfAnimationId(section.get(RAZA_ENANA_ANIM, int.class));
    }

    public Obj createObject(int id, int kind, String name, int grhIndex) {
        return createObject(id, Type.values()[kind - 1], name, grhIndex);
    }

    public Obj createObject(int id, Type kind, String name, int grhIndex) {
        switch (kind) {
            case ANVIL:
                return new AnvilObj(id, name, grhIndex);
            case GEM:
                return new GemObj(id, name, grhIndex);
            case AURA:
            case BOAT:
                return new BoatObj(id, name, grhIndex);
            case BOOK:
                return new BookObj(id, name, grhIndex);
            case DOOR:
                return new DoorObj(id, name, grhIndex);
            case FOOD:
                return new Food(id, name, grhIndex);
            case GOLD:
                return new GoldObj(id, name, grhIndex);
            case KEYS:
                return new KeyObj(id, name, grhIndex);
            case RING:
                return new MagicObj(id, name, grhIndex);
            case TREE:
                return new TreeObj(id, name, grhIndex);
            case WOOD:
                return new WoodObj(id, name, grhIndex);
            case ARMOR:
                return new ArmorObj(id, name, grhIndex);
            case ARROW:
                return new ArrowObj(id, name, grhIndex);
            case DRINK:
                return new DrinkObj(id, name, grhIndex);
            case FORGE:
                return new ForgeObj(id, name, grhIndex);
            case FORUM:
                return new ForumObj(id, name, grhIndex);
            case JEWEL:
                return new JewelObj(id, name, grhIndex);
            case METAL:
                return new MineralObj(id, name, grhIndex);
            case SPELL:
                return new SpellObj(id, name, grhIndex);
            case STAIN:
                return new StainObj(id, name, grhIndex);
            case BOTTLE:
                return new DrinkObj(id, name, grhIndex);
            case FLOWER:
                return new FlowerObj(id, name, grhIndex);
            case HELMET:
                return new HelmetObj(id, name, grhIndex);
            case POSTER:
                return new PosterObj(id, name, grhIndex);
            case POTION:
                return new PotionObj(id, name, grhIndex);
            case SHIELD:
                return new ShieldObj(id, name, grhIndex);
            case WEAPON:
                return new WeaponObj(id, name, grhIndex);
            case BONFIRE:
                return new BonfireObj(id, name, grhIndex);
            case DEPOSIT:
                return new DepositObj(id, name, grhIndex);
            case MUSICAL:
                return new MusicalObj(id, name, grhIndex);
            case TELEPORT:
                return new TeleportObj(id, name, grhIndex);
            case CONTAINER:
                return new ContainerObj(id, name, grhIndex);
            case FURNITURE:
                return new FurnitureObj(id, name, grhIndex);
            case EMPTY_BOTTLE:
            default:
                return new Obj(id, name, grhIndex) {
                    @Override
                    public Type getType() {
                        return null;
                    }
                };
        }
    }

    public void fillObject(Obj obj, Profile.Section section) {
        obj.fillObject(section);
    }
}
