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
 * Representa la orientacion hacia la que mira un personaje
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public enum Orientacion {
    NORTE(1), ESTE(2), SUR(3), OESTE(4);
    private final int valor;

    private Orientacion(int valor) {
        this.valor = valor;
    }

    public int valor() {
        return valor;
    }

    /**
     * Obtener orientacion desde un valor entero dado
     *
     * @param valor
     * @return
     */
    public static Orientacion valueOf(int valor) {
        for (Orientacion o : Orientacion.values()) {
            if (o.valor == valor) {
                return o;
            }
        }
        throw new IllegalArgumentException("Orientacion invalida (" + valor + ")");
    }
}
