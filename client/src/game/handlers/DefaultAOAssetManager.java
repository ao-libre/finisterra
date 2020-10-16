package game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.I18NBundle;
import com.esotericsoftware.minlog.Log;
import game.ClientConfiguration;
import game.loaders.*;
import game.loaders.ObjectsLoader.ObjectParameter;
import game.utils.Resources;
import game.utils.Skins.AOSkin;
import model.descriptors.*;
import model.textures.AOAnimation;
import model.textures.AOImage;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import shared.model.Spell;
import shared.objects.types.Obj;
import shared.objects.types.Type;
import shared.util.Messages;
import shared.util.SharedResources;

import javax.sound.midi.Sequencer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static game.loaders.DescriptorsLoader.*;
import static game.loaders.DescriptorsLoader.DescriptorParameter.descriptor;
import static game.utils.Resources.GAME_DESCRIPTORS_PATH;

// TODO convert to SYSTEM!
public class DefaultAOAssetManager extends AssetManager implements AOAssetManager {

    private static final Class<ArrayList<AOImage>> IMAGE_CLASS;
    private static final Class<ArrayList<AOAnimation>> ANIMATION_CLASS;
    private static final Class<HashMap<Integer, Obj>> OBJS_CLASS;
    private static final Class<HashMap<Integer, Spell>> SPELLS_CLASS;
    private static final Class<ArrayList<Descriptor>> DESCRIPTORS_CLASS;

    static {
        ArrayList<AOImage> integerGraphicHashMap = new ArrayList<>();
        IMAGE_CLASS = (Class<ArrayList<AOImage>>) integerGraphicHashMap.getClass();

        ArrayList<AOAnimation> integerAnimationHashMap = new ArrayList<>();
        ANIMATION_CLASS = (Class<ArrayList<AOAnimation>>) integerAnimationHashMap.getClass();

        HashMap<Integer, Obj> integerObjHashMap = new HashMap<>();
        OBJS_CLASS = (Class<HashMap<Integer, Obj>>) integerObjHashMap.getClass();

        HashMap<Integer, Spell> integerSpellHashMap = new HashMap<>();
        SPELLS_CLASS = (Class<HashMap<Integer, Spell>>) integerSpellHashMap.getClass();

        ArrayList<Descriptor> descriptors = new ArrayList<>();
        DESCRIPTORS_CLASS = (Class<ArrayList<Descriptor>>) descriptors.getClass();
    }

    private final String languagesFile;
    private final String[] languagesLocale;
    private Map<Integer, AOImage> images;
    private Map<Integer, AOAnimation> animations;
    private Map<Integer, ShieldDescriptor> shields;
    private Map<Integer, FXDescriptor> fxs;
    private Map<Integer, HeadDescriptor> heads;
    private Map<Integer, HelmetDescriptor> helmets;
    private Map<Integer, WeaponDescriptor> weapons;
    private Map<Integer, BodyDescriptor> bodies;

    private DefaultAOAssetManager(ClientConfiguration clientConfiguration) {
        this.languagesFile = SharedResources.LANGUAGES_FOLDER + "messages";
        this.languagesLocale = clientConfiguration.getInitConfig().getLanguage().split("_");
        setLoader(Sequencer.class, new MidiLoader());
        setLoader(ANIMATION_CLASS, ANIMATIONS + JSON_EXTENSION, new AnimationLoader());
        setLoader(IMAGE_CLASS, IMAGES + JSON_EXTENSION, new ImageLoader());
        setLoader(OBJS_CLASS, new ObjectsLoader());
        setLoader(SPELLS_CLASS, SharedResources.SPELLS_FILE + JSON_EXTENSION, new SpellsLoader());
        setLoader(DESCRIPTORS_CLASS, new DescriptorsLoader());
        setLoader(AOSkin.class, new AOSkinLoader());
    }

    private static DefaultAOAssetManager instance;
    private static final Object lock = new Object(); //thread-safety singleton lock

