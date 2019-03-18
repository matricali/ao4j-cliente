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

import ar.net.argentum.cliente.motor.surface.Textura;
import ar.net.argentum.cliente.motor.user.InventarioSlot;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.opengl.GL32C;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Interfaz grafica del inventario
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Inventario {

    protected final IInterfaz padre;

    public Inventario(IInterfaz padre) {
        this.padre = padre;
    }

    public void layout(NkContext ctx, int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);

            if (nk_begin(
                    ctx,
                    "Inventario",
                    nk_rect(x, y, 225, 180, rect),
                    NK_WINDOW_BORDER
            )) {
                NkVec2 margenViejo = NkVec2.mallocStack(stack);
                margenViejo.set(ctx.style().button().padding());

                NkVec2 margen = NkVec2.mallocStack(stack);
                margen.set(0, 0);

                float bordeViejo = ctx.style().button().rounding();

                ctx.style().button().padding().set(margen);
                ctx.style().button().rounding(0);

                nk_layout_row_static(ctx, 32, 32, 5);
                GL32C.glEnable(GL32C.GL_BLEND);
                for (InventarioSlot obj : padre.getJuego().getUsuario().getInventario().getObjetos()) {

                    final Textura tex = padre.getTexturas().getTextura(padre.getJuego().getSprite(obj != null ? obj.getGrhIndex() : 0));
                    final int textID = tex.getId();
                    final String strCantidad = obj == null ? "" : String.valueOf(obj.getCantidad());

                    NkImage image = NkImage.create();
                    image.handle(it -> it.id(textID));
                    if (nk_button_image_label(ctx, image, strCantidad, NK_TEXT_ALIGN_BOTTOM)) {
                        // Action to be performed when the button is clicked
                        System.out.println("Clickeaste un objeto");
                    }
                }
                ctx.style().button().padding().set(margenViejo);
                ctx.style().button().rounding(bordeViejo);

                nk_layout_row_dynamic(ctx, 25, 3);
                {
                    // Nombre del usuario
                    nk_label(ctx, String.valueOf(padre.getJuego().getMapaActual()), NK_TEXT_CENTERED);
                    nk_label(ctx, String.valueOf(padre.getJuego().getUsuario().getPosicion().x()), NK_TEXT_CENTERED);
                    nk_label(ctx, String.valueOf(padre.getJuego().getUsuario().getPosicion().y()), NK_TEXT_CENTERED);
                }
            }
            nk_end(ctx);
        }
    }
}
