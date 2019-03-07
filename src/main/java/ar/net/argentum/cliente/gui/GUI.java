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
import java.awt.CardLayout;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class GUI {

    private static GUI instancia;
    private final VentanaPrincipal ventana;
    private final JPanel panelPadre;
    private final PanelConectar panelConectar;
    private final PanelJugar panelJugar;
    private final PanelCargando panelCargando;
    private final CardLayout layout;

    private static StyleContext estilos;

    public static GUI iniciar(VentanaPrincipal vp) {
        if (instancia == null) {
            instancia = new GUI(vp);
        }
        return instancia;
    }

    private GUI(VentanaPrincipal vp) {
        this.ventana = vp;

        // Creamos un nuevo panel padre
        this.layout = new CardLayout();
        this.panelPadre = new JPanel(layout);

        // Creamos un nuevo panel de conectar
        this.panelConectar = new PanelConectar();

        // Creamos un nuevo panel de juego
        this.panelJugar = new PanelJugar();

        // Creamos la pantalla de carga
        this.panelCargando = new PanelCargando();

        // Agregamos todos los paneles al panel padre
        panelPadre.add(panelConectar, "conectar");
        panelPadre.add(panelJugar, "jugar");
        panelPadre.add(panelCargando, "cargando");

        // Agregamos el panel padre a la ventana principal
        ventana.add(panelPadre);
    }

    public static void mostrarMensaje(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(Cliente.getCliente().getVentanaPrincipal(), mensaje, titulo, JOptionPane.PLAIN_MESSAGE);
    }

    public static void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(Cliente.getCliente().getVentanaPrincipal(), mensaje);
    }

    public static void mostrarPanel(String nombre) {
        if (instancia != null) {
            instancia.layout.show(instancia.panelPadre, nombre);
        }
    }

    public static StyleContext getEstilos() {
        if (null == estilos) {
            estilos = new StyleContext();

            final Style mensajesChat = estilos.addStyle("CHAT", null);
            mensajesChat.addAttribute(StyleConstants.Foreground, Color.LIGHT_GRAY);
            mensajesChat.addAttribute(StyleConstants.FontSize, 14);
            mensajesChat.addAttribute(StyleConstants.FontFamily, "Arial");

            final Style colorNegro = estilos.addStyle("BLACK", mensajesChat);
            colorNegro.addAttribute(StyleConstants.Foreground, Color.BLACK);

            final Style colorAzulOscuro = estilos.addStyle("DARK_BLUE", mensajesChat);
            colorAzulOscuro.addAttribute(StyleConstants.Foreground, Color.getHSBColor(217, 85, 43));

            final Style colorVerdeOscuro = estilos.addStyle("DARK_GREEN", mensajesChat);
            colorVerdeOscuro.addAttribute(StyleConstants.Foreground, Color.getHSBColor(90, 81, 40));

            final Style colorTurquesaOscuro = estilos.addStyle("DARK_AQUA", mensajesChat);
            colorTurquesaOscuro.addAttribute(StyleConstants.Foreground, Color.getHSBColor(180, 100, 50));

            final Style colorRojoOscuro = estilos.addStyle("DARK_RED", mensajesChat);
            colorRojoOscuro.addAttribute(StyleConstants.Foreground, Color.getHSBColor(90, 81, 40));

            final Style colorPurpuraOscuro = estilos.addStyle("DARK_PURPLE", mensajesChat);
            colorPurpuraOscuro.addAttribute(StyleConstants.Foreground, Color.getHSBColor(300, 100, 50));

            final Style colorAmarilloOscuro = estilos.addStyle("GOLD", mensajesChat);
            colorAmarilloOscuro.addAttribute(StyleConstants.Foreground, Color.getHSBColor(53, 80, 84));

            final Style colorGrisClaro = estilos.addStyle("GRAY", mensajesChat);
            colorGrisClaro.addAttribute(StyleConstants.Foreground, Color.LIGHT_GRAY);

            final Style colorGrisOscuro = estilos.addStyle("DARK_GRAY", mensajesChat);
            colorGrisOscuro.addAttribute(StyleConstants.Foreground, Color.DARK_GRAY);

            final Style colorAzulClaro = estilos.addStyle("BLUE", mensajesChat);
            colorAzulClaro.addAttribute(StyleConstants.Foreground, Color.BLUE);

            final Style colorVerdeClaro = estilos.addStyle("GREEN", mensajesChat);
            colorVerdeClaro.addAttribute(StyleConstants.Foreground, Color.GREEN);

            final Style colorTurquesaClaro = estilos.addStyle("AQUA", mensajesChat);
            colorTurquesaClaro.addAttribute(StyleConstants.Foreground, Color.CYAN);

            final Style colorRojoClaro = estilos.addStyle("RED", mensajesChat);
            colorRojoClaro.addAttribute(StyleConstants.Foreground, Color.RED);

            final Style colorMagenta = estilos.addStyle("LIGHT_PURPLE", mensajesChat);
            colorMagenta.addAttribute(StyleConstants.Foreground, Color.MAGENTA);

            final Style colorAmarilloClaro = estilos.addStyle("YELLOW", mensajesChat);
            colorAmarilloClaro.addAttribute(StyleConstants.Foreground, Color.YELLOW);

            final Style colorBlanco = estilos.addStyle("WHITE", mensajesChat);
            colorBlanco.addAttribute(StyleConstants.Foreground, Color.WHITE);
        }

        return estilos;
    }

    public JPanel getPanel() {
        return panelPadre;
    }

    public static void agregarMensajeConsola(String mensaje) {
        if (instancia != null) {
            JTextPane consola = instancia.panelJugar.getConsola();
            Document doc = consola.getDocument();
            try {
                // Se paramos el texto por color
                Matcher matcher = ColoresChat.separarColores(mensaje);
                Style estilo;
                while (matcher.find()) {
                    estilo = getEstilos().getStyle("CHAT");
                    if (matcher.group("color") != null && !matcher.group("color").isEmpty()) {
                        ColoresChat color = ColoresChat.getByChar(matcher.group("color").substring(1));
                        if (color.esColor()) {
                            estilo = getEstilos().getStyle(color.name());
                        }
                    }
                    doc.insertString(doc.getLength(), matcher.group("texto"), estilo);
                }
                // Insertamos un salto de linea al final del mensaje
                doc.insertString(doc.getLength(), "\n", getEstilos().getStyle("CHAT"));
            } catch (BadLocationException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            consola.setCaretPosition(consola.getDocument().getLength() - 1);
        }
    }
}
