/**
 * Copyright (C) 2014  Rodrigo Troncoso
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Stores information about a helmet
 *
 * @author Rodrigo Troncoso
 * @version 0.1
 * @since 2014-04-10
 */
/**
 * Stores information about a helmet
 * @author Rodrigo Troncoso
 * @version 0.1
 * @since 2014-04-10
 */
package ar.com.tamborindeguy.model.descriptors;

public class FXDescriptor extends Descriptor {

    private int offsetX;
    private int offsetY;

    public FXDescriptor() {}
    public FXDescriptor(int fxIndex, int offsetX, int offsetY) {
        super(new int[] {fxIndex});
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getOffsetX() {
        return offsetX;
    }
}
