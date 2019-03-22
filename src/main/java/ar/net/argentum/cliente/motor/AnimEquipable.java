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

import ar.net.argentum.cliente.mundo.Orientacion;

/**
 * Almacena un cuerpo que puede ser vestido por un {@link Personaje}
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public abstract class AnimEquipable {

    /**
     * Animaciones del personaje en cada direccion
     */
    Animacion[] animaciones = new Animacion[4 + 1];

    public AnimEquipable() {
        this.animaciones[1] = new Animacion();
        this.animaciones[2] = new Animacion();
        this.animaciones[3] = new Animacion();
        this.animaciones[4] = new Animacion();
    }

    public AnimEquipable(Sprite sprite, short a1, short a2, short a3, short a4, boolean iniciado) {
        this.animaciones[1] = new Animacion(a1, sprite, iniciado);
        this.animaciones[2] = new Animacion(a2, sprite, iniciado);
        this.animaciones[3] = new Animacion(a3, sprite, iniciado);
        this.animaciones[4] = new Animacion(a4, sprite, iniciado);
    }

    public AnimEquipable(AnimEquipable original) {
        this.animaciones[1] = new Animacion(original.animaciones[1]);
        this.animaciones[2] = new Animacion(original.animaciones[2]);
        this.animaciones[3] = new Animacion(original.animaciones[3]);
        this.animaciones[4] = new Animacion(original.animaciones[4]);
    }

    public Animacion getAnimacion(Orientacion orientacion) {
        return animaciones[orientacion.valor()];
    }
}
