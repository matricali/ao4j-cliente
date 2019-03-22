package ar.net.argentum.cliente;

import ar.net.argentum.cliente.juego.Usuario;
import ar.net.argentum.cliente.mundo.Baldosa;
import ar.net.argentum.cliente.mundo.Mapa;
import ar.net.argentum.cliente.mundo.Orientacion;
import ar.net.argentum.cliente.mundo.Posicion;
import org.apache.log4j.Logger;

/**
 * Objeto que contiene la informacion y la logica del juego propiamente
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Juego {

    private static final Logger LOGGER = Logger.getLogger(Juego.class);
    /**
     * Instancia del mapa actual
     */
    protected Mapa mapa;
    /**
     * Instancia del usuario
     */
    protected Usuario usuario;
    /**
     * Intancia del cliente
     */
    protected final ClienteArgentum cliente;
    /**
     * Generar nueva instancia del juego
     *
     * @param cliente
     */
    public Juego(ClienteArgentum cliente) {
        this.cliente = cliente;
        this.usuario = new Usuario(this);
    }

    /**
     * @return Instancia del usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Reiniciar el juego
     */
    public void reiniciar() {
        // Reiniciamos la data del usuario
        usuario = new Usuario(this);
    }

    /**
     * @return Intancia del mapa en el que se esta jugando actualmente
     */
    public Mapa getMapa() {
        return mapa;
    }

    /**
     * @return Numero de mapa en el que se esta jugando actualmente
     */
    public int getMapaActual() {
        if (mapa == null) {
            return 0;
        }
        return getMapa().getNumMapa();
    }

    /**
     * Cargar un nuevo mapa
     *
     * @param numMapa
     */
    public void cargarMapa(int numMapa) {
        this.mapa = new Mapa(this, numMapa);
    }

    private Posicion calcularPaso(Posicion posicion, Orientacion orientacion) {
        return calcularPaso(posicion.x(), posicion.y(), orientacion);
    }

    public Posicion calcularPaso(int x, int y, Orientacion orientacion) {
        int addX = 0;
        int addY = 0;

        switch (orientacion) {
            case NORTE:
                addY = -1;
                break;

            case ESTE:
                addX = 1;
                break;

            case SUR:
                addY = 1;
                break;

            case OESTE:
                addX = -1;
                break;
        }

        return new Posicion(x + addX, y + addY);
    }

    public void usuarioCamina(Orientacion orientacion) {
        Usuario user = getUsuario();
        Posicion nuevaPosicion = calcularPaso(user.getPosicion(), orientacion);
        Baldosa nuevaBaldosa = getMapa().getBaldosa(nuevaPosicion.x(), nuevaPosicion.y());
        if (nuevaBaldosa == null) {
            return;
        }
        if (getMapa().isPosicionValida(nuevaPosicion) && !user.isParalizado()) {
            cliente.getMotorGrafico().personajeDarPaso(1, orientacion);
            cliente.getMotorGrafico().moverPantalla(orientacion);
            cliente.getConexion().enviarUsuarioCaminar(orientacion);
        } else {
            cliente.getMotorGrafico().getPersonaje(1).setOrientacion(orientacion);
            cliente.getConexion().enviarUsuarioCambiarDireccion(orientacion);
        }
        cliente.getMotorGrafico().personajesActualizarTodos();
    }

    /**
     * El usuario lanza un golpe
     */
    public void usuarioGolpea() {
        cliente.getConexion().enviarUsuarioGolpea();
    }

}
