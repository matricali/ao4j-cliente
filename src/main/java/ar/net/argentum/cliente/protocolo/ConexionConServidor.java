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

import ar.net.argentum.cliente.ClienteArgentum;
import ar.net.argentum.cliente.interfaz.Pantallas;
import ar.net.argentum.cliente.motor.gamedata.Animacion;
import ar.net.argentum.cliente.motor.gamedata.Baldosa;
import ar.net.argentum.cliente.motor.user.InventarioSlot;
import ar.net.argentum.cliente.motor.user.Orientacion;
import ar.net.argentum.cliente.motor.user.Usuario;
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

    private final ClienteArgentum cliente;

    private static final byte PQT_DESCONECTAR = 0x1;
    private static final byte PQT_INICIAR_SESION = 0x2;
    private static final byte PQT_CHAT = 0x3;
    private static final byte PQT_ACTUALIZAR_INVENTARIO = 0x4;
    private static final byte PQT_CAMBIA_MUNDO = 0x5;
    private static final byte PQT_USUARIO_NOMBRE = 0x6;
    private static final byte PQT_USUARIO_POSICION = 0x7;
    private static final byte PQT_USUARIO_STATS = 0x8;
    private static final byte PQT_MUNDO_REPRODUCIR_ANIMACION = 0x9;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private boolean corriendo = false;
    private final String direccion;
    private final int puerto;
    private final String usuario;
    private final String password;
    private final byte version = 0x1;

    public ConexionConServidor(ClienteArgentum cliente, String direccion, int puerto, String usuario, String password) {
        this.cliente = cliente;
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
                cliente.getInterfaz().setPantalla(Pantallas.INICIAR_SESION);

                // Mostramos mensaje de error
                cliente.getInterfaz().mostrarMensaje(ex.getMessage(), "No se pudo conectar al servidor");

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
            dos.writeByte(PQT_INICIAR_SESION); // Paquete INICIAR_SESION
            dos.writeByte(version); // Version del protocolo
            dos.writeUTF(usuario);
            dos.writeUTF(password);

            // Mostramos el panel de juego
            cliente.getInterfaz().setPantalla(Pantallas.JUGAR);

            // Este bucle se encarga de intercambiar la informacion con el servidor
            while (corriendo) {
                enviarYRecibir();
            }

        } catch (HeadlessException | IOException e) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, e);
        }

        terminar();
    }

    public void enviarYRecibir() {
        try {
            // obtenemos el tipo de paquete recibido
            Byte tipoPaquete = dis.readByte();

            switch (tipoPaquete) {
                case PQT_DESCONECTAR:
                    // Desconectar
                    String mensaje = dis.readUTF();

                    // Mostramos la pantalla de conectar
                    cliente.getInterfaz().setPantalla(Pantallas.INICIAR_SESION);

                    // Mostramos el mensaje
                    if (mensaje.isEmpty()) {
                        mensaje = "Has sido desconectado del servidor. Razon desconocida.";
                    }
                    cliente.getInterfaz().mostrarMensaje(mensaje, "Has sido desconectado");

                    // Nos hecharon, no hay nada mas que hacer.
                    terminar();
                    break;

                case PQT_CHAT:
                    // Mensaje de chat
                    cliente.getInterfaz().agregarMensajeConsola(dis.readUTF());
                    break;

                case PQT_ACTUALIZAR_INVENTARIO:
                    // Actualizar slot de inventario
                    recibirUsuarioInventarioActualizarSlot(dis);
                    break;

                case PQT_CAMBIA_MUNDO:
                    // Actualizar slot de inventario
                    recibirUsuarioCambiaMapa(dis);
                    break;

                case PQT_USUARIO_NOMBRE:
                    // Actualizar nombre para mostrar del usuario
                    recibirUsuarioNombre(dis);
                    break;

                case PQT_USUARIO_POSICION:
                    // Actualizar slot de inventario
                    recibirUsuarioPosicion(dis);
                    break;

                case PQT_USUARIO_STATS:
                    // Actualizar slot de inventario
                    recibirUsuarioStats(dis);
                    break;

                default:
                    String received = dis.readUTF();
//                    GUI.agregarMensajeConsola(received);
                    System.out.println(received);
            }
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviarChat(String mensaje) {
        try {
            dos.writeByte(PQT_CHAT); // CHAT
            dos.writeUTF(mensaje);
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void detener() {
        this.corriendo = false;
    }

    public void terminar() {
        detener();

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
//        GUI.mostrarPanel("conectar");
        cliente.setJugando(false);
    }

    public void recibirUsuarioInventarioActualizarSlot(DataInputStream dis) {
        try {
            int slot = dis.readInt();
            int id_objeto = dis.readInt();
            int tipo = dis.readInt();
            int grhIndex = dis.readInt();
            String nombre = dis.readUTF();
            int cantidad = dis.readInt();
            boolean equipado = dis.readBoolean();

            InventarioSlot nuevoSlot = new InventarioSlot(id_objeto, grhIndex, nombre, cantidad, equipado);

            cliente.getJuego().getUsuario().getInventario().setSlot(slot, nuevoSlot);
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void recibirUsuarioCambiaMapa(DataInputStream dis) {
        try {
            int numMapa = dis.readInt();
            cliente.getJuego().cargarMapa(numMapa);
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void recibirUsuarioNombre(DataInputStream dis) {
        try {
            cliente.getJuego().getUsuario().setNombre(dis.readUTF());
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void recibirUsuarioPosicion(DataInputStream dis) {
        try {
            int numMapa = dis.readInt();
            int x = dis.readInt();
            int y = dis.readInt();
            int heading = dis.readInt();

            Orientacion orientacion = Orientacion.valueOf(heading);

            Usuario usuario = cliente.getJuego().getUsuario();
            usuario.setPosicion(x, y);

            if (!cliente.isJugando()) {
                cliente.getMotorGrafico().crearPersonaje(1, usuario.getNombre(), x, y, orientacion, 1, 128, 1, 1, 1);
                cliente.setJugando(true);
            }
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void recibirUsuarioStats(DataInputStream dis) {
        try {
            Usuario user = cliente.getJuego().getUsuario();
            // Salud
            user.setMinHP(dis.readInt());
            user.setMaxHP(dis.readInt());
            // Mana
            user.setMinMana(dis.readInt());
            user.setMaxMana(dis.readInt());
            // Stamina
            user.setMinStamina(dis.readInt());
            user.setMaxStamina(dis.readInt());
            // Hambre
            user.setMinHambre(dis.readInt());
            user.setMaxHambre(dis.readInt());
            // Sed
            user.setMinSed(dis.readInt());
            user.setMaxSed(dis.readInt());
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void recibirMundoReproducirAnimacion(DataInputStream dis) {
        try {
            int animacion = dis.readInt();
            int x = dis.readInt();
            int y = dis.readInt();
            Baldosa baldosa = cliente.getJuego().getBaldosa(x, y);
            if (baldosa != null) {
                baldosa.setCapa(3, new Animacion((short) animacion, false));
            }
        } catch (IOException ex) {
            Logger.getLogger(ConexionConServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