    // singleton
    public static DefaultAOAssetManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) instance = new DefaultAOAssetManager(ClientConfiguration.createConfig());
            }
        }
        return instance;
    }

    @Override
    public void load() {
        loadObjects();
        loadSpells();
        loadDescriptors();
        loadParticles();
        loadSounds();
        loadMusic();
        loadSkins();
        loadFonts();
        loadMessages();
    }

    @Override
    public AssetManager getAssetManager() {
        return this;
    }

    private void loadFonts() {
        // TODO
    }

    private void loadMessages() {
        load(languagesFile, I18NBundle.class, new I18NBundleLoader.I18NBundleParameter(new Locale(languagesLocale[0], languagesLocale[1])));
    }

    private void loadSkins() {
        load(Resources.GAME_SKIN_FILE, AOSkin.class);
    }

    @Override
    public Pixmap getPixmap(String key) {
        return null;
    }

    @Override
    public Texture getTexture(int key) {
        String fileName = Resources.GAME_GRAPHICS_PATH + key + Resources.GAME_GRAPHICS_EXTENSION;
        if (!isLoaded(fileName)) {
            loadTexture(fileName);
            finishLoadingAsset(fileName);
        }
        return get(fileName);
    }

    @Override
    public AOImage getImage(int id) {
        if (images == null) {
            this.images = getImages();
        }
        return images.get(id);
    }

    @Override
    public AOAnimation getAnimation(int id) {
        if (animations == null) {
            this.animations = getAnimations();
        }
        return animations.get(id);
    }

    @Override
    public BitmapFont getFont(String key) {
        return null;
    }

    @Override
    public TextureAtlas getTextureAtlas(String key) {
        return null;
    }

    @Override
    public AOSkin getSkin() {
        return get(Resources.GAME_SKIN_FILE);
    }

    // @todo Revisar
    @Override
    public Music getMusic(int key) {
        String path = "data/music/" + key + ".mp3";
        Music music = null;
        try {
            music = getAssetManager().get(path);
        } catch (Exception e) {
            Log.error("DefaultAOAssetManager", "Error getting music: " + path, e);
        } finally {
            return music;
        }
    }

    // TODO: fix asap
    @Override
    public Sound getSound(int key) {
        String soundFile = Resources.GAME_SOUNDS_PATH + key + Resources.GAME_SOUNDS_EXTENSION;
        if (Gdx.files.internal(soundFile).exists()) {
            if (!isLoaded(soundFile)) {
                load(soundFile, Sound.class);
                finishLoading();
            }
            return get(soundFile);
        } else {
            return null;
        }
    }

    @Override
    public Sequencer getMidi(int key) {
        if (Gdx.files.internal(Resources.GAME_MIDI_PATH + key + Resources.GAME_MIDI_EXTENSION).exists()) {
            return get(Resources.GAME_MIDI_PATH + key + Resources.GAME_MIDI_EXTENSION);
        } else {
            return null;
        }

    }

    @Override
    public ParticleEffect getParticle(String particle) {
        return get(Resources.GAME_PARTICLES_PATH + particle);
    }

    @Override
    public Map<Integer, BodyDescriptor> getBodies() {
        if (bodies == null) {
            List<BodyDescriptor> list = get(GAME_DESCRIPTORS_PATH + BODIES + JSON_EXTENSION);
            bodies = list.stream().collect(Collectors.toMap(Descriptor::getId, o -> o));
        }
        return bodies;
    }

    @Override
    public Map<Integer, AOImage> getImages() {
        if (images == null) {
            List<AOImage> list = get(GAME_DESCRIPTORS_PATH + IMAGES + JSON_EXTENSION);
            this.images = list
                    .stream()
                    .collect(Collectors.toMap(AOImage::getId, image -> image));
        }
        return images;
    }

    @Override
    public Map<Integer, AOAnimation> getAnimations() {
        if (animations == null) {
            List<AOAnimation> aoAnimations = get(GAME_DESCRIPTORS_PATH + ANIMATIONS + JSON_EXTENSION);
            animations = aoAnimations.stream().collect(Collectors.toMap(AOAnimation::getId, o -> o));
        }
        return animations;
    }

    @Override
    public Map<Integer, Obj> getObjs() {
        Map<Integer, Obj> objs = new HashMap<>();
        Arrays.stream(Type.values()).forEach(type -> {
            String fileName = SharedResources.OBJECTS_FOLDER + type.name().toLowerCase() + JSON_EXTENSION;
            if (Gdx.files.internal(fileName).exists()) {
                objs.putAll(get(fileName));
            }
        });
        return objs;
    }

    @Override
    public Map<Integer, Spell> getSpells() {
        return get(SharedResources.SPELLS_JSON_FILE);
    }

    @Override
    public Map<Integer, ShieldDescriptor> getShields() {
        if (shields == null) {
            List<ShieldDescriptor> list = get(GAME_DESCRIPTORS_FOLDER + SHIELDS + JSON_EXTENSION);
            shields = list.stream().collect(Collectors.toMap(Descriptor::getId, o -> o));
        }
        return shields;
    }

    @Override
    public Map<Integer, FXDescriptor> getFXs() {
        if (fxs == null) {
            List<FXDescriptor> list = get(GAME_DESCRIPTORS_FOLDER + FXS + JSON_EXTENSION);
            fxs = list.stream().collect(Collectors.toMap(Descriptor::getId, o -> o));
        }
        return fxs;
    }

    @Override
    public Map<Integer, HeadDescriptor> getHeads() {
        if (heads == null) {
            List<HeadDescriptor> list = get(GAME_DESCRIPTORS_FOLDER + HEADS + JSON_EXTENSION);
            heads = list.stream().collect(Collectors.toMap(Descriptor::getId, o -> o));
        }
        return heads;
    }

    @Override
    public Map<Integer, HelmetDescriptor> getHelmets() {
        if (helmets == null) {
            List<HelmetDescriptor> list = get(GAME_DESCRIPTORS_FOLDER + HELMETS + JSON_EXTENSION);
            helmets = list.stream().collect(Collectors.toMap(Descriptor::getId, o -> o));
        }
        return helmets;
    }

    @Override
    public Map<Integer, WeaponDescriptor> getWeapons() {
        if (weapons == null) {
            List<WeaponDescriptor> list = get(GAME_DESCRIPTORS_FOLDER + WEAPONS + JSON_EXTENSION);
            weapons = list.stream().collect(Collectors.toMap(Descriptor::getId, o -> o));
        }
        return weapons;
    }

    @Override
    public String getMessages(Messages key, String... params) {
        if (key == null) {
            Gdx.app.error("Internationalization", "Error trying to get message");
            return "";
        }

        if (!isLoaded(languagesFile)) {
            load(languagesFile, I18NBundle.class);
            finishLoadingAsset(languagesFile);
        }

        I18NBundle i18 = get(languagesFile);
        if (i18 == null) {
            Gdx.app.error("Internationalization", "Error trying to get message: " + key.name());
            return "";
        }

        if (params.length > 0) {
            return i18.format(key.name(), (Object[]) params);
        } else {
            return i18.get(key.name());
        }
    }

    private void loadTexture(String fileName) {
        TextureParameter param = new TextureParameter();
        param.minFilter = TextureFilter.Linear;
        param.magFilter = TextureFilter.Linear;
        param.genMipMaps = true;
        param.wrapU = Texture.TextureWrap.Repeat;
        param.wrapV = Texture.TextureWrap.Repeat;
        load(fileName, Texture.class, param);
    }

    private void loadObjects() {
        Arrays.stream(Type.values()).forEach(type -> {
            ObjectParameter<HashMap<Integer, Obj>> param = new ObjectParameter<>(type);
            String fileName = SharedResources.OBJECTS_FOLDER + type.name().toLowerCase() + JSON_EXTENSION;
            if (Gdx.app.getFiles().internal(fileName).exists()) {
                load(fileName, OBJS_CLASS, param);
            }
        });
    }

    private void loadSpells() {
        load(SharedResources.SPELLS_JSON_FILE, SPELLS_CLASS);
    }

    private void loadDescriptors() {
        load(GAME_DESCRIPTORS_FOLDER + WEAPONS + JSON_EXTENSION, DESCRIPTORS_CLASS, descriptor(WeaponDescriptor.class));
        load(GAME_DESCRIPTORS_FOLDER + SHIELDS + JSON_EXTENSION, DESCRIPTORS_CLASS, descriptor(ShieldDescriptor.class));
        load(GAME_DESCRIPTORS_FOLDER + HEADS + JSON_EXTENSION, DESCRIPTORS_CLASS, descriptor(HeadDescriptor.class));
        load(GAME_DESCRIPTORS_FOLDER + HELMETS + JSON_EXTENSION, DESCRIPTORS_CLASS, descriptor(HelmetDescriptor.class));
        load(GAME_DESCRIPTORS_FOLDER + FXS + JSON_EXTENSION, DESCRIPTORS_CLASS, descriptor(FXDescriptor.class));
        load(GAME_DESCRIPTORS_FOLDER + BODIES + JSON_EXTENSION, DESCRIPTORS_CLASS, descriptor(BodyDescriptor.class));
        load(GAME_DESCRIPTORS_FOLDER + IMAGES + JSON_EXTENSION, IMAGE_CLASS);
        load(GAME_DESCRIPTORS_FOLDER + ANIMATIONS + JSON_EXTENSION, ANIMATION_CLASS);
    }

    private void loadParticles() {
        load(Resources.GAME_PARTICLES_PATH + "meditate1.party", ParticleEffect.class);
        ParticleEffectParameter params = new ParticleEffectParameter();
        params.imagesDir = Gdx.files.internal(Resources.GAME_PARTICLES_PATH + "images/");
        load(Resources.GAME_PARTICLES_PATH + "aura1.party", ParticleEffect.class, params);
        load(Resources.GAME_PARTICLES_PATH + "blue-meditation.p", ParticleEffect.class, params);
        load(Resources.GAME_PARTICLES_PATH + "blue-meditation3.p", ParticleEffect.class, params);
        load(Resources.GAME_PARTICLES_PATH + "level-up.p", ParticleEffect.class, params);
        load(Resources.GAME_PARTICLES_PATH + "magic-projectile.p", ParticleEffect.class, params);
        load(Resources.GAME_PARTICLES_PATH + "thunder.p", ParticleEffect.class, params);
    }

    private void loadMusic() {
        Reflections reflections = new Reflections("", new ResourcesScanner());
        Set<String> mp3Files = reflections.getResources(Pattern.compile(".*\\.mp3"));
        Set<String> midiFiles = reflections.getResources(Pattern.compile(".*\\.mid"));
        mp3Files.forEach(mp3 -> load(mp3, Music.class));
        midiFiles.forEach(midi -> load(midi, Sequencer.class));
    }

    private void loadSounds() {
        Reflections reflections = new Reflections("", new ResourcesScanner());
        Set<String> sounds = reflections.getResources(Pattern.compile(".*\\.ogg"));
        sounds.forEach(sound -> load(sound, Sound.class));
    }
}
