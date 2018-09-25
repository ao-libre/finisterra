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
/**
 * Handles all active textures loaded from the graphics path.
 * @author Rodrigo Troncoso
 * @version 0.1
 * @since 2014-04-10
 */
package ar.com.tamborindeguy.client.handlers;

import ar.com.tamborindeguy.client.game.AO;
import ar.com.tamborindeguy.interfaces.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

import java.util.HashMap;

public class SurfaceHandler implements Constants {

	private static HashMap<String, Texture> surfaces = new HashMap<String, Texture>();
	private static String graphicsPath = AO.GAME_GRAPHICS_PATH;

	/**
	 * Loads into memory ALL the textures in the graphics path
	 */
	public static void loadAllTextures() {
		FileHandle file = Gdx.app.getFiles().internal(graphicsPath);
		if(file.isDirectory()) {
			for(FileHandle tmp : file.list()) {
				if(tmp.extension() == AO.GAME_GRAPHICS_EXTENSION) {
					Gdx.app.debug(SurfaceHandler.class.getSimpleName(), "Cargando " + tmp.name());
					SurfaceHandler.loadTexture(tmp.nameWithoutExtension());
				}
			}
		}
	}
	
	/**
	 * @param fileName Name of the file found in the graphics folder
	 */
	public static void loadTexture(String fileName) {
		Texture texture = new Texture(graphicsPath + fileName + AO.GAME_GRAPHICS_EXTENSION);
		texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		SurfaceHandler.add(fileName, texture);
	}

	/**
	 * @param key Index in map
	 * @param texture Texture to add
	 */
	 public static void add(final String key, final Texture texture) {
		 if (SurfaceHandler.surfaces.containsKey(key)) {
			 return;
		 }
	   
		 SurfaceHandler.surfaces.put(key, texture);
	 }

	/**
	 * Gets a Texture from the map
	 * @param key Index in map
	 * @return BundledTexture inside the map
	 */
	 public static Texture get(final String key) {
		 if(!SurfaceHandler.surfaces.containsKey(key)) SurfaceHandler.loadTexture(key);
		 return SurfaceHandler.surfaces.get(key);
	 }
	  
	 /**
	  * Unloads a Texture from the map and memory
	  * @param key Index in map
	  */
	public static void dispose(final String key) {
		if (!SurfaceHandler.surfaces.containsKey(key)) {
			return;
		}
	   
		final Texture t = SurfaceHandler.surfaces.get(key);
		t.dispose();
		SurfaceHandler.surfaces.remove(key);
	}
	  
	public static void disposeAll() {
		for (final Texture t : SurfaceHandler.surfaces.values()) {
			t.dispose();
		}
   
		SurfaceHandler.surfaces.clear();
	 }

}
