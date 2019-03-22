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
package ar.net.argentum.cliente.mundo;

/**
 * Almacena una posicion en un plano de 2 dimensiones.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Posicion {

    private int x;
    private int y;

    /**
     * Crea una nueva posicion en un plano 2D
     */
    public Posicion() {
        this(0, 0);
    }

    /**
     * Crea una nueva posicion en un plano 2D
     *
     * @param x Posicion en el eje X
     * @param y Posiciion en el eje Y
     */
    public Posicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Obtiene la posicion en el eje X
     *
     * @return int Posicion X
     */
    public int x() {
        return x;
    }

    /**
     * Obtiene la posiciooin en el eje Y
     *
     * @return int Posicion Y
     */
    public int y() {
        return y;
    }

    /**
     * Establece una nueva posicion en el eje X
     *
     * @param x nueva posicion
     * @return
     */
    public Posicion x(int x) {
        this.x = x;
        return this;
    }

    /**
     * Establece una nueva posiciioion en el eje Y
     *
     * @param y nueva posicion
     * @return
     */
    public Posicion y(int y) {
        this.y = y;
        return this;
    }
}
