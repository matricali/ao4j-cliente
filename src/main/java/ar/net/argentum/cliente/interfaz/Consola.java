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
package ar.net.argentum.cliente.interfaz;

import ar.net.argentum.cliente.interfaz.componentes.TextField;
import ar.net.argentum.cliente.interfaz.eventos.OnTextFieldEventPerformed;
import org.lwjgl.nuklear.*;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.system.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Interfaz grafica de inicio de sesion
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Consola {

    protected final IInterfaz padre;
    protected final TextField consola;
    protected final TextField mensaje;

    public Consola(IInterfaz padre) {
        this.padre = padre;
        this.consola = new TextField(2048, true);
        this.mensaje = new TextField(255, false, new OnTextFieldEventPerformed() {
            @Override
            public void onCommit(TextField campo) {
                String mensaje = campo.getValue();
                padre.getCliente().getConexion().enviarChat(mensaje);
                // Agregamos mensaje a la consola (?)
                // consola.appendString(mensaje + "\n");
                // Borramos el campo
                campo.reset();
            }
        });
    }

    public void layout(NkContext ctx, int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);

            if (nk_begin(
                    ctx,
                    "Consola",
                    nk_rect(x, y, 544, 140, rect),
                    NK_WINDOW_MOVABLE
            )) {

                // Consola
                nk_layout_row_dynamic(ctx, 95, 1);
                consola.render(ctx);

                // Ingreso de mensajes
                nk_layout_row_dynamic(ctx, 20, 1);
                mensaje.render(ctx);
            }
            nk_end(ctx);
        }
    }

    public void agregarTexto(String texto) {
        consola.appendString(texto + "\n");
    }
}
