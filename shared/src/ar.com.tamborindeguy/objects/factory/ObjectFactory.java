package ar.com.tamborindeguy.objects.factory;

import ar.com.tamborindeguy.objects.types.*;
import ar.com.tamborindeguy.objects.types.common.*;
import org.ini4j.Profile;

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

    public static Obj createObject(int kind, String name, int grhIndex) {
        return createObject(Type.values()[kind-1], name, grhIndex);
    }
    private static Obj createObject(Type kind, String name, int grhIndex) {
        switch (kind) {
            case ANVIL:
                return new AnvilObj(name, grhIndex);
            case GEM:
                return new GemObj(name, grhIndex);
            case AURA:
            case BOAT:
                return new BoatObj(name, grhIndex) ;
            case BOOK:
                return new BookObj(name, grhIndex);
            case DOOR:
                return new DoorObj(name, grhIndex);
            case FOOD:
                return new Food(name, grhIndex);
            case GOLD:
                return new GoldObj(name, grhIndex);
            case KEYS:
                return new KeyObj(name, grhIndex);
            case RING:
                return new MagicObj(name, grhIndex);
            case TREE:
                return new TreeObj(name, grhIndex);
            case WOOD:
                return new WoodObj(name, grhIndex);
            case ARMOR:
                return new ArmorObj(name, grhIndex);
            case ARROW:
                return new ArrowObj(name, grhIndex);
            case DRINK:
                return new DrinkObj(name, grhIndex);
            case FORGE:
                return new ForgeObj(name, grhIndex);
            case FORUM:
                return new ForumObj(name, grhIndex);
            case JEWEL:
                return new JewelObj(name, grhIndex);
            case METAL:
                return new MineralObj(name, grhIndex);
            case SPELL:
                return new SpellObj(name, grhIndex);
            case STAIN:
                return new StainObj(name, grhIndex);
            case BOTTLE:
                return new DrinkObj(name, grhIndex);
            case FLOWER:
                return new FlowerObj(name, grhIndex);
            case HELMET:
                return new HelmetObj(name, grhIndex);
            case POSTER:
                return new PosterObj(name, grhIndex);
            case POTION:
                return new PotionObj(name, grhIndex);
            case SHIELD:
                return new ShieldObj(name, grhIndex);
            case WEAPON:
                return new WeaponObj(name, grhIndex);
            case BONFIRE:
                return new BonfireObj(name, grhIndex);
            case DEPOSIT:
                return new DepositObj(name, grhIndex);
            case MUSICAL:
                return new MusicalObj(name, grhIndex);
            case TELEPORT:
                return new TeleportObj(name, grhIndex);
            case CONTAINER:
                return new ContainerObj(name, grhIndex);
            case FURNITURE:
                return new FurnitureObj(name, grhIndex);
            case EMPTY_BOTTLE:
                default:
                    return new Obj(name, grhIndex) {
                        @Override
                        public Type getType() {
                            return null;
                        }
                    };
        }
    }

    public static void fillObject(Obj obj, Profile.Section section){
        obj.fillObject(section);
    }


    public static void fillCommon(Obj obj, Profile.Section section) {
        boolean collectable = section.get("Agarrable", int.class) == 1 ? true : false;
        int value = section.get("Valor", int.class);
        boolean crucial = section.get("Crucial", int.class) == 1 ? true : false;
        boolean newbie = section.get("Newbie", int.class) == 1 ? true : false;
        boolean notDrop = section.get("NoSeCae", int.class) == 1 ? true : false;

        obj.setCollectable(collectable);
        obj.setValue(value);
        obj.setCrucial(crucial);
        obj.setNewbie(newbie);
        obj.setNotDrop(notDrop);
    }

    public static void fill(ArmorObj obj, Profile.Section section) {
        obj.setBodyNumber(section.get(NUM_ROPAJE, int.class));
        obj.setDwarf(section.get(RAZA_ENANA, int.class) == 1 ? true : false);
        obj.setMaxDef(section.get(MAX_DEF, int.class));
        obj.setMinDef(section.get(MIN_DEF, int.class));
        obj.setWomen(section.get(MUJER, int.class) == 1 ? true : false);
    }

    public static void fill(ArrowObj obj, Profile.Section section) {
        obj.setMaxHit(section.get(MAX_HIT, int.class));
        obj.setMinHit(section.get(MIN_HIT, int.class));
    }

    public static void fill(BoatObj obj, Profile.Section section) {
        obj.setBodyIndex(section.get(NUM_ROPAJE, int.class));
        obj.setMaxDef(section.get(MAX_DEF, int.class ));
        obj.setMinDef(section.get(MIN_DEF, int.class ));
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
        obj.setAnimationId(section.get(ANIM, int.class));
        obj.setMaxHit(section.get(MAX_HIT, int.class));
        obj.setMinHit(section.get(MIN_HIT, int.class));
        obj.setDwarfAnimationId(section.get(RAZA_ENANA_ANIM, int.class));
    }
}
