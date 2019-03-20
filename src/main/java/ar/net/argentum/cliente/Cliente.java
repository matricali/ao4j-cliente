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

import ar.net.argentum.cliente.interfaz.IInterfaz;
import ar.net.argentum.cliente.motor.MotorGrafico;
import ar.net.argentum.cliente.motor.gamedata.GameData;
import ar.net.argentum.cliente.protocolo.ConexionConServidor;
import java.io.IOException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.system.MemoryUtil.*;

/**
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

    private GameData game;
    private ConexionConServidor conexion = null;
    private final MotorGrafico motor;

    private boolean jugando = false;
    private long window;

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
        // this.window = glfwCreateWindow(800, 600, "Argentum Online", glfwGetPrimaryMonitor(), NULL);
        this.window = glfwCreateWindow(800, 600, "Argentum Online", NULL, NULL);

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
                if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                    glfwSetWindowShouldClose(window, true);
                }

                motor.keyEvents(window, key, scancode, action, mods);
//                interfaz.keyEvents(window, key, scancode, action, mods);
            }
        };

        glfwSetKeyCallback(window, keyCallback);

//        // Get the thread stack and push a new frame
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            IntBuffer pWidth = stack.mallocInt(1); // int*
//            IntBuffer pHeight = stack.mallocInt(1); // int*
//
//            // Get the window size passed to glfwCreateWindow
//            glfwGetWindowSize(window, pWidth, pHeight);
//
//            // Get the resolution of the primary monitor
//            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//
//            // Center the window
//            glfwSetWindowPos(
//                    window,
//                    (vidmode.width() - pWidth.get(0)) / 2,
//                    (vidmode.height() - pHeight.get(0)) / 2
//            );
//        } // the stack frame is popped automatically
        // Mostramos la ventana
        glfwShowWindow(window);

        this.game = GameData.getInstancia();

        // Iniciamos la carga de los datos del juego
        game.initialize();

//        // Agregamos un evento al cerrar la ventana
//        ventana.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                if (conexion != null) {
//                    conexion.terminar();
//                }
//            }
//        });
        // Iniciamos el motor grafico
        this.motor = new MotorGrafico(this, window, game);
        motor.iniciar();

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

        // Terminamos GLFW y liberamos el callback de error
        glfwTerminate();
        errorCallback.free();
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
    public GameData getJuego() {
        return this.game;
    }
}
