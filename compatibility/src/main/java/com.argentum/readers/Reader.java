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

import com.argentum.loaders.Loader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.DataInputStream;
import java.io.IOException;

public class Reader<T> {

    public T read(String path, Loader<T> loader) {

        try {
            Gdx.app.log(getClass().getSimpleName(), path);
            FileHandle fileHandle = Gdx.files.internal(path);
            T loadedFile = loader.load(new DataInputStream(fileHandle.read()));

            Gdx.app.log(this.getClass().getSimpleName(), "[Reader] Asset " + path + " successfully loaded");
            return loadedFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
