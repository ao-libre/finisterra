/*******************************************************************************
 * Copyright (C) 2014  Rodrigo Troncoso
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
package com.argentum.loaders;


import com.argentum.Util;
import shared.model.map.Map;
import shared.model.map.Tile;
import shared.model.map.WorldPosition;

import java.io.DataInputStream;
import java.io.IOException;

import static com.argentum.Constants.*;

public class MapLoader {

    public Map load(DataInputStream file, DataInputStream inf) throws IOException {
        file.skipBytes(GAME_FILE_HEADER_SIZE + (2 * 5)); // Skip complete map header

        inf.readFloat();
        inf.readFloat();
        inf.readShort();

        Map map = new Map();

        // Read map info (rows first, then columns)
        for (int y = Map.MIN_MAP_SIZE_WIDTH; y <= Map.MAX_MAP_SIZE_WIDTH; y++) {
            for (int x = Map.MIN_MAP_SIZE_HEIGHT; x <= Map.MAX_MAP_SIZE_HEIGHT; x++) {
                int charIndex = 0, objCount = 0, objIndex = 0, npcIndex = 0, trigger = 0, graphic[] = new int[4];
                WorldPosition tileExit = null;
                boolean blocked;
                byte byFlags;

                byFlags = file.readByte();
                blocked = (1 == (byFlags & 1));

                graphic[0] = Util.leShort(file.readShort());

                if ((byFlags & 2) == 2) {
                    graphic[1] = Util.leShort(file.readShort());
                } else {
                    graphic[1] = 0;
                }

                if ((byFlags & 4) == 4) {
                    graphic[2] = Util.leShort(file.readShort());
                } else {
                    graphic[2] = 0;
                }

                if ((byFlags & 8) == 8) {
                    graphic[3] = Util.leShort(file.readShort());
                } else {
                    graphic[3] = 0;
                }

                if ((byFlags & 16) == 16) {
                    trigger = Util.leShort(file.readShort());
                }

                byFlags = inf.readByte();
                if ((1 == (byFlags & 1))) {
                    tileExit = new WorldPosition(Util.leShort(inf.readShort()), Util.leShort(inf.readShort()), Util.leShort(inf.readShort()));
                }
                if ((byFlags & 2) == 2) {
                    npcIndex = Util.leShort(inf.readShort());
                }
                if ((byFlags & 4) == 4) {
                    objIndex = Util.leShort(inf.readShort());
                    objCount = Util.leShort(inf.readShort());
                }

                Tile tile = new Tile(graphic, charIndex, objCount, objIndex, npcIndex, tileExit, blocked, trigger);
                map.setTile(x, y, tile);
            }
        }

        return map;
    }
}
