package com.argentum;

import com.argentum.loaders.GraphicLoader;
import com.argentum.readers.AOAssetsReader;
import com.argentum.readers.AssetsReader;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePackerFileProcessor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.LongMap;
import game.loaders.DescriptorsLoader;
import model.descriptors.*;
import model.textures.AOAnimation;
import model.textures.AOImage;
import shared.model.map.Map;
import shared.util.AOJson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static shared.util.SharedResources.JSON_EXT;

public class Generate extends ApplicationAdapter {
    private static LongMap<GraphicLoader.Graphic> graphics = new LongMap<>();
    private static LongMap<BodyDescriptor> bodies = new LongMap<>();
    private static LongMap<HeadDescriptor> heads = new LongMap<>();
    private static LongMap<HelmetDescriptor> helmets = new LongMap<>();
    private static LongMap<WeaponDescriptor> weapons = new LongMap<>();
    private static LongMap<ShieldDescriptor> shields = new LongMap<>();
    private static LongMap<FXDescriptor> fxs = new LongMap<>();
    private static final AssetsReader reader = new AOAssetsReader();

    private Json json = new AOJson();

    private void load() {
        graphics = reader.loadGraphics();
        bodies = reader.loadBodies();
        weapons = reader.loadWeapons();
        shields = reader.loadShields();
        heads = reader.loadHeads();
        helmets = reader.loadHelmets();
        fxs = reader.loadFxs();
    }

    @Override
    public void create() {
        super.create();
        // load all
        load();
        // generate atlas
        generateAtlas();
        // generate jsons
        generateMaps();
        generateDescriptors();
    }

    private void generateDescriptors() {
        save(bodies, BodyDescriptor.class);
        save(heads, BodyDescriptor.class);
        save(helmets, BodyDescriptor.class);
        save(weapons, BodyDescriptor.class);
        save(shields, BodyDescriptor.class);
        save(fxs, BodyDescriptor.class);
        saveImages();
        saveAnimations();
    }

    private void generateMaps() {
        // todo
    }

    private void generateAtlas() {
        File output = new File("output/graphics/");
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.fast = true;
        settings.paddingX = 1;
        settings.paddingY = 1;
        settings.pot = true;
        settings.combineSubdirectories = true;
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        TexturePacker.process(settings, "assets/" + Game.GAME_GRAPHICS_PATH, "assets/output/graphics/", "images");
//        TexturePacker texturePacker = new TexturePacker(output, settings);
//        List<GraphicLoader.Graphic> graphicsList = new ArrayList<>();
//        graphics.forEach(graphic -> {
//            graphicsList.add(graphic.value);
//        });
//        List<AOImage> images = graphicsList
//                .stream()
//                .filter(g -> !g.isAnimation())
//                .map(GraphicLoader.Graphic::getImage)
//                .sorted(Comparator.comparingInt(AOImage::getId))
//                .collect(Collectors.toList());
//
//        images.forEach(image -> {
//            BufferedImage bufferedImage = null;
//            try {
//                bufferedImage = createImage(image);
//                String name = image.getId() + "";
//                texturePacker.addImage(bufferedImage, name);
//
//            } catch (IOException | URISyntaxException e) {
//                e.printStackTrace();
//            }
//        });
//
//        texturePacker.pack(output, "images");
    }

    private BufferedImage createImage(AOImage image) throws IOException, URISyntaxException {

        File file = getFileFromResource(Game.GAME_GRAPHICS_PATH + image.getFileNum() + ".png");
        BufferedImage bufferedImage = ImageIO.read(file);
        int width = Math.min(bufferedImage.getWidth() - image.getX(), image.getX() + image.getWidth());
        return bufferedImage.getSubimage(image.getX(), image.getY(), width, image.getHeight());
    }

    private File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());

            return new File(resource.toURI());
        }

    }

    private void saveImages() {
        List<GraphicLoader.Graphic> graphicsList = new ArrayList<>();
        graphics.forEach(graphic -> {
            graphicsList.add(graphic.value);
        });
        List<AOImage> images = graphicsList.stream()
                .filter(g -> !g.isAnimation())
                .map(GraphicLoader.Graphic::getImage)
                .sorted(Comparator.comparingInt(AOImage::getId))
                .collect(Collectors.toList());
        json.toJson(images, ArrayList.class, AOImage.class, Gdx.files.local("output/descriptors/animations" + JSON_EXT));

    }

    private void saveAnimations() {
        List<GraphicLoader.Graphic> graphicsList = new ArrayList<>();
        graphics.forEach(graphic -> {
            graphicsList.add(graphic.value);
        });
        List<AOAnimation> animations = graphicsList.stream()
                .filter(GraphicLoader.Graphic::isAnimation)
                .map(GraphicLoader.Graphic::getAnimation)
                .sorted(Comparator.comparingInt(AOAnimation::getId))
                .collect(Collectors.toList());
        json.toJson(animations, ArrayList.class, AOAnimation.class, Gdx.files.local("output/descriptors/animations" + JSON_EXT));
    }

    private void save(LongMap<? extends Descriptor> descriptors, Class<? extends Descriptor> dClass) {
        List<Descriptor> list = new ArrayList<>();
        descriptors.values().forEach(list::add);
        List<Descriptor> toSave = list.stream()
                .filter(this::anyAnimation)
                .sorted(Comparator.comparingInt(Descriptor::getId))
                .collect(Collectors.toList());
        json.toJson(toSave, ArrayList.class, dClass, Gdx.files.local("output/descriptors/" + getFileName(dClass) + JSON_EXT));
    }

    private boolean anyAnimation(Descriptor t) {
        return Stream.of(t.getIndexs()).flatMapToInt(Arrays::stream).anyMatch(i -> i > 0);
    }

    private String getFileName(Class<? extends Descriptor> tClass) {
        String fileName = "file";
        if (tClass.equals(HeadDescriptor.class)) {
            fileName = DescriptorsLoader.HEADS;
        } else if (tClass.equals(BodyDescriptor.class)) {
            fileName = DescriptorsLoader.BODIES;
        } else if (tClass.equals(FXDescriptor.class)) {
            fileName = DescriptorsLoader.FXS;
        } else if (tClass.equals(HelmetDescriptor.class)) {
            fileName = DescriptorsLoader.HELMETS;
        } else if (tClass.equals(ShieldDescriptor.class)) {
            fileName = DescriptorsLoader.SHIELDS;
        } else if (tClass.equals(WeaponDescriptor.class)) {
            fileName = DescriptorsLoader.WEAPONS;
        }
        return fileName;
    }
}
