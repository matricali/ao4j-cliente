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
 * Almacena las animaciones de un cuerpo que puede ser vestido por un
 * {@link Personaje} en todas sus direcciones.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class AnimCuerpo extends AnimEquipable {

    /**
     * No todos los cuerpos tienen la misma altura, por esa razon es necesario
     * saber en que posicion vamos a dibujar la cabeza.
     */
    Posicion offsetCabeza;

    public AnimCuerpo() {
        super();
        this.offsetCabeza = new Posicion();
    }

    public AnimCuerpo(AnimCuerpo original) {
        super(original);
        this.offsetCabeza = original.offsetCabeza;
    }

    public AnimCuerpo(Sprite sprite, short a1, short a2, short a3, short a4, int offsetX, int offsetY, boolean iniciado) {
        super(sprite, a1, a2, a3, a4, iniciado);
        this.offsetCabeza = new Posicion(offsetX, offsetY);
    }
    
    public Posicion getOffsetCabeza() {
        return offsetCabeza;
    }
}
