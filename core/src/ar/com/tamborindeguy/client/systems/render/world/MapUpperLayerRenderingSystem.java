/*******************************************************************************
 * Copyright (C) 2015  Rodrigo Troncoso
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.game.MapManager;
import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import ar.com.tamborindeguy.client.systems.map.TiledMapSystem;
import ar.com.tamborindeguy.model.map.Map;
import ar.com.tamborindeguy.model.map.Tile;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

@Wire
public class MapUpperLayerRenderingSystem extends BaseSystem {

    private TiledMapSystem mapSystem;
    private CameraSystem cameraSystem;
    public SpriteBatch batch;

    public MapUpperLayerRenderingSystem(SpriteBatch spriteBatch) {
        this.batch = spriteBatch;
    }

    private void renderWorld() {
        // Variable Declarations
        Map map = this.mapSystem.map;
        if (map == null) return;
        int screenMinX, screenMaxX, screenMinY, screenMaxY, minAreaX, minAreaY, maxAreaX, maxAreaY;

        // Calculate visible part of the map
        int cameraPosX = (int) (this.cameraSystem.camera.position.x / Tile.TILE_PIXEL_WIDTH);
        int cameraPosY = (int) (this.cameraSystem.camera.position.y / Tile.TILE_PIXEL_HEIGHT);
        int halfWindowTileWidth = (int) ((this.cameraSystem.camera.viewportWidth / Tile.TILE_PIXEL_WIDTH) / 2f);
        int halfWindowTileHeight = (int) ((this.cameraSystem.camera.viewportHeight / Tile.TILE_PIXEL_HEIGHT) / 2f);

        screenMinX = cameraPosX - halfWindowTileWidth - 1;
        screenMaxX = cameraPosX + halfWindowTileWidth + 1;
        screenMinY = cameraPosY - halfWindowTileHeight - 1;
        screenMaxY = cameraPosY + halfWindowTileHeight + 1;

        minAreaX = screenMinX - Map.TILE_BUFFER_SIZE;
        maxAreaX = screenMaxX + Map.TILE_BUFFER_SIZE;
        minAreaY = screenMinY - Map.TILE_BUFFER_SIZE;
        maxAreaY = screenMaxY + Map.TILE_BUFFER_SIZE;

        // Make sure it is between map bounds
        if(minAreaX < Map.MIN_MAP_SIZE_WIDTH) minAreaX = Map.MIN_MAP_SIZE_WIDTH;
        if(maxAreaX > Map.MAX_MAP_SIZE_WIDTH) maxAreaX = Map.MAX_MAP_SIZE_WIDTH;
        if(minAreaY < Map.MIN_MAP_SIZE_HEIGHT) minAreaY = Map.MIN_MAP_SIZE_HEIGHT;
        if(maxAreaY > Map.MAX_MAP_SIZE_HEIGHT) maxAreaY = Map.MAX_MAP_SIZE_HEIGHT;

        // LAYER 3 - ANIMATED (POSSIBLY?)
        MapManager.renderLayer(map, this.batch, world.getDelta(), 2, minAreaX, maxAreaX, minAreaY, maxAreaY);

        // LAYER 4 - ANIMATED (POSSIBLY?)
        MapManager.renderLayer(map, this.batch, world.getDelta(), 3, minAreaX, maxAreaX, minAreaY, maxAreaY);
    }

    @Override
    protected void processSystem() {
        this.cameraSystem.camera.update();
        this.batch.setProjectionMatrix(this.cameraSystem.camera.combined);
        this.batch.begin();

        this.renderWorld();

        this.batch.end();
    }


}
