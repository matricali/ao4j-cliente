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
package ar.net.argentum.cliente.interfaz.componentes;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.system.MemoryStack;

/**
 * Caja de mensajes
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class MessageBox {

    protected final String mensaje;
    protected final String titulo;
    protected int x = 0;
    protected int y = 0;
    protected int ancho = 350;
    protected int alto = 100;
    protected boolean cerrado = false;

    public MessageBox(String mensaje, String titulo) {
        this.mensaje = mensaje;
        this.titulo = titulo;
    }

    public MessageBox(String mensaje) {
        this(mensaje, "");
    }

    /**
     * Dibujamos la caja con Nuklear
     *
     * @param ctx
     */
    public void render(NkContext ctx) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);

            if (nk_begin(
                    ctx,
                    titulo,
                    nk_rect(x, y, ancho, alto, rect),
                    NK_WINDOW_BORDER | NK_WINDOW_TITLE | NK_WINDOW_MOVABLE
            )) {

                nk_layout_row_dynamic(ctx, 25, 1);
                {
                    // Texto del mensaje
                    nk_label(ctx, mensaje, NK_TEXT_CENTERED);
                    // Boton OK
                    if (nk_button_label(ctx, "Aceptar")) {
                        // Cerrar
                        this.cerrado = true;
                    }
                }
            }
            nk_end(ctx);
        }
    }
    
    public boolean isCerrado() {
        return cerrado;
    }
}
