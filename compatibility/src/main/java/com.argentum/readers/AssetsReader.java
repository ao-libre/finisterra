/**
 * ****************************************************************************
 * Copyright (C) 2014  Rodrigo Troncoso
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * *****************************************************************************
 */
package com.argentum.readers;

import com.argentum.loaders.GraphicLoader;
import com.badlogic.gdx.utils.LongMap;
import model.descriptors.*;
import shared.model.map.Map;


public interface AssetsReader {

    Map loadMap(String map);

    LongMap<GraphicLoader.Graphic> loadGraphics();

    LongMap<BodyDescriptor> loadBodies();

    LongMap<FXDescriptor> loadFxs();

    LongMap<HeadDescriptor> loadHeads();

    LongMap<HelmetDescriptor> loadHelmets();

    LongMap<ShieldDescriptor> loadShields();

    LongMap<WeaponDescriptor> loadWeapons();
}
