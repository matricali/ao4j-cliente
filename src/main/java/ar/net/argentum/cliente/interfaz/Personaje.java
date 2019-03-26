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
import ar.net.argentum.cliente.juego.Usuario;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Interfaz grafica del personaje
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Personaje {

    protected final IInterfaz padre;

    public Personaje(IInterfaz padre) {
        this.padre = padre;
    }

    public void layout(NkContext ctx, int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);

            if (nk_begin(
                    ctx,
                    "Personaje",
                    nk_rect(x, y, 225, 250, rect),
                    NK_WINDOW_BORDER
            )) {
                Usuario user = padre.getJuego().getUsuario();

                NkColor oldColor = NkColor.mallocStack(stack);
                oldColor.set(ctx.style().progress().active().data().color());

                NkColor rojo = NkColor.mallocStack().set((byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0xFF);
                NkColor celeste = NkColor.mallocStack().set((byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF);
                NkColor amarillo = NkColor.mallocStack().set((byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0xFF);
                NkColor verde = NkColor.mallocStack().set((byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0xFF);
                NkColor azul = NkColor.mallocStack().set((byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF);

                nk_layout_row_dynamic(ctx, 25, 1);
                {
                    // Nombre del usuario
                    nk_label(ctx, user.getNombre(), NK_TEXT_CENTERED);
                }

                nk_layout_row_dynamic(ctx, 18, 1);
                {
                    // Nivel y experiencia
                    nk_label(ctx, "Nivel " + user.getNivel() + " [" + user.getExperienciaActual() + "/" + user.getExperienciaSiguienteNivel() + "]", NK_TEXT_CENTERED);
                }

                nk_layout_row_dynamic(ctx, 25, 1);
                {
                    // Barra de experiencia
                    ctx.style().progress().cursor_normal().data().color(verde);
                    nk_prog(ctx, user.getExperienciaActual(), user.getExperienciaSiguienteNivel(), false);
                }

                nk_layout_row_dynamic(ctx, 25, 3);
                {
                    // Botones
                    if (nk_button_label(ctx, "Habilidades")) {
                       
                    }
                    if (nk_button_label(ctx, "Estadisticas")) {
                       
                    }
                    if (nk_button_label(ctx, "Opciones")) {
                       
                    }
                }

                nk_layout_row_dynamic(ctx, 16, 1);
                {
                    // Estilo de los progressbar
                    ctx.style().progress().border(0f);

                    // Barra de vida
                    ctx.style().progress().cursor_normal().data().color(rojo);
                    nk_prog(ctx, user.getMinHP(), user.getMaxHP(), false);

                    // Barra de mana
                    ctx.style().progress().cursor_normal().data().color(celeste);
                    nk_prog(ctx, user.getMinMana(), user.getMaxMana(), false);

                    // Barra de stamina
                    ctx.style().progress().cursor_normal().data().color(amarillo);
                    nk_prog(ctx, user.getMinStamina(), user.getMaxStamina(), false);

                    // Barra de hambre
                    ctx.style().progress().cursor_normal().data().color(verde);
                    nk_prog(ctx, user.getMinHambre(), user.getMaxHambre(), false);

                    // Barra de sed
                    ctx.style().progress().cursor_normal().data().color(azul);
                    nk_prog(ctx, user.getMinSed(), user.getMaxSed(), false);

                    ctx.style().progress().cursor_normal().data().color(oldColor);
                }

            }
            nk_end(ctx);
        }
    }
}
