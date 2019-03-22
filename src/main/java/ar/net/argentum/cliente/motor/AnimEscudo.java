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
 * Almacena las animaciones de un escudo en todas sus direcciones.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class AnimEscudo extends AnimEquipable {

    public AnimEscudo() {
        super();
    }

    public AnimEscudo(Sprite sprite, short a1, short a2, short a3, short a4, boolean iniciado) {
        super(sprite, a1, a2, a3, a4, iniciado);
    }

    public AnimEscudo(AnimEquipable original) {
        super(original);
    }
}
