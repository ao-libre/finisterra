package game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import model.descriptors.*;
import model.readers.GenericReader;
import model.serializers.BodyDescriptorSerializer;
import model.serializers.GraphicsSerializer;
import shared.model.Graphic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DescriptorHandler {

    public static final String GAME_DESCRIPTORS_FOLDER = "data/descriptors/";
    public static final String JSON_EXTENSION = ".json";
    public static final String GRAPHICS = "graphics";
    public static final String BODIES = "bodies";
    public static final String WEAPONS = "weapons";
    public static final String SHIELDS = "shields";
    public static final String HEADS = "heads";
    public static final String HELMETS = "helmets";
    public static final String FXS = "fxs";

    private static Map<Integer, Graphic> graphics;
    private static Map<Integer, BodyDescriptor> bodies;
    private static List<HeadDescriptor> heads;
    private static List<HelmetDescriptor> helmets;
    private static List<WeaponDescriptor> weapons;
    private static List<ShieldDescriptor> shields;
    private static List<FXDescriptor> fxs;


    public static void load() {
        graphics = new GenericReader<Graphic>().read(GRAPHICS, Graphic.class, new GraphicsSerializer(), Graphic::getGrhIndex);
        bodies = new GenericReader<BodyDescriptor>().read(BODIES, BodyDescriptor.class, new BodyDescriptorSerializer(), Descriptor::getId);
        weapons = load(WEAPONS);
        shields = load(SHIELDS);
        heads = load(HEADS);
        helmets = load(HELMETS);
        fxs = load(FXS);
    }

    public static List load(String fileName) {
        Json json = getJson();
        FileHandle file = Gdx.files.internal(GAME_DESCRIPTORS_FOLDER + fileName + JSON_EXTENSION);
        return json.fromJson(ArrayList.class, file);
    }

    private static Json getJson() {
        Json json = new Json();
        json.addClassTag(GRAPHICS, Graphic.class);
        json.addClassTag(BODIES, BodyDescriptor.class);
        json.addClassTag(HEADS, HeadDescriptor.class);
        json.addClassTag(HELMETS, HelmetDescriptor.class);
        json.addClassTag(WEAPONS, WeaponDescriptor.class);
        json.addClassTag(SHIELDS, ShieldDescriptor.class);
        json.addClassTag(FXS, FXDescriptor.class);
        return json;
    }

    public static Map<Integer, Graphic> getGraphics() {
        return graphics;
    }

    public static Map<Integer, BodyDescriptor> getBodies() {
        return bodies;
    }

    public static List<FXDescriptor> getFxs() {
        return fxs;
    }

    public static List<HeadDescriptor> getHeads() {
        return heads;
    }

    public static List<HelmetDescriptor> getHelmets() {
        return helmets;
    }

    public static List<ShieldDescriptor> getShields() {
        return shields;
    }

    public static List<WeaponDescriptor> getWeapons() {
        return weapons;
    }

    public static BodyDescriptor getBody(int index) {
        return bodies.get(index);
    }

    public static HeadDescriptor getHead(int index) {
        return heads.get(index);
    }

    public static HelmetDescriptor getHelmet(int index) {
        return helmets.get(index);
    }

    public static FXDescriptor getFX(int index) {
        return fxs.get(index);
    }

    public static ShieldDescriptor getShield(int index) {
        return shields.get(index);
    }

    public static WeaponDescriptor getWeapon(int index) {
        return weapons.get(index);
    }

    public static Graphic getGraphic(int index) {
        return graphics.get(index);
    }

}
