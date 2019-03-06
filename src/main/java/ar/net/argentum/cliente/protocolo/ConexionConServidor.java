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
package ar.net.argentum.cliente.protocolo;

import ar.net.argentum.cliente.Cliente;
import ar.net.argentum.cliente.gui.GUI;
import java.awt.HeadlessException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class ConexionConServidor extends Thread {

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private boolean corriendo = false;
    private final String direccion;
    private final int puerto;
    private final String usuario;
    private final String password;
    private final byte version = 0x1;

    public ConexionConServidor(String direccion, int puerto, String usuario, String password) {
        this.direccion = direccion;
        this.puerto = puerto;
        this.usuario = usuario;
        this.password = password;
    }

    @Override
    public void run() {
        try {
            System.out.println("Intentando conectar a " + direccion + ":" + puerto + "...");
            // Resolver direccion
            InetAddress ip = InetAddress.getByName(direccion);

            // Establecemos la coneccion al puerto indicado
            try {
                this.socket = new Socket(ip, puerto);
            } catch (ConnectException ex) {
                // Volvemos a la pantalla de conectar
                Cliente.getCliente().mostrarPanel("conectar");

                // Mostramos mensaje de error
                GUI.mostrarMensaje(ex.getMessage(), "No se pudo conectar al servidor");

                // Ya no hay nada que hacer
                return;
            }

            // Obtenemos los Streams de entrada y de salida
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());

            this.corriendo = true;
            System.out.println("Conectado!");

            // Iniciamos sesion
            System.out.println("Iniciandooo sesion");
            dos.writeByte(0x2); // Paquete INICIAR_SESION
            dos.writeByte(version); // Version del protocolo
            dos.writeUTF(usuario);
            dos.writeUTF(password);

            // Mostramos el panel de juego
            Cliente.getCliente().mostrarPanel("jugar");

            // Este bucle se encarga de intercambiar la informacion con el servidor
            while (corriendo) {

                // obtenemos el tipo de paquete recibido
                Byte tipoPaquete = dis.readByte();

                switch (tipoPaquete) {
                    case 0x1:
                        // Desconectar
                        String mensaje = dis.readUTF();
                        if (mensaje.isEmpty()) {
                            mensaje = "Has sido desconectado del servidor. Razon desconocida.";
                        }
                        GUI.mostrarMensaje(mensaje, "Has sido desconectado");
                        terminar();
                        break;

                    case 0x3:
                        // Mensaje de chat
                        Cliente.getCliente().getPanelJugar().agregarMensajeConsola(dis.readUTF());
                        break;

                    default:
                        String received = dis.readUTF();
                        Cliente.getCliente().getPanelJugar().agregarMensajeConsola(received);
                        System.out.println(received);
                }
            }

        } catch (HeadlessException | IOException e) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, e);
        }

        terminar();
    }

    public void enviarChat(String mensaje) {
        try {
            dos.writeByte(0x3); // CHAT
            dos.writeUTF(mensaje);
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void terminar() {
        this.corriendo = false;

        try {
            // Cerramos el socket
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Cerramos los recursos abiertos
        try {
            dis.close();
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            dos.close();
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Volvemos a la pantalla de conectar
        Cliente.getCliente().mostrarPanel("conectar");
    }
}
