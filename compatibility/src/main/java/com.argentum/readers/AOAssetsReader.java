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

import com.argentum.Game;
import com.argentum.loaders.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.LongMap;
import com.esotericsoftware.minlog.Log;
import model.descriptors.*;
import shared.model.map.Map;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Class AOAssetsReader
 * <p>
 * This class handles all the logic for parsing
 * Argentum Online's data objects.
 */
public class AOAssetsReader implements AssetsReader {

    @Override
    public Map loadMap(String map) {
        FileHandle mapPath = Gdx.files.internal(Game.GAME_MAPS_PATH + "Mapa" + map + map + ".map");
        FileHandle infPath = Gdx.files.internal(Game.GAME_MAPS_PATH + "Mapa" + map + map + ".inf");
        MapLoader loader = new MapLoader();
        try (DataInputStream mapStream = new DataInputStream(mapPath.read());
             DataInputStream inf = new DataInputStream(infPath.read())) {
            return loader.load(mapStream, inf);
        } catch (IOException | GdxRuntimeException e) {
            Log.error("Map I/O", "Failed to read map " + map, e);
            return new Map();
        }
    }

    @Override
    public LongMap<GraphicLoader.Graphic> loadGraphics() {
        Reader<LongMap<GraphicLoader.Graphic>> reader = new Reader<>();
        GraphicLoader loader = new GraphicLoader();

        return reader.read(Game.GAME_INIT_PATH + "Graficos.ind", loader);
    }

    @Override
    public LongMap<BodyDescriptor> loadBodies() {
        Reader<LongMap<BodyDescriptor>> reader = new Reader<>();
        BodyLoader loader = new BodyLoader();

        return reader.read(Game.GAME_INIT_PATH + "Personajes.ind", loader);
    }

    @Override
    public LongMap<FXDescriptor> loadFxs() {
        Reader<LongMap<FXDescriptor>> reader = new Reader<>();
        FxLoader loader = new FxLoader();

        return reader.read(Game.GAME_INIT_PATH + "Fxs.ind", loader);
    }

    @Override
    public LongMap<HeadDescriptor> loadHeads() {
        Reader<LongMap<HeadDescriptor>> reader = new Reader<>();
        HeadLoader loader = new HeadLoader();

        return reader.read(Game.GAME_INIT_PATH + "Cabezas.ind", loader);
    }

    @Override
    public LongMap<HelmetDescriptor> loadHelmets() {
        Reader<LongMap<HelmetDescriptor>> reader = new Reader<>();
        HelmetLoader loader = new HelmetLoader();

        return reader.read(Game.GAME_INIT_PATH + "Cascos.ind", loader);
    }

    @Override
    public LongMap<ShieldDescriptor> loadShields() {
        Reader<LongMap<ShieldDescriptor>> reader = new Reader<>();
        ShieldLoader loader = new ShieldLoader();

        return reader.read(Game.GAME_INIT_PATH + "Escudos.dat", loader);
    }

    @Override
    public LongMap<WeaponDescriptor> loadWeapons() {
        Reader<LongMap<WeaponDescriptor>> reader = new Reader<>();
        WeaponLoader loader = new WeaponLoader();

        return reader.read(Game.GAME_INIT_PATH + "Armas.dat", loader);
    }

}
