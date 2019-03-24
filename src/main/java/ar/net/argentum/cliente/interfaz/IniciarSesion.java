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

import ar.net.argentum.cliente.motor.IInterfaz;
import ar.net.argentum.cliente.interfaz.componentes.TextField;
import org.lwjgl.nuklear.*;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.system.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Interfaz grafica de inicio de sesion
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class IniciarSesion {

    protected final IInterfaz padre;
    protected final TextField usuario;
    protected final TextField password;

    public IniciarSesion(IInterfaz padre) {
        this.padre = padre;
        this.usuario = new TextField(16, false);
        this.password = new TextField(16, false);
    }

    public void layout(NkContext ctx, int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);

            if (nk_begin(
                    ctx,
                    "Iniciar sesion",
                    nk_rect(213, 225, 350, 150, rect),
                    NK_WINDOW_BORDER | NK_WINDOW_TITLE
            )) {

                // Usuario
                nk_layout_row_begin(ctx, NK_DYNAMIC, 25, 2);
                {
                    nk_layout_row_push(ctx, 0.3f);
                    nk_label(ctx, "Usuario:", NK_TEXT_LEFT);
                    nk_layout_row_push(ctx, 0.7f);
                    usuario.render(ctx);
                }
                nk_layout_row_end(ctx);

                // Contraseña
                nk_layout_row_begin(ctx, NK_DYNAMIC, 25, 2);
                {
                    nk_layout_row_push(ctx, 0.3f);
                    nk_label(ctx, "Contrasena:", NK_TEXT_LEFT);
                    nk_layout_row_push(ctx, 0.7f);
                    password.render(ctx);
                }
                nk_layout_row_end(ctx);

                // btnIniciarSesion
                nk_layout_row_static(ctx, 30, 150, 1);
                if (nk_button_label(ctx, "Iniciar sesion")) {
                    padre.getCliente().conectar("localhost", 7666, usuario.getValue(), password.getValue());
                }
            }
            nk_end(ctx);
        }
    }
}
