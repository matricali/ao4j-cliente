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
 * along with this programa.  If not, see <https://www.gnu.org/licenses/>.
 */
package ar.net.argentum.cliente.motor.surface;

import ar.net.argentum.cliente.motor.gamedata.Sprite;

/**
 * Representa un manejador de texturas.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface ISurface {

    public void initialize();

    public Textura getTextura(Sprite sprite);

    public Textura getTextura(int fileNum);

    public Textura cargarTextura(int fileNum);
    
    public void destruir();
}
