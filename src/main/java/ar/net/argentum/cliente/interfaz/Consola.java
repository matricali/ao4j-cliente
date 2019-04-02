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
import java.nio.IntBuffer;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import net.museful.general.CircularArrayList;
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

    protected static NkColor convertirColor(ColoresChat color) {
        return NkColor.create().set(color.getR(), color.getG(), color.getB(), (byte) 0xFF);
    }

    protected final GUI padre;
    protected final TextField mensaje;
    protected final CircularArrayList<String> mensajes;
    protected final int alturaRenglon = 14;
    protected final Map<Character, NkColor> colores = new HashMap<Character, NkColor>() {
        {
            put(ColoresChat.ROJO_OSCURO.getChar(), convertirColor(ColoresChat.ROJO_OSCURO));
            put(ColoresChat.ROJO.getChar(), convertirColor(ColoresChat.ROJO));
            put(ColoresChat.AMARILLO_OSCURO.getChar(), convertirColor(ColoresChat.AMARILLO_OSCURO));
            put(ColoresChat.AMARILLO.getChar(), convertirColor(ColoresChat.AMARILLO));
            put(ColoresChat.VERDE_OSCURO.getChar(), convertirColor(ColoresChat.VERDE_OSCURO));
            put(ColoresChat.VERDE.getChar(), convertirColor(ColoresChat.VERDE));
            put(ColoresChat.TURQUESA.getChar(), convertirColor(ColoresChat.TURQUESA));
            put(ColoresChat.TURQUESA_OSCURO.getChar(), convertirColor(ColoresChat.TURQUESA_OSCURO));
            put(ColoresChat.AZUL_OSCURO.getChar(), convertirColor(ColoresChat.AZUL_OSCURO));
            put(ColoresChat.AZUL.getChar(), convertirColor(ColoresChat.AZUL));
            put(ColoresChat.MAGENTA.getChar(), convertirColor(ColoresChat.MAGENTA));
            put(ColoresChat.PURPURA.getChar(), convertirColor(ColoresChat.PURPURA));
            put(ColoresChat.BLANCO.getChar(), convertirColor(ColoresChat.BLANCO));
            put(ColoresChat.GRIS.getChar(), convertirColor(ColoresChat.GRIS));
            put(ColoresChat.GRIS_OSCURO.getChar(), convertirColor(ColoresChat.GRIS_OSCURO));
            put(ColoresChat.NEGRO.getChar(), convertirColor(ColoresChat.NEGRO));
        }
    };
    protected final NkColor colorDefecto = colores.get('7');

    IntBuffer scrollHorizOffset = MemoryStack.stackMallocInt(1);
    IntBuffer scrollVertOffset = MemoryStack.stackMallocInt(1);

    public Consola(GUI padre) {
        this.padre = padre;
        this.mensajes = new CircularArrayList<>(25);
        this.mensaje = new TextField(255, false, new OnTextFieldEventPerformed() {
            @Override
            public void onCommit(TextField campo) {
                String mensaje = campo.getValue();
                padre.getCliente().getConexion().enviarChat(mensaje);
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
                    NK_WINDOW_SCROLL_AUTO_HIDE
            )) {

                // Consola
                nk_layout_row_dynamic(ctx, 98, 1);
                {
                    if (nk_group_scrolled_offset_begin(ctx, scrollHorizOffset, scrollVertOffset, "consolaMensajes", NK_WINDOW_SCROLL_AUTO_HIDE)) {
                        nk_layout_row_dynamic(ctx, alturaRenglon, 1);
                        {
                            try {
                                NkColor colorActual = colorDefecto;
                                for (String linea : mensajes) {
                                    colorActual = colorDefecto;
                                    Matcher matcher = ColoresChat.separarColores(linea);
                                    while (matcher.find()) {
                                        String color = matcher.group("color");
                                        String texto = matcher.group("texto");
                                        if (color != null && !color.isEmpty()) {
                                            char c = matcher.group("color").charAt(1);
                                            if (colores.containsKey(c)) {
                                                colorActual = colores.get(c);
                                            }
                                        }
                                        if (texto != null && !texto.isEmpty()) {
                                            nk_label_colored(ctx, normalizar(texto), NK_TEXT_ALIGN_LEFT, colorActual);
                                        }
                                    }
                                }

                            } catch (NoSuchElementException ex) {

                            }
                        }
                        nk_group_scrolled_end(ctx);
                    }
                }

                // Ingreso de mensajes
                nk_layout_row_dynamic(ctx, 20, 1);
                mensaje.render(ctx);
            }
            nk_end(ctx);
        }
    }

    public void agregarTexto(String texto) {
        if (mensajes.size() == mensajes.capacity()) {
            // Si la cola de mensajes esta llena, entonces borramos el mas viejo
            mensajes.remove(0);
        }
        // Agregamos el mensaje a la lista
        mensajes.add(texto);
        // Movemos el scroll al final
        scrollVertOffset.put(0, (alturaRenglon + 2) * mensajes.size());
    }

    protected String normalizar(String input) {
        return input == null ? null
                : Normalizer.normalize(input, Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "");
    }
}
