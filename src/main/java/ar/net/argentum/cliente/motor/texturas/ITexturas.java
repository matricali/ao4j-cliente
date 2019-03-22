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
package ar.net.argentum.cliente.motor.texturas;

import ar.net.argentum.cliente.motor.Sprite;

/**
 * Representa un manejador de texturas.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface ITexturas {

    /**
     * Aca se deberia hacer todo lo necesario antes de podar cargar las texturas
     */
    public void inicializar();

    /**
     * Obtener la instancia de Textura del Sprite dado
     *
     * @param sprite Sprite del cual se desea obtener la textura
     * @return La textura
     */
    public Textura getTextura(Sprite sprite);

    /**
     * Obtener la instancia de Textura correspondiente al numero de archivo
     *
     * @param fileNum Numero de archivo del cual se desea obtener la textura
     * @return La textura
     */
    public Textura getTextura(int fileNum);

    /**
     * Cargar un archivo de textura en memoria y generar la instancia de Textura
     *
     * @param fileNum Numero de archivo del cual se desea obtener la textura
     * @return
     */
    public Textura cargarTextura(int fileNum);

    /**
     * Aca se deberia destruir todo lo que ya no se necesita
     */
    public void destruir();
}
