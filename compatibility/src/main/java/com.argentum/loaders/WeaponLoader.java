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
import com.badlogic.gdx.utils.LongMap;

import model.descriptors.WeaponDescriptor;
import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class WeaponLoader extends Loader<LongMap<WeaponDescriptor>> {

	@Override
	public LongMap<WeaponDescriptor> load(DataInputStream file) {
		LongMap<WeaponDescriptor> weapons = new LongMap<WeaponDescriptor>();
		Ini iniFile = new Ini();
		Config c = new Config();
		c.setLowerCaseSection(true);
		iniFile.setConfig(c);
		
		try {
			iniFile.load(file);
			
			int numArmas = Integer.parseInt(iniFile.get("init", "NumArmas"));

			for(int i = 1; i <= numArmas; i++) {
				int[] weaponIndex = new int[4];
				
				if(iniFile.get("arma" + String.valueOf(i), "Dir1") == null) { 
					weaponIndex[Heading.NORTH.toInt()] = 0;
					weaponIndex[Heading.EAST.toInt()] = 0; 
					weaponIndex[Heading.SOUTH.toInt()] = 0; 
					weaponIndex[Heading.WEST.toInt()] = 0; 
					weapons.put(i, new WeaponDescriptor(weaponIndex));
					continue; 
				}
				
				weaponIndex[Heading.NORTH.toInt()] = Integer.parseInt(iniFile.get("arma" + String.valueOf(i), "Dir1"));
				weaponIndex[Heading.EAST.toInt()] = Integer.parseInt(iniFile.get("arma" + String.valueOf(i), "Dir2"));
				weaponIndex[Heading.SOUTH.toInt()] = Integer.parseInt(iniFile.get("arma" + String.valueOf(i), "Dir3"));
				weaponIndex[Heading.WEST.toInt()] = Integer.parseInt(iniFile.get("arma" + String.valueOf(i), "Dir4"));
				
				weapons.put(i, new WeaponDescriptor(weaponIndex));
			}

			return weapons;
		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


}
