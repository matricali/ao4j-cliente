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
package ar.net.argentum.cliente;

import ar.net.argentum.cliente.gui.GUI;
import ar.net.argentum.cliente.gui.PanelJugar;
import ar.net.argentum.cliente.gui.VentanaPrincipal;
import ar.net.argentum.cliente.protocolo.ConexionConServidor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Cliente {

    private static Cliente instancia;

    public static void main(String[] args) throws IOException {
        Cliente cliente = Cliente.getCliente();
    }

    public static Cliente getCliente() {
        if (null == instancia) {
            instancia = new Cliente();
        }
        return instancia;
    }

    private final VentanaPrincipal ventana;
    private GUI interfaz;
    private ConexionConServidor conexion = null;

    private Cliente() {

        // Creamos la ventana principal
        this.ventana = new VentanaPrincipal();

        // Agregamos un evento al cerrar la ventana
        ventana.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (conexion != null) {
                    conexion.terminar();
                }
            }
        });

        this.interfaz = GUI.iniciar(ventana);

        // Mostramos la ventana
        ventana.setVisible(true);
    }

    public void conectar(String direccion, int puerto, String username, String password) {
        System.out.println("Conectando...");
        GUI.mostrarPanel("cargando");
        this.conexion = new ConexionConServidor(direccion, puerto, username, password);
        conexion.start();
    }

    public VentanaPrincipal getVentanaPrincipal() {
        return ventana;
    }

    public ConexionConServidor getConexion() {
        return conexion;
    }

    public GUI getInterfaz() {
        return interfaz;
    }
}
