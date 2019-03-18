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
package ar.net.argentum.cliente.motor.gamedata;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Sprite {

    public short sX;
    public short sY;

    public int fileNum;

    public short pixelWidth;
    public short pixelHeight;

    public float tileWidth;
    public float tileHeight;

    public short numFrames;
    public int frames[];

    public float speed;
    
    public boolean esValido() {
        return fileNum != 0;
    }  
}
