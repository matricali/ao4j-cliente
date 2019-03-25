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

import ar.net.argentum.cliente.ClienteArgentum;
import ar.net.argentum.cliente.Juego;
import ar.net.argentum.cliente.interfaz.Pantallas;
import ar.net.argentum.cliente.motor.texturas.ITexturas;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public interface IInterfaz {

    public Pantallas getPantalla();

    public void setPantalla(Pantallas pantalla);

    public void dibujar();

    public void terminar();

    public void entradaTeclado(long window, int key, int scancode, int action, int mods);

    public void mouseEvents(long window, int x, int y, int button, int action, int mods);

    public Juego getJuego();

    public ITexturas getTexturas();

    public ClienteArgentum getCliente();

    public void mostrarMensaje(String mensaje);

    public void mostrarMensaje(String mensaje, String titulo);

    public void agregarMensajeConsola(String mensaje);
}
