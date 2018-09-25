package ar.com.tamborindeguy.client.utils;

import ar.com.tamborindeguy.client.handlers.MapHandler;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.model.map.Map;
import ar.com.tamborindeguy.model.map.Tile;
import ar.com.tamborindeguy.model.map.WorldPosition;
import com.artemis.E;
import position.WorldPos;

public class MapUtils {

    public static boolean changeMap(E player, WorldPos pos) {
        Map map = MapHandler.get(pos.map);
        Tile tile = map.getTile(pos.x, pos.y);
        WorldPosition newPos = tile.getTileExit();
        WorldPos playerPos = player.getWorldPos();
        if (newPos.getMap() != 0 && newPos.getMap() != playerPos.map) {
            playerPos.map = newPos.getMap();
            playerPos.x = newPos.getX();
            playerPos.y = newPos.getY();
            updateTile(GameScreen.getPlayer(), playerPos);
            return true;
        }
        return false;
    }

    public static void updateTile(int entity, WorldPos pos) {
        Map map = MapHandler.get(pos.map);
        Tile tile = map.getTile(pos.x, pos.y);
        tile.setCharIndex(entity);
    }

    public static boolean isValidPos(Map map, WorldPos expectedPos) {
        Tile tile = map.getTile(expectedPos.x, expectedPos.y);
        return tile != null && !tile.isBlocked() && tile.getCharIndex() == Tile.EMPTY_INDEX;
    }

    public static boolean isValid(WorldPos expectedPos) {
        Map map = MapHandler.get(expectedPos.map);
        Tile tile = map.getTile(expectedPos.x, expectedPos.y);
        return tile!= null && !tile.isBlocked() && tile.getCharIndex() == Tile.EMPTY_INDEX;
    }
}
