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

import ar.net.argentum.cliente.Recursos;
import ar.net.argentum.cliente.motor.texturas.Textura;
import ar.net.argentum.cliente.juego.InventarioSlot;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;
import static org.lwjgl.nuklear.Nuklear.*;
import org.lwjgl.opengl.GL32C;
import static org.lwjgl.system.MemoryStack.*;
import ar.net.argentum.cliente.motor.IInterfaz;

/**
 * IInterfaz grafica del inventario
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Inventario {

    protected final IInterfaz padre;

    protected final NkColor rojo = NkColor.create().set((byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0xFF);
    protected final NkColor amarillo = NkColor.create().set((byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0xFF);
    protected final NkColor colorEquipado = NkColor.create().set((byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0xFF);

    public Inventario(IInterfaz padre) {
        this.padre = padre;
    }

    public void layout(NkContext ctx, int x, int y) {
        ar.net.argentum.cliente.juego.Inventario inv = padre.getJuego().getUsuario().getInventario();
        InventarioSlot[] objetos = inv.getObjetos();

        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);

            if (nk_begin(
                    ctx,
                    "Inventario",
                    nk_rect(x, y, 225, 190, rect),
                    NK_WINDOW_BORDER
            )) {
                // Guardamos los estilos que vamos a pisar
                NkVec2 margenViejo = NkVec2.mallocStack(stack);
                margenViejo.set(ctx.style().button().padding());
                NkColor colorBorde = NkColor.mallocStack(stack);
                colorBorde.set(ctx.style().button().border_color());
                float bordeViejo = ctx.style().button().rounding();
                NkColor fondoViejo = NkColor.mallocStack(stack);
                fondoViejo.set(ctx.style().button().normal().data().color());

                // Pisamos el estiloo
                NkVec2 margen = NkVec2.mallocStack(stack);
                margen.set(0, 0);

                ctx.style().button().padding().set(margen);
                ctx.style().button().rounding(0);

                nk_layout_row_static(ctx, 32, 32, 5);
                {
                    GL32C.glEnable(GL32C.GL_BLEND);
                    for (int i = 0; i < objetos.length; ++i) {
                        final InventarioSlot obj = objetos[i];
                        final Textura tex = padre.getTexturas().getTextura(Recursos.getSprite(obj != null ? obj.getGrhIndex() : 0));
                        final int textID = tex.getId();
                        final String strCantidad = obj == null ? "" : String.valueOf(obj.getCantidad());

                        NkImage image = NkImage.create();
                        image.handle(it -> it.id(textID));

                        // Si el objeto esta seleccionado, entonces dibujamos el borde en rojo
                        if (inv.getSeleccionado() == i) {
                            ctx.style().button().border_color(rojo);
                        } else {
                            ctx.style().button().border_color(colorBorde);
                        }
                        if (obj != null && obj.isEquipado()) {
                            ctx.style().button().normal().data().color(colorEquipado);
                        } else {
                            ctx.style().button().normal().data().color(fondoViejo);
                        }
                        if (nk_button_image_label(ctx, image, strCantidad, NK_TEXT_ALIGN_CENTERED)) {
                            System.out.println("Clickeaste un objeto!");
                            inv.setSeleccionado(i);
                        }
                    }
                }
                // Devolvemos el estilo anterior
                ctx.style().button().padding().set(margenViejo);
                ctx.style().button().rounding(bordeViejo);
                ctx.style().button().border_color(colorBorde);
                ctx.style().button().normal().data().color(fondoViejo);

                nk_layout_row_dynamic(ctx, 25, 3);
                {
                    // Posicion en el mapa
                    nk_label(ctx, String.valueOf(padre.getJuego().getMapaActual()), NK_TEXT_CENTERED);
                    nk_label(ctx, String.valueOf(padre.getJuego().getUsuario().getPosicion().x()), NK_TEXT_CENTERED);
                    nk_label(ctx, String.valueOf(padre.getJuego().getUsuario().getPosicion().y()), NK_TEXT_CENTERED);
                }
            }
            nk_end(ctx);
        }
    }
}
