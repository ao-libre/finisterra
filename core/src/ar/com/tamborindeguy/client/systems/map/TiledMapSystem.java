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
package ar.com.tamborindeguy.client.systems.map;

import ar.com.tamborindeguy.client.handlers.MapHandler;
import ar.com.tamborindeguy.model.map.Map;
import camera.Focused;
import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import position.WorldPos;

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
    public long mapNumber = -1;

    public TiledMapSystem() {
        super(Aspect.all(Focused.class, WorldPos.class));
    }

    /**
     * Change the currently loaded map
     *
     * @param number
     */
    public void changeMap(int number) {
        this.mapNumber = number;
        this.map = MapHandler.get(number);

//        new Thread(() -> {
//            List<Integer> lindants = map.getLindants();
//            lindants.forEach(mapNumber -> MapHandler.getObject(mapNumber));
//        });
    }

    @Override
    protected void process(int entityId) {
        int playerMap = E(entityId).getWorldPos().map;
        if (playerMap != mapNumber) {
            changeMap(playerMap);
        }
    }
}
