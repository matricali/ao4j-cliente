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
package ar.net.argentum.cliente.gui;

import ar.net.argentum.cliente.Cliente;
import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class GUI {

    private static StyleContext estilos;

    private GUI() {

    }

    public static void mostrarMensaje(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(Cliente.getCliente().getVentanaPrincipal(), mensaje, titulo, JOptionPane.PLAIN_MESSAGE);
    }

    public static void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(Cliente.getCliente().getVentanaPrincipal(), mensaje);
    }

    public static StyleContext getEstilos() {
        if (null == estilos) {
            estilos = new StyleContext();

            final Style mensajesChat = estilos.addStyle("CHAT", null);
            mensajesChat.addAttribute(StyleConstants.Foreground, Color.LIGHT_GRAY);
            mensajesChat.addAttribute(StyleConstants.FontSize, 16);
            mensajesChat.addAttribute(StyleConstants.FontFamily, "serif");
        }

        return estilos;
    }
}
