package game.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import game.systems.resources.SoundsSystem;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import java.io.IOException;

public class MidiLoader extends AsynchronousAssetLoader<Sequencer, MidiLoader.MidiParameter<Sequencer>> {

    private Sequencer result;

    public MidiLoader() {
        super(new InternalFileHandleResolver());
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, MidiParameter parameter) {
        result = null;
        result = loadSequencer(file);
    }

    @Override
    public Sequencer loadSync(AssetManager manager, String fileName, FileHandle file, MidiParameter parameter) {
        Sequencer synchronizedResult = this.result;
        this.result = null;
        return synchronizedResult;
    }

    private Sequencer loadSequencer(FileHandle file) {
        Sequencer sequencer;
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(file.read());
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        } catch (MidiUnavailableException e) {
            Gdx.app.debug(SoundsSystem.class.getSimpleName(), "Error on loadMidi(FileHandle file): Midi is not available.", e);
            return null;
        } catch (InvalidMidiDataException e) {
            Gdx.app.debug(SoundsSystem.class.getSimpleName(), "Error on loadMidi(FileHandle file): Midi Data was invalid.", e);
            return null;
        } catch (IOException e) {
            Gdx.app.debug(SoundsSystem.class.getSimpleName(), "Error on loadMidi(FileHandle file): IO error.", e);
            return null;
        }
        return sequencer;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, MidiParameter parameter) {
        return null;
    }

    static class MidiParameter<T> extends AssetLoaderParameters<T> {
    }
}
