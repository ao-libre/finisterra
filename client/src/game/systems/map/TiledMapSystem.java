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
package game.systems.map;

import component.camera.Focused;
import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import game.systems.resources.MapSystem;
import component.position.WorldPos;
import shared.model.map.Map;
import shared.util.MapHelper;

import static com.artemis.E.E;

/**
 * TiledMapSystem CharHero
 *
 * @author rt
 * @package com.mob.client.api.systems.map
 */
@Wire
public class TiledMapSystem extends IteratingSystem {

    public Map map;
    public int mapNumber = -1;

    public TiledMapSystem() {
        super(Aspect.all(Focused.class, WorldPos.class));
    }

    /**
     * Change the currently loaded map
     *
     * @param number
     */
    private void changeMap(int number) {
        this.mapNumber = number;
        this.map = MapSystem.get(number);

        new Thread(() -> {
            Map left = MapSystem.get(map.getNeighbour(MapHelper.Dir.LEFT));
            if (left != null) {
                MapSystem.get(left.getNeighbour(MapHelper.Dir.UP));
                MapSystem.get(left.getNeighbour(MapHelper.Dir.DOWN));
            }
            Map right = MapSystem.get(map.getNeighbour(MapHelper.Dir.RIGHT));
            if (right != null) {
                MapSystem.get(right.getNeighbour(MapHelper.Dir.UP));
                MapSystem.get(right.getNeighbour(MapHelper.Dir.DOWN));
            }
            MapSystem.get(map.getNeighbour(MapHelper.Dir.UP));
            MapSystem.get(map.getNeighbour(MapHelper.Dir.DOWN));

        });
    }

    @Override
    protected void process(int entityId) {
        int playerMap = E(entityId).getWorldPos().map;
        if (playerMap != mapNumber) {
            changeMap(playerMap);
        }
    }
}
