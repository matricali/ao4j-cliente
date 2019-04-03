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
import ar.net.argentum.cliente.motor.Animacion;
import ar.net.argentum.cliente.juego.InventarioSlot;
import ar.net.argentum.cliente.juego.Objeto;
import ar.net.argentum.cliente.juego.Usuario;
import ar.net.argentum.cliente.mundo.Baldosa;
import ar.net.argentum.cliente.mundo.Orientacion;
import ar.net.argentum.cliente.sonido.Sonido;
import java.awt.HeadlessException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class ConexionConServidor extends Thread {

    protected static final byte PQT_DESCONECTAR = 0x1;
    protected static final byte PQT_INICIAR_SESION = 0x2;
    protected static final byte PQT_CHAT = 0x3;
    protected static final byte PQT_ACTUALIZAR_INVENTARIO = 0x4;
    protected static final byte PQT_CAMBIA_MUNDO = 0x5;
    protected static final byte PQT_USUARIO_NOMBRE = 0x6;
    protected static final byte PQT_USUARIO_POSICION = 0x7;
    protected static final byte PQT_USUARIO_STATS = 0x8;
    protected static final byte PQT_MUNDO_REPRODUCIR_ANIMACION = 0x9;
    protected static final byte PQT_USUARIO_CAMINAR = 0x10;
    protected static final byte PQT_USUARIO_CAMBIAR_DIRECCION = 0x11;
    protected static final byte PQT_PERSONAJE_CREAR = 0x12;
    protected static final byte PQT_PERSONAJE_CAMBIAR = 0x13;
    protected static final byte PQT_PERSONAJE_CAMINAR = 0x14;
    protected static final byte PQT_PERSONAJE_ANIMACION = 0x15;
    protected static final byte PQT_PERSONAJE_QUITAR = 0x16;
    protected static final byte PQT_CLICK = 0x17;
    protected static final byte PQT_USUARIO_GOLPEA = 0x18;
    protected static final byte PQT_USUARIO_EXPERIENCIA = 0x19;
    protected static final byte PQT_USUARIO_EQUIPAR_SLOT = 0x20;
    protected static final byte PQT_MUNDO_REPRODUCIR_SONIDO = 0x21;
    protected static final byte PQT_MUNDO_OBJETO = 0x22;
    protected static final byte PQT_USUARIO_USAR_OBJETO = 0x23;
    protected static final byte PQT_USUARIO_TIRAR_OBJETO = 0x24;
    protected static final byte PQT_USUARIO_AGARRAR_OBJETO = 0x25;
    protected static final byte PQT_MUNDO_BALDOSA_BLOQUEADA = 0x26;

    protected static final Logger LOGGER = Logger.getLogger(ConexionConServidor.class.getName());
    private final ClienteArgentum cliente;

    protected Socket socket;
    protected DataInputStream dis;
    protected DataOutputStream dos;

    protected boolean corriendo = false;
    protected final String direccion;
    protected final int puerto;
    protected final String usuario;
    protected final String password;
    protected final byte version = 0x1;

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
            LOGGER.info("Intentando conectar a " + direccion + ":" + puerto + "...");
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
            LOGGER.info("Conectado!");

            // Iniciamos sesion
            LOGGER.info("Iniciando sesion...");
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
            LOGGER.fatal(null, e);
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

                case PQT_USUARIO_EXPERIENCIA:
                    // Actualizar experiencia del usuario
                    recibirUsuarioExperiencia(dis);
                    break;

                case PQT_PERSONAJE_CREAR:
                    recibirPersonajeCrear(dis);
                    break;

                case PQT_PERSONAJE_CAMBIAR:
                    recibirPersonajeCambiar(dis);
                    break;

                case PQT_PERSONAJE_CAMINAR:
                    recibirPersonajeCaminar(dis);
                    break;

                case PQT_PERSONAJE_QUITAR:
                    recibirPersonajeQuitar(dis);
                    break;

                case PQT_PERSONAJE_ANIMACION:
                    recibirPersonajeAnimacion(dis);
                    break;

                case PQT_MUNDO_REPRODUCIR_SONIDO:
                    recibirMundoReproducirSonido(dis);
                    break;

                case PQT_MUNDO_OBJETO:
                    recibirMundoObjeto();
                    break;

                case PQT_MUNDO_BALDOSA_BLOQUEADA:
                    recibirMundoBaldosaBloqueada();
                    break;

                default:
                    LOGGER.fatal("Recibimos un paquete que no supimos manejar (" + tipoPaquete + ")");
                    terminar();
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarChat(String mensaje) {
        LOGGER.info("PQT_CHAT>>" + mensaje);
        try {
            dos.writeByte(PQT_CHAT); // CHAT
            dos.writeUTF(mensaje);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarClick(int x, int y) {
        LOGGER.debug("PQT_CLICK>>" + x + ">>" + y);
        try {
            dos.writeByte(PQT_CLICK); // CHAT
            dos.writeInt(x);
            dos.writeInt(y);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarDesconectar() {
        LOGGER.info("PQT_DESCONECTAR>>");
        try {
            dos.writeByte(PQT_DESCONECTAR); // CHAT
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
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
            LOGGER.fatal(null, ex);
        }

        // Cerramos los recursos abiertos
        try {
            dis.close();
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        try {
            dos.close();
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
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
            if (id_objeto == 0) {
                cliente.getJuego().getUsuario().getInventario().setSlot(slot, null);
                return;
            }
            InventarioSlot nuevoSlot = new InventarioSlot(id_objeto, grhIndex, nombre, cantidad, equipado);
            cliente.getJuego().getUsuario().getInventario().setSlot(slot, nuevoSlot);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void recibirUsuarioCambiaMapa(DataInputStream dis) {
        try {
            int numMapa = dis.readInt();
            cliente.getJuego().cargarMapa(numMapa);
            if (!cliente.isJugando()) {
                cliente.setJugando(true);
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void recibirUsuarioNombre(DataInputStream dis) {
        try {
            cliente.getJuego().getUsuario().setNombre(dis.readUTF());

        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void recibirUsuarioPosicion(DataInputStream dis) {
        try {
            int numMapa = dis.readInt();
            int x = dis.readInt();
            int y = dis.readInt();
            int heading = dis.readInt();

            LOGGER.debug("PQT_USUARIO_POSICION<<"
                    + numMapa + "<<" + x + "<<" + y + "<<" + heading);

            Orientacion orientacion = Orientacion.valueOf(heading);
            Usuario usuario = cliente.getJuego().getUsuario();

            Baldosa antiguaBaldosa = cliente.getJuego().getMapa().getBaldosa(usuario.getPosicion());
            if (antiguaBaldosa != null) {
                if (antiguaBaldosa.getCharindex() == 1) {
                    antiguaBaldosa.setCharindex(0);
                }
            }

            usuario.setPosicion(x, y);
            Baldosa nuevaBaldosa = cliente.getJuego().getMapa().getBaldosa(x, y);
            nuevaBaldosa.setCharindex(1);

            cliente.getMotorGrafico().getPersonaje(1).setPosicion(x, y);

        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
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
            LOGGER.fatal(null, ex);
        }
    }

    public void recibirUsuarioExperiencia(DataInputStream dis) {
        try {
            Usuario user = cliente.getJuego().getUsuario();
            user.setNivel(dis.readInt());
            user.setExperienciaActual(dis.readInt());
            user.setExperienciaSiguienteNivel(dis.readInt());
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void recibirMundoReproducirAnimacion(DataInputStream dis) {
        try {
            int animacion = dis.readInt();
            int x = dis.readInt();
            int y = dis.readInt();
            Baldosa baldosa = cliente.getJuego().getMapa().getBaldosa(x, y);
            if (baldosa != null) {
                baldosa.setCapa(3, new Animacion((short) animacion, false));
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void recibirPersonajeCrear(DataInputStream dis) {
        try {
            int charindex = dis.readInt();
            int heading = dis.readInt();
            int x = dis.readInt();
            int y = dis.readInt();
            int cuerpo = dis.readInt();
            int cabeza = dis.readInt();
            int arma = dis.readInt();
            int escudo = dis.readInt();
            int casco = dis.readInt();

            LOGGER.debug("PQT_PERSONAJE_CREAR<<" + charindex
                    + "<<" + heading + "<<" + x + "<<" + y + "<<" + cuerpo
                    + "<<" + cabeza + "<<" + arma + "<<" + escudo + "<<" + casco);

            cliente.getMotorGrafico().crearPersonaje(
                    charindex,
                    "", x, y, Orientacion.valueOf(heading),
                    cabeza, cuerpo, casco, arma, escudo);

            cliente.getJuego().getMapa().getBaldosa(x, y).setCharindex(charindex);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void recibirPersonajeAnimacion(DataInputStream dis) {
        try {
            int charindex = dis.readInt();
            int efecto = dis.readInt();
            int repeticiones = dis.readInt();

            LOGGER.debug("PQT_PERSONAJE_ANIMACION<<" + charindex
                    + "<<" + efecto + "<<" + repeticiones);

            if (efecto == 0) {
                cliente.getMotorGrafico().getPersonaje(charindex).setEfecto(null);
            } else {
                cliente.getMotorGrafico().getPersonaje(charindex).setEfecto(efecto);
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void recibirPersonajeCambiar(DataInputStream dis) {
        try {
            int charindex = dis.readInt();
            int heading = dis.readInt();
            int cuerpo = dis.readInt();
            int cabeza = dis.readInt();
            int arma = dis.readInt();
            int escudo = dis.readInt();
            int casco = dis.readInt();

            LOGGER.debug("PQT_PERSONAJE_CAMBIAR<<" + charindex
                    + "<<" + heading + "<<" + cuerpo + "<<" + cabeza
                    + "<<" + arma + "<<" + escudo + "<<" + casco);

            cliente.getMotorGrafico().cambiarPersonaje(charindex,
                    Orientacion.valueOf(heading), cabeza, cuerpo, casco, arma, escudo);

        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void recibirPersonajeQuitar(DataInputStream dis) {
        try {
            int charindex = dis.readInt();

            LOGGER.debug("PQT_PERSONAJE_QUITAR<<" + charindex);

            cliente.getMotorGrafico().getPersonaje(charindex).setActivo(false);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void recibirPersonajeCaminar(DataInputStream dis) {
        try {
            int charindex = dis.readInt();
            int heading = dis.readInt();

            LOGGER.debug("PQT_PERSONAJE_CAMINAR<<" + charindex
                    + "<<" + heading);

            cliente.getJuego().personajeDarPaso(charindex, Orientacion.valueOf(heading));
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void recibirMundoReproducirSonido(DataInputStream dis) {
        try {
            int sonido = dis.readInt();
            int x = dis.readInt();
            int y = dis.readInt();

            LOGGER.debug("PQT_MUNDO_REPRODUCIR_SONIDO<<" + sonido
                    + "<<" + x + "<<" + y);

            float xf = 0.0f + ((float) (x - cliente.getJuego().getUsuario().getPosicion().x())) / 4;
            float yf = 0.0f + ((float) (y - cliente.getJuego().getUsuario().getPosicion().y())) / 4;

            Sonido.reproducirSonido(sonido, xf, yf);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarUsuarioCaminar(Orientacion orientacion) {
        LOGGER.debug("PQT_USUARIO_CAMINAR>>" + orientacion.valor());
        try {
            dos.writeByte(PQT_USUARIO_CAMINAR);
            dos.writeByte(orientacion.valor());
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarUsuarioCambiarDireccion(Orientacion orientacion) {
        LOGGER.debug("PQT_USUARIO_CAMBIAR_DIRECCION>>" + orientacion.valor());
        try {
            dos.writeByte(PQT_USUARIO_CAMBIAR_DIRECCION);
            dos.writeByte(orientacion.valor());
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarUsuarioGolpea() {
        LOGGER.debug("PQT_USUARIO_GOLPEA>>");
        try {
            dos.writeByte(PQT_USUARIO_GOLPEA);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarUsuarioEquiparSlot(int invslot) {
        LOGGER.debug("PQT_USUARIO_EQUIPAR_SLOT>>" + invslot);
        try {
            dos.writeByte(PQT_USUARIO_EQUIPAR_SLOT);
            dos.writeInt(invslot);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarUsuarioUsarItem(int invslot) {
        LOGGER.debug("PQT_USUARIO_USAR_ITEM>>" + invslot);
        try {
            dos.writeByte(PQT_USUARIO_USAR_OBJETO);
            dos.writeInt(invslot);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarUsuarioTirarObjeto(int invslot, int cantidad) {
        LOGGER.debug("PQT_USUARIO_TIRAR_OBJETO>>" + invslot + ">>" + cantidad);
        try {
            dos.writeByte(PQT_USUARIO_TIRAR_OBJETO);
            dos.writeInt(invslot);
            dos.writeInt(cantidad);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public void enviarUsuarioAgarrarObjeto() {
        LOGGER.debug("PQT_USUARIO_AGARRAR_OBJETO>>");
        try {
            dos.writeByte(PQT_USUARIO_AGARRAR_OBJETO);
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    protected boolean recibirMundoObjeto() {
        try {
            int x = dis.readInt();
            int y = dis.readInt();
            int objIndex = dis.readInt();
            int grhIndex = dis.readInt();
            int cantidad = dis.readInt();
            String nombre = dis.readUTF();

            LOGGER.debug("PQT_MUNDO_OBJETO<<" + x + "<<" + y + "<<" + objIndex
                    + "<<" + grhIndex + "<<" + cantidad + "<<" + nombre);

            Baldosa b = cliente.getJuego().getMapa().getBaldosa(x, y);
            if (b == null) {
                return false;
            }

            if (objIndex == 0) {
                b.setObjeto(null);
                return true;
            }

            Objeto obj = new Objeto(objIndex, grhIndex, nombre, cantidad);
            b.setObjeto(obj);
            return true;
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        return false;
    }

    protected boolean recibirMundoBaldosaBloqueada() {
        try {
            int x = dis.readInt();
            int y = dis.readInt();
            boolean bloq = dis.readBoolean();

            LOGGER.debug("PQT_MUNDO_BALDOSA_BLOQUEADA<<" + x + "<<" + y + "<<" + bloq);

            Baldosa b = cliente.getJuego().getMapa().getBaldosa(x, y);
            if (b == null) {
                return false;
            }
            b.setBloqueado(bloq);
            return true;
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        return false;
    }

}
