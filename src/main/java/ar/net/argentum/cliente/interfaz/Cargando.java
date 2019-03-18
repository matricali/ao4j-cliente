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

import org.lwjgl.nuklear.*;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.system.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Interfaz grafica de inicio de sesion
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Cargando {

    public void dibujar(NkContext ctx, String mensaje) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);

            if (nk_begin(
                    ctx,
                    "Cargando",
                    nk_rect(200, 200, 400, 200, rect),
                    NK_WINDOW_BORDER | NK_WINDOW_TITLE
            )) {

                // Usuario
                nk_layout_row_dynamic(ctx, 25, 1);
                {
                    nk_label(ctx, mensaje, NK_TEXT_CENTERED);
                }
            }
            nk_end(ctx);
        }
    }
}
