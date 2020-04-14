package game.systems.screen;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.esotericsoftware.minlog.Log;
import game.handlers.DefaultAOAssetManager;
import game.systems.ui.console.ConsoleSystem;
import game.utils.CursorSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import shared.util.Messages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Wire
public class ScreenSystem extends PassiveSystem {
    @Wire
    private DefaultAOAssetManager assetManager;
    private CursorSystem cursorSystem;
    private ConsoleSystem consoleSystem;

    private int windowedWidth;
    private int windowedHeight;

    // Take a screenshot of the render.
    public void takeScreenshot() {
        try {
            // Set where we gonna save the screenshot
            String screenshotPath = "Screenshots/Screenshot-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM_HH-mm-ss")) + ".png";

            // Perform the appropiate I/O opperations.
            byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
            // this loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
            for (int i = 4; i < pixels.length; i += 4) {
                pixels[i - 1] = (byte) 255;
            }
            Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
            BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
            PixmapIO.writePNG(Gdx.files.local(screenshotPath), pixmap);

            // Render the message in the game component.console.
            consoleSystem.getConsole().addInfo(assetManager.getMessages(Messages.SCREENSHOT, screenshotPath));

            // Clear/dispose the pixmap object.
            pixmap.dispose();

        } catch (Exception ex) {
            Log.error("Screenshot I/O", "Error trying to take a screenshot...", ex);
        }

    }

    // Toggle between Windowed Mode and Fullscreen.
    public void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            setWindowed();
        } else {
            this.windowedWidth = (int) getWidth();
            this.windowedHeight = (int) getHeight();
            setFullScreen();
        }
    }

    private void setFullScreen() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        resolutionChanged();
    }

    private void setWindowed() {
        Gdx.graphics.setWindowedMode(this.windowedWidth, this.windowedHeight);
        resolutionChanged();
    }

    private float getHeight() {
        return Gdx.graphics.getHeight();
    }

    private float getWidth() {
        return Gdx.graphics.getWidth();
    }

    public void changeResolution(Graphics.DisplayMode displayMode) {
        this.windowedWidth = displayMode.width;
        this.windowedHeight = displayMode.height;
        setWindowed();
    }

    public void resolutionChanged() {
        // notify other systems
        cursorSystem.reload();
    }

}
