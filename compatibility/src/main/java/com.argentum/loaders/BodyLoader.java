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
import model.descriptors.BodyDescriptor;

import static com.argentum.Constants.GAME_FILE_HEADER_SIZE;

public class BodyLoader extends Loader<LongMap<BodyDescriptor>> {

	@Override
	public LongMap<BodyDescriptor> load(DataInputStream file) throws IOException {
		LongMap<BodyDescriptor> bodys = new LongMap<>();
		int numBodys;

		file.skipBytes(GAME_FILE_HEADER_SIZE);
		numBodys = Util.leShort(file.readShort());

		for(int i = 1; i <= numBodys; i++) {
			int grhArray[] = new int[4], headOffSetX, headOffSetY;

			grhArray[Heading.NORTH.toInt()] = Util.leShort(file.readShort());
			grhArray[Heading.EAST.toInt()] = Util.leShort(file.readShort());
			grhArray[Heading.SOUTH.toInt()] = Util.leShort(file.readShort());
			grhArray[Heading.WEST.toInt()] = Util.leShort(file.readShort());

			headOffSetX = Util.leShort(file.readShort());
			headOffSetY = Util.leShort(file.readShort());

			bodys.put(i, new BodyDescriptor(grhArray, headOffSetX, headOffSetY));
		}

		return bodys;
	}

}
