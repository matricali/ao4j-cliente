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
 * Representa un grafico indexado de Argentum Online
 *
 * @see TileEngine.bas.GrhData
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Sprite {

    /**
     * Coordenada X de origen
     */
    public short sX;
    /**
     * Coordenada Y de origen
     */
    public short sY;
    /**
     * Numero de archivo (recursos/graficos/{numero}.png)
     */
    public int fileNum;
    /**
     * Ancho en pixeles
     */
    public short pixelWidth;
    /**
     * Alto en pixeles
     */
    public short pixelHeight;
    /**
     * Ancho en baldosas
     */
    public float tileWidth;
    /**
     * Alto en baldosas
     */
    public float tileHeight;
    /**
     * Numero de cuadros
     */
    public short numFrames;
    /**
     * Cuadros
     */
    public int frames[];
    /**
     * Velocidad
     */
    public float speed;

    /**
     * @return Devuelve true si el grafico es valido.
     */
    public boolean esValido() {
        return fileNum != 0;
    }
}
