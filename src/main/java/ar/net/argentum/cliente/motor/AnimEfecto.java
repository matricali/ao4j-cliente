/*
 * Copyright (C) 2019 Jorge Matricali <jorgematricali@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ar.net.argentum.cliente.motor;

/**
 * Almacena las animaciones de los efectos
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class AnimEfecto {

    protected Animacion animacion;
    protected short grhIndex;
    protected int offsetX;
    protected int offsetY;

    public AnimEfecto(short grhIndex, short offsetX, short offsetY) {
        this.animacion = new Animacion(grhIndex, true);
        this.grhIndex = grhIndex;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public AnimEfecto(AnimEfecto original) {
        this.animacion = new Animacion(original.getAnimacion());
        this.grhIndex = original.getGrhIndex();
        this.offsetX = original.getOffsetX();
        this.offsetY = original.getOffsetY();
    }

    /**
     * @return the animacion
     */
    public Animacion getAnimacion() {
        return animacion;
    }

    /**
     * @return the grhIndex
     */
    public short getGrhIndex() {
        return grhIndex;
    }

    /**
     * @return the offsetX
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * @return the offsetY
     */
    public int getOffsetY() {
        return offsetY;
    }
}
