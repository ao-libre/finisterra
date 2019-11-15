//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package game.screens.transitions;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class FadingGame extends Game {
    private final Array<TransitionListener> listeners;
    protected Batch batch;
    protected FrameBuffer currentScreenFBO;
    protected FrameBuffer nextScreenFBO;
    protected Screen nextScreen;
    private float transitionDuration;
    private float currentTransitionTime;
    private boolean transitionRunning;
    private ScreenTransition screenTransition;

    public FadingGame() {
        this.listeners = new Array<>();
    }

    public void create() {
        this.batch = new SpriteBatch();
        this.currentScreenFBO = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        this.nextScreenFBO = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }

    public void dispose() {
        if (this.screen != null) {
            this.screen.hide();
        }

        if (this.nextScreen != null) {
            this.nextScreen.hide();
        }

        this.currentScreenFBO.dispose();
        this.nextScreenFBO.dispose();
    }

    public void pause() {
        if (this.screen != null) {
            this.screen.pause();
        }

        if (this.nextScreen != null) {
            this.nextScreen.pause();
        }

    }

    public void resume() {
        if (this.screen != null) {
            this.screen.resume();
        }

        if (this.nextScreen != null) {
            this.nextScreen.resume();
        }

    }

    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if (this.nextScreen == null) {
            this.screen.render(delta);
        } else if (this.transitionRunning && this.currentTransitionTime >= this.transitionDuration) {
            this.screen.dispose();
            this.screen = this.nextScreen;
            this.screen.resume();
            this.transitionRunning = false;
            this.nextScreen = null;
            this.notifyFinished();
            this.screen.render(delta);
        } else if (this.screenTransition != null) {
            this.currentScreenFBO.begin();
            this.screen.render(delta);
            this.currentScreenFBO.end();
            this.nextScreenFBO.begin();
            this.nextScreen.render(delta);
            this.nextScreenFBO.end();
            float percent = this.currentTransitionTime / this.transitionDuration;
            this.screenTransition.render(this.batch, this.currentScreenFBO.getColorBufferTexture(), this.nextScreenFBO.getColorBufferTexture(), percent);
            this.currentTransitionTime += delta;
        }

    }

    @Override
    public void resize(int width, int height) {
        if (this.screen != null) {
            this.screen.resize(width, height);
        }

        if (this.nextScreen != null) {
            this.nextScreen.resize(width, height);
        }

        this.currentScreenFBO.dispose();
        this.nextScreenFBO.dispose();
        this.currentScreenFBO = new FrameBuffer(Format.RGBA8888, width, height, false);
        this.nextScreenFBO = new FrameBuffer(Format.RGBA8888, width, height, false);
    }

    protected boolean setTransition(ScreenTransition screenTransition, float duration) {
        if (this.transitionRunning) {
            return false;
        } else {
            this.screenTransition = screenTransition;
            this.transitionDuration = duration;
            return true;
        }
    }

    public Screen getScreen() {
        return this.screen;
    }

    @Override
    public void setScreen(Screen screen) {
        screen.show();
        if (this.transitionRunning) {
            Gdx.app.log(FadingGame.class.getSimpleName(), "Changed Screen while transition in progress");
        }

        if (this.screen == null) {
            this.screen = screen;
        } else if (this.screenTransition == null) {
            this.screen.hide();
            this.screen = screen;
        } else {
            this.nextScreen = screen;
            this.screen.pause();
            this.nextScreen.pause();
            this.currentTransitionTime = 0.0F;
            this.transitionRunning = true;
            this.notifyStarted();
        }

        this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public Screen getNextScreen() {
        return this.nextScreen;
    }

    public void addTransitionListener(TransitionListener listener) {
        this.listeners.add(listener);
    }

    public boolean removeTransitionListener(TransitionListener listener) {
        return this.listeners.removeValue(listener, true);
    }

    public void clearTransitionListeners() {
        this.listeners.clear();
    }

    private void notifyFinished() {
        Iterator var1 = this.listeners.iterator();

        while (var1.hasNext()) {
            TransitionListener transitionListener = (TransitionListener) var1.next();
            transitionListener.onTransitionFinished();
        }

    }

    private void notifyStarted() {
        Iterator var1 = this.listeners.iterator();

        while (var1.hasNext()) {
            TransitionListener transitionListener = (TransitionListener) var1.next();
            transitionListener.onTransitionStart();
        }

    }
}
