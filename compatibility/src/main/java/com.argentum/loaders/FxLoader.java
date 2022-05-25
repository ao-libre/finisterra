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

import java.io.DataInputStream;
import java.io.IOException;

import static com.argentum.Constants.*;

import com.argentum.Util;
import com.badlogic.gdx.utils.LongMap;
import model.descriptors.FXDescriptor;

public class FxLoader extends Loader<LongMap<FXDescriptor>> {

	@Override
	public LongMap<FXDescriptor> load(DataInputStream file) throws IOException {
		LongMap<FXDescriptor> fxs = new LongMap<>();
		int numFxs;

		file.skipBytes(GAME_FILE_HEADER_SIZE);
        numFxs = Util.leShort(file.readShort());

		for(int i = 1; i <= numFxs; i++) {
			int offsetX, offsetY, fxIndex;

			fxIndex = Util.leShort(file.readShort());
			offsetX = Util.leShort(file.readShort());
			offsetY = Util.leShort(file.readShort());

			fxs.put(i, new FXDescriptor(fxIndex, offsetX, offsetY));
		}

		return fxs;
	}

}
