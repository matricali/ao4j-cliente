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

import ar.net.argentum.cliente.motor.IInterfaz;
import ar.net.argentum.cliente.motor.MotorGrafico;
import ar.net.argentum.cliente.protocolo.ConexionConServidor;
import ar.net.argentum.cliente.sonido.Sonido;
import java.io.IOException;
import java.nio.DoubleBuffer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Cliente de Argentum Online
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Cliente implements ClienteArgentum {

    protected static final Logger LOGGER = Logger.getLogger(ConexionConServidor.class);
    protected static Cliente instancia;

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        Cliente cliente = Cliente.getCliente();
    }

    public static Cliente getCliente() {
        if (null == instancia) {
            instancia = new Cliente();
        }
        return instancia;
    }

    /**
     * This error callback will simply print the error to
     * <code>System.err</code>.
     */
    private static GLFWErrorCallback errorCallback
            = GLFWErrorCallback.createPrint(System.err);

    /**
     * This key callback will check if ESC is pressed and will close the window
     * if it is pressed.
     */
    private GLFWKeyCallback keyCallback;

    private Juego juego;
    private ConexionConServidor conexion = null;
    private final MotorGrafico motor;

    private boolean jugando = false;
    private long window;
    private GLFWMouseButtonCallback mouseCallback;

    private int ancho = 800;
    private int alto = 600;

    /**
     * Cliente de Argentum Online
     *
     * @author Jorge Matricali <jorgematricali@gmail.com>
     */
    private Cliente() {

        // Establecemos una devolución de llamada de error.
        // Imprimimos todo en la consola de errores.
        glfwSetErrorCallback(errorCallback);

        // Inicializar GLFW. La mayoria de las funciones GLFW no funcionan 
        // sin antes hacer esto.
        if (!glfwInit()) {
            throw new IllegalStateException("No se pudo inicializar GLFW");
        }

        // Configuraramos el modo de video GLFW con OPENGL3C
        glfwDefaultWindowHints(); // opcional
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        // OpenGL 3.2 Core Profile
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        }
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        // Creamos la ventana
        // this.window = glfwCreateWindow(ancho, alto, "Argentum Online", glfwGetPrimaryMonitor(), NULL);
        this.window = glfwCreateWindow(ancho, alto, "Argentum Online", NULL, NULL);

        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Ocurrio un error al crear la ventana GLFW");
        }

        glfwMakeContextCurrent(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        this.keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    if (isJugando()) {
                        conexion.enviarChat("/salir");
                        return;
                    }
                    glfwSetWindowShouldClose(window, true);
                    return;
                }

                motor.entradaTeclado(window, key, scancode, action, mods);
            }
        };

        this.mouseCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    DoubleBuffer cx = stack.mallocDouble(1);
                    DoubleBuffer cy = stack.mallocDouble(1);

                    glfwGetCursorPos(window, cx, cy);

                    int x = (int) cx.get(0);
                    int y = (int) cy.get(0);

                    motor.entradaMouse(window, x, y, button, action, mods);
                }
            }

        };

        glfwSetKeyCallback(window, keyCallback);
        glfwSetMouseButtonCallback(window, mouseCallback);

        // Mostramos la ventana
        glfwShowWindow(window);

        this.juego = new Juego(this);

        // Iniciamos el sonido
        Sonido.iniciar();

        // Iniciamos la carga de los recursos del juego
        Recursos.cargar();

        // Iniciamos el motor grafico
        this.motor = new MotorGrafico(this, window, juego);
        // motor.iniciar(800, 600, 15, 169, 544, 416);
        motor.iniciar(800, 600);

        // Cerramos la conexion
        if (conexion != null) {
            if (jugando) {
                conexion.enviarDesconectar();
            }
            conexion.detener();
        }

        // Liberamos la ventana y los callbacks
        glfwDestroyWindow(window);

        keyCallback.free();
        mouseCallback.free();

        // Terminamos GLFW y liberamos el callback de error
        glfwTerminate();

        errorCallback.free();

        // Destruimos el contexto de sonido
        Sonido.destruir();
    }

    @Override
    public void conectar(String servidor, int puerto, String username, String password) {
        LOGGER.info("Conectando...");
        this.conexion = new ConexionConServidor(this, servidor, puerto, username, password);
        conexion.start();
    }

    @Override
    public ConexionConServidor getConexion() {
        return conexion;
    }

    @Override
    public MotorGrafico getMotorGrafico() {
        return motor;
    }

    @Override
    public boolean isJugando() {
        return jugando;
    }

    @Override
    public void setJugando(boolean jugando) {
        this.jugando = jugando;
    }

    @Override
    public IInterfaz getInterfaz() {
        return this.motor.getInterfaz();
    }

    @Override
    public Juego getJuego() {
        return this.juego;
    }
}
