package game.handlers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import model.descriptors.*;
import model.textures.AOAnimation;
import model.textures.AOImage;
import shared.model.Graphic;
import shared.model.Spell;
import shared.objects.types.Obj;

import javax.sound.midi.Sequencer;
import java.util.List;
import java.util.Map;

public interface AOAssetManager {

    void load();

    void dispose();

    AssetManager getAssetManager();

    Pixmap getPixmap(String key);

    Texture getTexture(int key);

    AOImage getImage(int id);

    AOAnimation getAnimation(int id);

    BitmapFont getFont(String key);

    TextureAtlas getTextureAtlas(String key);

    Skin getSkin();

    Music getMusic(int key);

    Sound getSound(int key);

    Sequencer getMidi(int key);

    ParticleEffect getParticle(String index);

    Map<Integer, BodyDescriptor> getBodies();

    Map<Integer, AOImage> getImages();

    List<AOAnimation> getAnimations();

    Map<Integer, Obj> getObjs();

    Map<Integer, Spell> getSpells();

    List<ShieldDescriptor> getShields();

    List<FXDescriptor> getFXs();

    List<HeadDescriptor> getHeads();

    List<HelmetDescriptor> getHelmets();

    List<WeaponDescriptor> getWeapons();

}
