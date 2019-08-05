package design.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import design.designers.ImageDesigner;
import design.screens.ScreenEnum;
import design.screens.map.model.TileSet;
import design.screens.views.ImageView;
import design.screens.views.View;
import game.utils.Resources;
import model.textures.AOImage;
import shared.model.map.Tile;

import java.util.Optional;

public class TileSetCreator {
    private FileHandle fileHandle;

    private TileSetCreator(FileHandle fileHandle) {
        this.fileHandle = fileHandle;
    }

    public static TileSet create(FileHandle fileHandle, int id) {
        TileSetCreator tileSetCreator = new TileSetCreator(fileHandle);
        ImageView screen = (ImageView) ScreenEnum.IMAGE_VIEW.getScreen();
        ImageDesigner designer = screen.getDesigner();
        int fileId = designer.getFreeId();
        FileHandle dest = Gdx.files.local(Resources.GAME_GRAPHICS_PATH + fileId + ".png");
        fileHandle.copyTo(dest);
        return tileSetCreator.doSplit(fileId, id, designer);
    }

    private TileSet doSplit(int fileId, int id, ImageDesigner designer) {

        Pixmap image = new Pixmap(fileHandle);
        int width = image.getWidth();
        int height = image.getHeight();
        int tilePixelWidth = (int) Tile.TILE_PIXEL_WIDTH;
        int tilePixelHeight = (int) Tile.TILE_PIXEL_HEIGHT;
        int cols = width / tilePixelWidth;
        int rows = height / tilePixelHeight;

        TileSet tileSet = new TileSet(id, cols, rows);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                AOImage newImage = new AOImage();
                newImage.setX((j * tilePixelWidth));
                newImage.setY((i * tilePixelHeight));
                newImage.setWidth(tilePixelWidth);
                newImage.setHeight(tilePixelHeight);
                newImage.setFileNum(fileId);
                newImage.setId(designer.getFreeId());
                designer.add(newImage);
                tileSet.setImage(j, i, newImage.getId());
            }
        }

        View screen = ScreenEnum.IMAGE_VIEW.getScreen();
        screen.loadItems(Optional.empty());

        return tileSet;
    }

}
