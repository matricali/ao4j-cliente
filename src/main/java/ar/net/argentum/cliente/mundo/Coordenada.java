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

import ar.net.argentum.cliente.mundo.Posicion;

/**
 * Representa una posicion especifica en el mundo de Argentum Online.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Coordenada {

    private final short id_mapa;
    private final Posicion posicion;

    public Coordenada(short id_mapa, Posicion posicion) {
        this.id_mapa = id_mapa;
        this.posicion = posicion;
    }

    public Coordenada(short id_mapa, int x, int y) {
        this.id_mapa = id_mapa;
        this.posicion = new Posicion(x, y);
    }
}
