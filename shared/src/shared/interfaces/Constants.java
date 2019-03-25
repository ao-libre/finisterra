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
package shared.interfaces;

import com.badlogic.gdx.graphics.Color;

public interface Constants {

    float PI2 = 3.1415926535897932384626433832795f * 2.0f;

    int GAME_FILE_HEADER_SIZE = 263;

    float OFFSET_HEAD = 12.0f;

    Color COLOR_DAYLIGHT = new Color(0.5f, 0.5f, 0.5f, 0.2f);
    Color COLOR_DAWN = new Color(0.35f, 0.3f, 0.3f, 0.2f);
    Color COLOR_NIGHT = new Color(0.2f, 0.2f, 0.2f, 1.0f);

    float ALPHA_TREES = 1.0f;
    float ALPHA_LIGHTS = 0.4f;
    float ALPHA_FXS = 0.6f;

    int DEFAULT_NUM_RAYS = 128;

    // Meditation FXs
    int MEDITATE_NW_FX = 1;

    int AURA_FX = 2;

    enum Heading {
        NORTH(0), EAST(1), SOUTH(2), WEST(3);

        final int mHeading;

        Heading(int pHeading) {
            this.mHeading = pHeading;
        }

        public int toInt() {
            return this.mHeading;
        }
    }

}
