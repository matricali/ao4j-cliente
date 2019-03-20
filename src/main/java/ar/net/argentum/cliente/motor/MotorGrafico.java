package ar.net.argentum.cliente.motor;

import ar.net.argentum.cliente.ClienteArgentum;
import ar.net.argentum.cliente.motor.user.Usuario;
import ar.net.argentum.cliente.motor.gamedata.GameData;
import ar.net.argentum.cliente.motor.gamedata.Animacion;
import ar.net.argentum.cliente.motor.gamedata.Sprite;
import ar.net.argentum.cliente.motor.surface.ISurface;
import ar.net.argentum.cliente.motor.surface.SurfaceRasta;
import ar.net.argentum.cliente.motor.user.Orientacion;
import ar.net.argentum.cliente.interfaz.GUI;
import ar.net.argentum.cliente.interfaz.IInterfaz;
import ar.net.argentum.cliente.motor.gamedata.AnimArma;
import ar.net.argentum.cliente.motor.gamedata.AnimCabeza;
import ar.net.argentum.cliente.motor.gamedata.AnimCuerpo;
import ar.net.argentum.cliente.motor.gamedata.AnimEscudo;
import ar.net.argentum.cliente.motor.gamedata.Baldosa;
import ar.net.argentum.cliente.motor.gamedata.Posicion;
import org.apache.log4j.Logger;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Motor grafico 2D, basado en Argentum Online Original
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class MotorGrafico {

    public static final Logger LOGGER = Logger.getLogger(MotorGrafico.class);
    public static final int TILE_PIXEL_WIDTH = 32;
    public static final int TILE_PIXEL_HEIGHT = 32;

    private final ClienteArgentum cliente;
    ISurface surface;
    GameData game;
    IInterfaz interfaz;
    Renderizador render;

    private long ventana;

    final int XMaxMapSize = 100;
    final int XMinMapSize = 1;
    final int YMaxMapSize = 100;
    final int YMinMapSize = 1;

    final float engineBaseSpeed = 0.018f;

    /**
     * Cuantos tiles de distancia del personaje queremos dibujar
     */
    final int tileBufferSize = 15; //9;

    final int halfWindowTileWidth = 8;
    final int halfWindowTileHeight = 6;

    final int scrollPixelsPerFrameX = 8;
    final int scrollPixelsPerFrameY = 8;

    private float offsetCounterX = 0;
    private float offsetCounterY = 0;
    private boolean userMoving = false;

    private long lFrameTimer;
    private long framesPerSecCounter;
    private long FPS;
    private long timerTiempoTranscurrido;
    private float timerTicksPerFrame;
    private long timerEndtime;
    private boolean corriendo;
    private FuenteTruetype fuente;

    public Posicion AddToUserPos = new Posicion();
    public Personaje[] personajes = new Personaje[10000 + 1];

    Color ambientcolor;

    public MotorGrafico(ClienteArgentum cliente, long ventana, GameData game) {
        this.cliente = cliente;
        this.ventana = ventana;
        this.game = game;
        this.surface = new SurfaceRasta();
//        this.fuente = new FuenteTruetype("recursos/fuentes/FreeSans.ttf");

        surface.initialize();

//        fuente.iniciar();
        this.ambientcolor = new Color(1.0f, 1.0f, 1.0f);
    }

    public void iniciar() {

        // Creamos la instancia del renderizador
        this.render = new RenderizadorOpenGL32(ventana, surface, 800, 600, 15, 169, 544, 416);

        // Lo configuramos
        render.iniciar();

        // Creamos la interfaz grafica
        this.interfaz = new GUI(cliente, ventana, game, surface);

        for (int i = 1; i <= 10000; i++) {
            personajes[i] = new Personaje();
            personajes[i].setActivo(false);
        }

//        // Cargamos el mapa
//        game.cargarMapa(4);
//
//        // Creamos un usuario fake
//        Usuario user = game.getUsuario();
//
//        // Posicion del usuario fake
//        int x = 50;
//        int y = 40;
//        game.getBaldosa(x, y).setCharindex((short) crearPersonaje(1, 124, Orientacion.SUR, x, y));
//        game.getBaldosa(x-1, y-3).setCharindex((short) crearPersonaje(2, 128, Orientacion.SUR, x-1, y-3));
//
//        Objeto obj1 = new Objeto(1, 508, "Horquilla", 1);
//        Objeto obj2 = new Objeto(2, 520, "TEST", 20);
//        game.getBaldosa(x + 2, y + 2).setObjeto(obj1);
//
//        personajesActualizarTodos();
//        user.setPosicion(x, y);
//        user.setNombre("R4ST4");
//        user.setMinHP(50);
//        user.setMaxHP(120);
//        user.getInventario().setSlot(1, obj1);
//        user.getInventario().setSlot(7, obj2);
        this.corriendo = true;
        cliente.setJugando(false);

        loop();
        destruir();
    }

    public void detener() {
        this.corriendo = false;
    }

    private void loop() {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(ventana)) {
            render();
        }
    }

    private void render() {
        // Limpiamos la pantalla
        render.limpiarPantalla();

        // Dibujamos el juegoo
        if (cliente.isJugando()) {
            render.iniciarDibujado(ventana);
            dibujarSiguienteCuadro();
            render.finalizarDibujado();
        }
        // Dibujamos la interfaz gráfica
        interfaz.dibujarInterfaz();

//        fuente.iniciarDibujado();
//        fuente.dibujarTexto(12, 12, 0, Color.BLACK, FPS+" FPS");
//        fuente.dibujarTexto(10, 10, 0, Color.WHITE, FPS+" FPS");
//        fuente.terminarDibujado();
        // Actualizamos los events de entrada (teclado y mouse)
        glfwPollEvents();

        // Dibujamos el buffer en la ventana
        glfwSwapBuffers(ventana);

        // Actualizamos el contador de FPS
        if (getTime() >= lFrameTimer + 1000) {
            lFrameTimer = getTime();
            FPS = framesPerSecCounter;
            framesPerSecCounter = 0;
        }

        framesPerSecCounter++;
        timerTiempoTranscurrido = getElapsedTime();
        timerTicksPerFrame = (timerTiempoTranscurrido * engineBaseSpeed);
    }

    private long getTime() {
        return System.nanoTime() / 1000000;
    }

    private long getElapsedTime() {

        long startTime = getTime();
        long ms = (startTime - timerEndtime);
        timerEndtime = getTime();
        return ms;
    }

    private void dibujarSiguienteCuadro() {
        final Usuario user = game.getUsuario();

        if (userMoving) {
            if (0 != AddToUserPos.x()) {
                offsetCounterX = offsetCounterX - scrollPixelsPerFrameX * AddToUserPos.x() * timerTicksPerFrame;
                if (Math.abs(offsetCounterX) >= Math.abs(TILE_PIXEL_WIDTH * AddToUserPos.x())) {
                    offsetCounterX = 0;
                    AddToUserPos.x(0);
                    userMoving = false;
                }
            }

            if (0 != AddToUserPos.y()) {
                offsetCounterY = offsetCounterY - scrollPixelsPerFrameY * AddToUserPos.y() * timerTicksPerFrame;
                if (Math.abs(offsetCounterY) >= Math.abs(TILE_PIXEL_HEIGHT * AddToUserPos.y())) {
                    offsetCounterY = 0;
                    AddToUserPos.y(0);
                    userMoving = false;
                }
            }
        }

        //OffsetCounterY = OffsetCounterY - 2;
        dibujarPantalla(user.getPosicion().x() - AddToUserPos.x(),
                user.getPosicion().y() - AddToUserPos.y(),
                (int) (offsetCounterX), (int) (offsetCounterY));
    }

    private void moverPantalla(Orientacion orientacion) {
        int x = 0, y = 0, tX = 0, tY = 0;
        switch (orientacion) {
            case NORTE:
                y = -1;
                break;
            case ESTE:
                x = 1;
                break;
            case SUR:
                y = 1;
                break;
            case OESTE:
                x = -1;
                break;
        }

        Usuario user = game.getUsuario();
        tX = user.getPosicion().x() + x;
        tY = user.getPosicion().y() + y;

        if (tX < tileBufferSize || tX > XMaxMapSize - tileBufferSize || tY < tileBufferSize || tY > YMaxMapSize - tileBufferSize) {
            return;
        }
        AddToUserPos.x(x);
        user.getPosicion().x(tX);
        AddToUserPos.y(y);
        user.getPosicion().y(tY);
        this.userMoving = true;
    }

    /**
     * Hacer caminar al personaje un paso en la direccion dada
     *
     * @see MooveCharByHead
     *
     * @param id_personaje
     * @param orientacion
     */
    public void personajeDarPaso(int id_personaje, Orientacion orientacion) {
        int addX = 0;
        int addY = 0;
        int X;
        int Y;
        int nX;
        int nY;

        Personaje personaje = personajes[id_personaje];

        X = personaje.getPosicion().x();
        Y = personaje.getPosicion().y();

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

        nX = X + addX;
        nY = Y + addY;

        /**
         * Movemos la entidad en el mundo. Basicamente sacamos el personaje de
         * la baldosa anterior y luego lo ponemos en la nueva
         */
        try {
            game.getMapa().getBaldosa(X, Y).setCharindex(0);
            game.getMapa().getBaldosa(nX, nY).setCharindex(id_personaje);
        } catch (Exception ex) {

        }

        personaje.setPosicion(nX, nY);
        personaje.setMovimiento(addX, addY, orientacion);
    }

    /**
     * Dibujar una animacion en la pantalla
     *
     * @param grh Animacion que queremos dibujar
     * @param x Posicion de la pantalla en pixeles donde queremos dibujar
     * @param y Posicion de la pantalla en pixeles donde queremos dibujar
     * @param centrado
     * @param animar
     * @param transparente
     * @param color
     */
    private void dibujarAnimacion(Animacion grh, int x, int y, boolean centrado, boolean animar, boolean transparente, Color color) {
        if (!grh.esValido()) {
            return;
        }
        if (grh.getSprite().numFrames == 0) {
            return;
        }
        if (animar) {
            if (grh.isStarted()) {
                grh.animar(timerTiempoTranscurrido);
            }
        }

        Sprite graficoActual = grh.getCuadroActual();

        if (!graficoActual.esValido()) {
            return;
        }

        if (centrado) {
            if (graficoActual.tileWidth != 1) {
                x = x - (int) (graficoActual.tileWidth * TILE_PIXEL_WIDTH / 2) + TILE_PIXEL_WIDTH / 2;
            }

            if (graficoActual.tileHeight != 1) {
                y = y - (int) (graficoActual.tileHeight * TILE_PIXEL_HEIGHT) + TILE_PIXEL_HEIGHT;
            }
        }

        render.dibujarSprite(graficoActual, x, y, transparente, color);
    }

    private void dibujarPantalla(int tileX, int tileY, int pixelOffsetX, int pixelOffsetY) {
        int y;
        int x;
        int screenMinY;
        int screenMaxY;
        int screenMinX;
        int screenMaxX;
        int minY;
        int maxY;
        int minX;
        int maxX;
        int screenX = 0;
        int screenY = 0;
        int minXOffset = 0;
        int minYOffset = 0;

        screenMinY = tileY - halfWindowTileHeight;
        screenMaxY = tileY + halfWindowTileHeight;
        screenMinX = tileX - halfWindowTileWidth;
        screenMaxX = tileX + halfWindowTileWidth;

        minY = screenMinY - tileBufferSize;
        maxY = screenMaxY + tileBufferSize;
        minX = screenMinX - tileBufferSize;
        maxX = screenMaxX + tileBufferSize;

        if (minY < XMinMapSize) {
            minYOffset = YMinMapSize - minY;
            minY = YMinMapSize;
        }

        if (maxY > YMaxMapSize) {
            maxY = YMaxMapSize;
        }

        if (minX < XMinMapSize) {
            minXOffset = XMinMapSize - minX;
            minX = XMinMapSize;
        }

        if (maxX > XMaxMapSize) {
            maxX = XMaxMapSize;
        }

        if (screenMinY > YMinMapSize) {
            screenMinY = screenMinY - 1;
        } else {
            screenMinY = 1;
            screenY = 1;
        }

        if (screenMaxY < YMaxMapSize) {
            screenMaxY = screenMaxY + 1;
        }

        if (screenMinX > XMinMapSize) {
            screenMinX = screenMinX - 1;
        } else {
            screenMinX = 1;
            screenX = 1;
        }

        if (screenMaxX < XMaxMapSize) {
            screenMaxX = screenMaxX + 1;
        }

        for (y = screenMinY; y <= screenMaxY; y++) {
            for (x = screenMinX; x <= screenMaxX; x++) {
                // Dibujamos la primer capa
                dibujarAnimacion(game.getMapa().getBaldosa(x, y).getCapa(1),
                        (screenX - 1) * TILE_PIXEL_WIDTH + pixelOffsetX,
                        (screenY - 1) * TILE_PIXEL_HEIGHT + pixelOffsetY, true, true, false, ambientcolor);

                // Dibujamos la segunda capa
                if (game.getMapa().getBaldosa(x, y).getCapa(2).esValido()) {
                    dibujarAnimacion(game.getMapa().getBaldosa(x, y).getCapa(2),
                            (screenX - 1) * TILE_PIXEL_WIDTH + pixelOffsetX,
                            (screenY - 1) * TILE_PIXEL_HEIGHT + pixelOffsetY, true, true, false, ambientcolor);
                }
                screenX++;
            }
            screenX = screenX - x + screenMinX;
            screenY++;
        }

        screenY = minYOffset - tileBufferSize;
        for (y = minY; y <= maxY; y++) {
            screenX = minXOffset - tileBufferSize;
            for (x = minX; x <= maxX; x++) {
                int xd = screenX * 32 + pixelOffsetX;
                int yd = screenY * 32 + pixelOffsetY;
                // Si hay un objeto arrojado en el suelo en esta posicion, entonces lo dibujamos.
                if (game.getMapa().getBaldosa(x, y).getAnimObjecto().esValido()) {
                    dibujarAnimacion(game.getMapa().getBaldosa(x, y).getAnimObjecto(), xd, yd, true, true, false, ambientcolor);
                } else {

                }

                // Si hay un personaje parado en esta posicion, entonces lo dibujamos.
                if (game.getMapa().getBaldosa(x, y).getCharindex() > 0) {
                    dibujarPersonaje(game.getMapa().getBaldosa(x, y).getCharindex(), xd, yd);
                }

                // Dibujamos la capa 3
                if (game.getMapa().getBaldosa(x, y).getCapa(3).esValido()) {
                    dibujarAnimacion(game.getMapa().getBaldosa(x, y).getCapa(3), xd,
                            yd, true, true, false, ambientcolor);
                }
                screenX++;
            }
            screenY++;
        }

        screenY = minYOffset - tileBufferSize;
        for (y = minY; y <= maxY; y++) {
            screenX = minXOffset - tileBufferSize;
            for (x = minX; x <= maxX; x++) {
                int xd = screenX * 32 + pixelOffsetX;
                int yd = screenY * 32 + pixelOffsetY;
                // Dibujamos el techo
                if (game.getMapa().getBaldosa(x, y).getCapa(4).esValido()) {
                    dibujarAnimacion(game.getMapa().getBaldosa(x, y).getCapa(4), xd,
                            yd, true, true, false, ambientcolor);
                }
                screenX++;
            }
            screenY++;
        }

        // drawText(FPS + " FPS", 454, 8, ambientcolor, 1, false);
    }

    /**
     * Dibujar un personaje en su posicion en el mapa
     *
     * @param CharIndex ID del personaje a dibujar
     * @param pixelOffsetX Offset horizontal en pixeles
     * @param pixelOffsetY Offset vertical en pixeles
     */
    public void dibujarPersonaje(int CharIndex, int pixelOffsetX, int pixelOffsetY) {
        dibujarPersonaje(personajes[CharIndex], pixelOffsetX, pixelOffsetY);
    }

    /**
     * Dibujar un personaje en su posicion en el mapa
     *
     * @param personaje Personaje a dibujar
     * @param pixelOffsetX Offset horizontal en pixeles
     * @param pixelOffsetY Offset vertical en pixeles
     */
    public void dibujarPersonaje(Personaje personaje, int pixelOffsetX, int pixelOffsetY) {
        boolean moved = false;
        int Pos;
        String line;

        if (personaje.estaMoviendose()) {
            if (personaje.getScrollDirectionX() != 0) {
                personaje.setMoveOffsetX(personaje.getMoveOffsetX() + scrollPixelsPerFrameX * Sgn(personaje.getScrollDirectionX()) * timerTicksPerFrame);

                if (personaje.getCuerpo().getAnimacion(personaje.getOrientacion()).getVelocidad() > 0.0f) {
                    personaje.getCuerpo().getAnimacion(personaje.getOrientacion()).setStarted(true);
                }
                personaje.getArma().getAnimacion(personaje.getOrientacion()).setStarted(true);
                personaje.getEscudo().getAnimacion(personaje.getOrientacion()).setStarted(true);

                moved = true;

                if ((Sgn(personaje.getScrollDirectionX()) == 1 && personaje.getMoveOffsetX() >= 0)
                        || (Sgn(personaje.getScrollDirectionX()) == -1 && personaje.getMoveOffsetX() <= 0)) {
                    personaje.setMoveOffsetX(0);
                    personaje.setScrollDirectionX((short) 0);
                }
            }

            if (personaje.getScrollDirectionY() != 0) {
                personaje.setMoveOffsetY(personaje.getMoveOffsetY() + scrollPixelsPerFrameY * Sgn(personaje.getScrollDirectionY()) * timerTicksPerFrame);

                if (personaje.getCuerpo().getAnimacion(personaje.getOrientacion()).getVelocidad() > 0.0f) {
                    personaje.getCuerpo().getAnimacion(personaje.getOrientacion()).setStarted(true);
                }
                personaje.getArma().getAnimacion(personaje.getOrientacion()).setStarted(true);
                personaje.getEscudo().getAnimacion(personaje.getOrientacion()).setStarted(true);

                moved = true;

                if ((Sgn(personaje.getScrollDirectionY()) == 1 && personaje.getMoveOffsetY() >= 0)
                        || (Sgn(personaje.getScrollDirectionY()) == -1 && personaje.getMoveOffsetY() <= 0)) {
                    personaje.setMoveOffsetY(0);
                    personaje.setScrollDirectionY((short) 0);
                }
            }
        }

        if (!moved) {
            // Parar movimiento y reiniciar animaciones del personaje
            personaje.pararMovimiento();
        }

        pixelOffsetX = pixelOffsetX + (int) personaje.getMoveOffsetX();
        pixelOffsetY = pixelOffsetY + (int) personaje.getMoveOffsetY();

        if (personaje.getCabeza().getAnimacion(personaje.getOrientacion()).esValido()) {
            if (personaje.esVisible()) {

                int offsetCabezaX = personaje.getCuerpo().getOffsetCabeza().x();
                int offsetCabezaY = personaje.getCuerpo().getOffsetCabeza().y();

                if (personaje.getCuerpo().getAnimacion(personaje.getOrientacion()).esValido()) {
                    dibujarAnimacion(personaje.getCuerpo().getAnimacion(personaje.getOrientacion()),
                            pixelOffsetX, pixelOffsetY, true, true, false, ambientcolor);
                }

                if (personaje.getCabeza().getAnimacion(personaje.getOrientacion()).esValido()) {
                    dibujarAnimacion(personaje.getCabeza().getAnimacion(personaje.getOrientacion()),
                            pixelOffsetX + offsetCabezaX, pixelOffsetY + offsetCabezaY, true, false, false, ambientcolor);

                    if (personaje.getCasco().getAnimacion(personaje.getOrientacion()).esValido()) {
                        dibujarAnimacion(personaje.getCasco().getAnimacion(personaje.getOrientacion()),
                                pixelOffsetX + offsetCabezaX, pixelOffsetY + offsetCabezaY - 34, true, false, false, ambientcolor);
                    }

                    if (personaje.getArma().getAnimacion(personaje.getOrientacion()).esValido()) {
                        dibujarAnimacion(personaje.getArma().getAnimacion(personaje.getOrientacion()),
                                pixelOffsetX, pixelOffsetY, true, true, false, ambientcolor);
                    }

                    if (personaje.getEscudo().getAnimacion(personaje.getOrientacion()).esValido()) {
                        dibujarAnimacion(personaje.getEscudo().getAnimacion(personaje.getOrientacion()),
                                pixelOffsetX, pixelOffsetY, true, true, false, ambientcolor);
                    }

                    if (personaje.getNombre().length() > 0) {
                        // @TODO: Dibujar el nombre
                        //drawText(line, PixelOffsetX - (line.length() * 4) + 28, PixelOffsetY + 30, color, 1, false);
                    }
                }
            }
        } else {
            if (personaje.getCuerpo().getAnimacion(personaje.getOrientacion()).esValido()) {
                dibujarAnimacion(personaje.getCuerpo().getAnimacion(personaje.getOrientacion()),
                        pixelOffsetX, pixelOffsetY, true, true, false, ambientcolor);
            }
        }
    }

    /**
     * Función signo. {@link https://es.wikipedia.org/wiki/Funci%C3%B3n_signo}
     *
     * @param number
     * @return
     */
    private int Sgn(short number) {
        if (number == 0) {
            return 0;
        }
        return (number / Math.abs(number));
    }

    public void destruir() {
        LOGGER.info("Destruyendo motor grafico...");
        this.surface.destruir();
    }

    /**
     * Procesamos los eventos de teclado relacionados al juego
     *
     * @param window
     * @param key
     * @param scancode
     * @param action
     * @param mods
     */
    public void keyEvents(long window, int key, int scancode, int action, int mods) {
        if (cliente.isJugando()) {

            if (key == GLFW_KEY_RIGHT && action != GLFW_RELEASE) {
                if (userMoving) {
                    return;
                }
                usuarioCamina(Orientacion.ESTE);
            }

            if (key == GLFW_KEY_LEFT && action != GLFW_RELEASE) {
                if (userMoving) {
                    return;
                }
                usuarioCamina(Orientacion.OESTE);
            }

            if (key == GLFW_KEY_UP && action != GLFW_RELEASE) {
                if (userMoving) {
                    return;
                }
                usuarioCamina(Orientacion.NORTE);
            }

            if (key == GLFW_KEY_DOWN && action != GLFW_RELEASE) {
                if (userMoving) {
                    return;
                }
                usuarioCamina(Orientacion.SUR);
            }
        }

        interfaz.keyEvents(window, key, scancode, action, mods);
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
        Usuario user = game.getUsuario();

        Posicion nuevaPosicion = calcularPaso(user.getPosicion(), orientacion);
        Baldosa nuevaBaldosa = game.getMapa().getBaldosa(nuevaPosicion.x(), nuevaPosicion.y());

        if (nuevaBaldosa == null) {
            return;
        }

        if (game.getMapa().isPosicionValida(nuevaPosicion) && !user.isParalizado()) {
            cliente.getConexion().enviarUsuarioCaminar(orientacion);
            user.setPosicion(nuevaPosicion);
            personajeDarPaso(1, orientacion);
            moverPantalla(orientacion);
        } else {
            personajes[1].setOrientacion(orientacion);
            cliente.getConexion().enviarUsuarioCambiarDireccion(orientacion);
        }

        game.getUsuario().setPosicion(nuevaPosicion);
        personajesActualizarTodos();
    }

    public int crearPersonaje(int id, String nombre, int x, int y, Orientacion orientacion, int cabeza, int cuerpo, int casco, int arma, int escudo) {
        if (id > game.last_char) {
            game.last_char = id;
        }
        if (personajes[id].isActivo()) {
            return 0;
        }
        personajes[id] = new Personaje(
                nombre,
                Orientacion.SUR,
                new Posicion(x, y),
                new AnimCabeza(game.getCabeza(cabeza)),
                new AnimCuerpo(game.getCuerpo(cuerpo)),
                new AnimCabeza(game.getCasco(casco)),
                new AnimArma(game.getArma(arma)),
                new AnimEscudo(game.getEscudo(escudo)));

        personajes[id].setActivo(true);

        // Actualizamos (?)
        personajesActualizarTodos();

        return id;
    }

    /**
     * Asegurarse que todos los personajes estan ubicados en la baldosa que les
     * corresponde
     *
     * @see RefreshAllChars
     */
    public void personajesActualizarTodos() {
        for (short i = 1; i <= game.getLastChar(); i++) {
            if (personajes[i].isActivo()) {
                game.getMapa().getBaldosa(personajes[i].getPosicion()).setCharindex(i);
            }
        }
    }

    public IInterfaz getInterfaz() {
        return interfaz;
    }

    public Personaje getPersonaje(int charindex) {
        return personajes[charindex];
    }
}
