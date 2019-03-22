package ar.net.argentum.cliente.motor;

import ar.net.argentum.cliente.ClienteArgentum;
import ar.net.argentum.cliente.Juego;
import ar.net.argentum.cliente.Recursos;
import ar.net.argentum.cliente.fuentes.FuenteTruetypeGL32;
import ar.net.argentum.cliente.fuentes.IFuente;
import ar.net.argentum.cliente.interfaz.GUI;
import ar.net.argentum.cliente.motor.texturas.ITexturas;
import ar.net.argentum.cliente.motor.texturas.TexturasDB;
import ar.net.argentum.cliente.juego.Usuario;
import ar.net.argentum.cliente.mundo.Orientacion;
import ar.net.argentum.cliente.mundo.Posicion;
import org.apache.log4j.Logger;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Motor grafico 2D, basado en Argentum Online Original
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class MotorGrafico {

    public static final Logger LOGGER = Logger.getLogger(MotorGrafico.class);
    /**
     * Ancho de las baldosas (en pixeles)
     */
    public static final int TILE_PIXEL_WIDTH = 32;
    /**
     * Alto de las baldosas (en pixeles)
     */
    public static final int TILE_PIXEL_HEIGHT = 32;
    /**
     * Desplazamiento vertical por cuadro (en pixeles)
     */
    protected final int scrollPixelsPerFrameX = 8;
    /**
     * Desplazamiento horizontal por cuadro (en pixeles)
     */
    protected final int scrollPixelsPerFrameY = 8;
    /**
     * Ancho maximo del mapa (en baldosas)
     */
    protected final int XMaxMapSize = 100;
    /**
     * Ancho minimo del mapa (en baldosas)
     */
    protected final int XMinMapSize = 1;
    /**
     * Alto maximo del mapa (en baldosas)
     */
    protected final int YMaxMapSize = 100;
    /**
     * Alto minimo del mapa (en baldosas)
     */
    protected final int YMinMapSize = 1;
    /**
     * Velocidad del motor grafico
     */
    protected final float engineBaseSpeed = 0.018f;
    /**
     * Instancia del cliente
     */
    protected final ClienteArgentum cliente;
    /**
     * Manejador de texturas
     */
    protected ITexturas texturas;
    /**
     * Manejador de la logica
     */
    protected Juego juego;
    /**
     * Instancia de la interfaz grafica
     */
    protected IInterfaz interfaz;
    /**
     * Instancia del renderizador
     */
    protected Renderizador render;
    /**
     * Handler de la ventana
     */
    protected long ventana;
    /**
     * Distancia de dibujado (en baldosas)
     */
    protected int tileBufferSize;
    /**
     * Vertice lateral izquierdo de la zona de dibujado del juego (en pixeles)
     */
    protected int viewportX;
    /**
     * Vertice superior de la zona de dibujado del juego (en pixeles)
     */
    protected int viewportY;
    /**
     * Ancho de la zona de dibujado del juego (en pixeles)
     */
    protected int viewportAncho;
    /**
     * Alto de la zona de dibujado del juego (en pixeles)
     */
    protected int viewportAlto;
    /**
     * Ancho de la zona de dibujado del juego (en baldosas)
     */
    protected int anchoEnBaldosas;
    /**
     * Ancho de la zona de dibujado del juego (en baldosas)
     */
    protected int altoEnBaldosas;
    /**
     * Mitad horizontal de la zona de dibujado del juego (en baldosas)
     * (anchoEnBaldosas / 2)
     */
    protected int halfWindowTileWidth;
    /**
     * Mitad vertical de la zona de dibujado del juego (en baldosas)
     * (altoEnBaldosas / 2)
     */
    protected int halfWindowTileHeight;
    /**
     * Contador de desplazamiento horizontal
     */
    protected float offsetCounterX = 0;
    /**
     * Contador de desplazamiento vertical
     */
    protected float offsetCounterY = 0;
    /**
     * Nos estamos moviendo?
     */
    protected boolean userMoving = false;
    /**
     * Timer ultimo calculo de cuadros por segundo (FPS)
     */
    protected long lFrameTimer;
    /**
     * Cantidad de frames que se dibujaron antes de la ultima comprobacion de
     * FPS
     */
    protected long fpsContador;
    /**
     * Cantidad de cuadros que se dibujaron en el ultimo segundo
     */
    protected long FPS;
    /**
     * Cantidad de tiempo que transcurrio del ultimo calculo de cuadros por
     * segundo
     */
    protected long fpsTiempoTranscurrido;
    protected float timerTicksPerFrame;
    protected long timerEndtime;
    /**
     * El motor esta corriendo?
     */
    protected boolean corriendo;
    /**
     * Instancia del manejador de fuentes
     */
    protected IFuente fuente;
    /**
     * Contador de desplazamiento
     */
    protected Posicion desplazamiento = new Posicion();
    /**
     * Coleccion de personajes
     */
    protected Personaje[] personajes = new Personaje[10000 + 1];
    /**
     * Charindex de mayor numero que hemos creado
     */
    protected int ultimoCharindex;
    /**
     * Color de luz ambiental
     */
    Color ambientcolor;

    /**
     * Generar una nueva instancia del motor grafico
     *
     * @param cliente Instancia del cliente
     * @param ventana Handler de la ventana
     * @param juego Instancia del juego
     */
    public MotorGrafico(ClienteArgentum cliente, long ventana, Juego juego) {
        this.cliente = cliente;
        this.ventana = ventana;
        this.juego = juego;
        this.ambientcolor = new Color(1.0f, 1.0f, 1.0f);
        this.texturas = new TexturasDB();
        texturas.inicializar();
    }

    /**
     * Iniciamos el motor grafico
     *
     * @param anchoVentana Ancho de la ventana en pixeles
     * @param altoVentana Alto de la ventana en pixeles
     */
    public void iniciar(int anchoVentana, int altoVentana) {
        // El viewport del juego ocupa toda la ventana
        iniciar(anchoVentana, altoVentana, 0, 0, anchoVentana, altoVentana);
    }

    /**
     * Iniciamos el motor grafico
     *
     * @param anchoVentana Ancho de la ventana en pixeles
     * @param altoVentana Alto de la ventana en pixeles
     * @param viewportX Vertice horizontal izquierdo de la zona de dibujado del
     * juego
     * @param viewportY Vertice superior de la zona de dibujado del juego
     * @param viewportAncho Ancho de la zona de dibujado del juego
     * @param viewportAlto Ancho de la zona de dibujado del juego
     */
    public void iniciar(int anchoVentana, int altoVentana, int viewportX, int viewportY, int viewportAncho, int viewportAlto) {

        this.viewportX = viewportX;
        this.viewportY = viewportY;
        this.viewportAlto = viewportAlto;
        this.viewportAncho = viewportAncho;
        this.anchoEnBaldosas = viewportAncho / TILE_PIXEL_WIDTH;
        this.altoEnBaldosas = viewportAlto / TILE_PIXEL_HEIGHT;
        this.halfWindowTileWidth = anchoEnBaldosas / 2;
        this.halfWindowTileHeight = altoEnBaldosas / 2;
        this.tileBufferSize = 2 + (halfWindowTileWidth > halfWindowTileHeight ? halfWindowTileWidth : halfWindowTileHeight);

        // Creamos la instancia del renderizador
        this.render = new RenderizadorOpenGL32(ventana, texturas, anchoVentana, altoVentana, viewportX, viewportY, viewportAncho, viewportAlto);

        // Lo configuramos
        render.iniciar();

        // Iniciamos la fuente
        this.fuente = new FuenteTruetypeGL32("recursos/fuentes/FreeSans.ttf", (RenderizadorOpenGL32) render);

        // Creamos la interfaz grafica
        this.interfaz = new GUI(cliente, ventana, juego, texturas);

        // Inicializamos los personajes
        for (int i = 1; i <= 10000; i++) {
            personajes[i] = new Personaje();
            personajes[i].setActivo(false);
        }

        this.corriendo = true;
        cliente.setJugando(false);
        cliente.conectar("localhost", 7666, "rasta", "1");

        loop();
        destruir();
    }

    /**
     * Detener el bucle principal del motor grafico
     */
    public void detener() {
        this.corriendo = false;
    }

    /**
     * Bucle principal
     */
    private void loop() {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(ventana)) {
            render();
        }
    }

    /**
     * Iniciar rutina de dibujado
     */
    private void render() {
        // Limpiamos la pantalla
        render.limpiarPantalla();

        // Dibujamos el juego
        if (cliente.isJugando()) {
            render.iniciarDibujado(ventana);
            dibujarSiguienteCuadro();
            render.finalizarDibujado();
        }

        // Dibujamos la interfaz gráfica
        interfaz.dibujarInterfaz();

        dibujarTexto(22, 22, 0, Color.BLACK, FPS + " FPS");
        dibujarTexto(20, 20, 0, Color.WHITE, FPS + " FPS");

        // Actualizamos los events de entrada (teclado y mouse)
        glfwPollEvents();

        // Dibujamos el buffer en la ventana
        glfwSwapBuffers(ventana);

        // Actualizamos el contador de FPS
        if (getTimer() >= lFrameTimer + 1000) {
            lFrameTimer = getTimer();
            FPS = fpsContador;
            fpsContador = 0;
        }

        fpsContador++;
        fpsTiempoTranscurrido = getTiempoTranscurrido();
        timerTicksPerFrame = (fpsTiempoTranscurrido * engineBaseSpeed);
    }

    /**
     * @return Obtiene el valor actual del reloj de la maquina virtual en
     * ejecucion
     * @see System.nanoTime
     */
    private long getTimer() {
        return System.nanoTime() / 1000000;
    }

    /**
     * @return Tiempo transcurrido desde la ultima comprobacion de cuadros por
     * segundo
     */
    private long getTiempoTranscurrido() {

        long inicio = getTimer();
        long ms = (inicio - timerEndtime);
        timerEndtime = getTimer();
        return ms;
    }

    /**
     * Calcula el siguiente cuadro que se va a dibujar. Esta rutina actualiza
     * todas las animaciones un cuadro.
     */
    private void dibujarSiguienteCuadro() {
        final Usuario user = juego.getUsuario();

        // Nos estabamos moviendo?
        if (userMoving) {
            // Nos estabamos moviendo horizontalmente?
            if (0 != desplazamiento.x()) {
                offsetCounterX = offsetCounterX - scrollPixelsPerFrameX * desplazamiento.x() * timerTicksPerFrame;
                // Nos terminamos de mover?
                if (Math.abs(offsetCounterX) >= Math.abs(TILE_PIXEL_WIDTH * desplazamiento.x())) {
                    // Nos terminamos de mover, reiniciamos los contadores
                    offsetCounterX = 0;
                    desplazamiento.x(0);
                    userMoving = false;
                }
            }

            // Nos estabamos moviendo verticalmente?
            if (0 != desplazamiento.y()) {
                offsetCounterY = offsetCounterY - scrollPixelsPerFrameY * desplazamiento.y() * timerTicksPerFrame;
                // Nos terminamos de mover?
                if (Math.abs(offsetCounterY) >= Math.abs(TILE_PIXEL_HEIGHT * desplazamiento.y())) {
                    // Nos terminamos de mover, reiniciamos los contadores
                    offsetCounterY = 0;
                    desplazamiento.y(0);
                    userMoving = false;
                }
            }
        }

        dibujarPantalla(user.getPosicion().x() - desplazamiento.x(),
                user.getPosicion().y() - desplazamiento.y(),
                (int) (offsetCounterX), (int) (offsetCounterY));
    }

    /**
     * Desplazar pantalla en la orientacion dada
     *
     * @param orientacion
     */
    public void moverPantalla(Orientacion orientacion) {
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

        Usuario user = juego.getUsuario();
        tX = user.getPosicion().x() + x;
        tY = user.getPosicion().y() + y;

        if (tX < tileBufferSize || tX > XMaxMapSize - tileBufferSize || tY < tileBufferSize || tY > YMaxMapSize - tileBufferSize) {
            return;
        }
        desplazamiento.x(x);
        user.getPosicion().x(tX);
        desplazamiento.y(y);
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
            juego.getMapa().getBaldosa(X, Y).setCharindex(0);
            juego.getMapa().getBaldosa(nX, nY).setCharindex(id_personaje);
        } catch (Exception ex) {
            LOGGER.error(null, ex);
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
                grh.animar(fpsTiempoTranscurrido);
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

    /**
     * Dibujamos el cuadro actual
     *
     * @param tileX
     * @param tileY
     * @param pixelOffsetX
     * @param pixelOffsetY
     */
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
                dibujarAnimacion(juego.getMapa().getBaldosa(x, y).getCapa(1),
                        (screenX - 1) * TILE_PIXEL_WIDTH + pixelOffsetX,
                        (screenY - 1) * TILE_PIXEL_HEIGHT + pixelOffsetY, true, true, false, ambientcolor);

                // Dibujamos la segunda capa
                if (juego.getMapa().getBaldosa(x, y).getCapa(2).esValido()) {
                    dibujarAnimacion(juego.getMapa().getBaldosa(x, y).getCapa(2),
                            (screenX - 1) * TILE_PIXEL_WIDTH + pixelOffsetX,
                            (screenY - 1) * TILE_PIXEL_HEIGHT + pixelOffsetY, true, true, false, ambientcolor);
                }
                screenX++;
            }
            screenX = screenX - x + screenMinX;
            screenY++;
        }

        int xd;
        int yd;
        screenY = minYOffset - tileBufferSize;
        for (y = minY; y <= maxY; y++) {
            screenX = minXOffset - tileBufferSize;
            for (x = minX; x <= maxX; x++) {
                xd = screenX * 32 + pixelOffsetX;
                yd = screenY * 32 + pixelOffsetY;
                // Si hay un objeto arrojado en el suelo en esta posicion, entonces lo dibujamos.
                if (juego.getMapa().getBaldosa(x, y).getAnimObjecto().esValido()) {
                    dibujarAnimacion(juego.getMapa().getBaldosa(x, y).getAnimObjecto(), xd, yd, true, true, false, ambientcolor);
                } else {

                }

                // Hay un personaje parado en esta baldosa?
                if (juego.getMapa().getBaldosa(x, y).getCharindex() > 0) {
                    Personaje p = personajes[juego.getMapa().getBaldosa(x, y).getCharindex()];

                    if (!p.isActivo()) {
                        // Si el personaje no esta activo, entonces lo sacamos
                        juego.getMapa().getBaldosa(x, y).setCharindex(0);
                    } else {
                        // Si el personaje esta activo, entonces lo dibujamos
                        dibujarPersonaje(p, xd, yd);
                    }
                }

                // Dibujamos la capa 3
                if (juego.getMapa().getBaldosa(x, y).getCapa(3).esValido()) {
                    dibujarAnimacion(juego.getMapa().getBaldosa(x, y).getCapa(3), xd,
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
                xd = screenX * 32 + pixelOffsetX;
                yd = screenY * 32 + pixelOffsetY;
                // Dibujamos el techo
                if (juego.getMapa().getBaldosa(x, y).getCapa(4).esValido()) {
                    dibujarAnimacion(juego.getMapa().getBaldosa(x, y).getCapa(4), xd,
                            yd, true, true, false, ambientcolor);
                }
                screenX++;
            }
            screenY++;
        }

        dibujarTexto(454, 8, 1, ambientcolor, FPS + " FPS");
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
                        dibujarTexto(pixelOffsetX - (personaje.getNombre().length() * 4) + 28, pixelOffsetY + 30, 1, ambientcolor, personaje.getNombre());
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
        this.texturas.destruir();
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
    public void entradaTeclado(long window, int key, int scancode, int action, int mods) {
        if (cliente.isJugando()) {

            if (key == GLFW_KEY_RIGHT && action != GLFW_RELEASE) {
                if (userMoving) {
                    return;
                }
                juego.usuarioCamina(Orientacion.ESTE);
            }

            if (key == GLFW_KEY_LEFT && action != GLFW_RELEASE) {
                if (userMoving) {
                    return;
                }
                juego.usuarioCamina(Orientacion.OESTE);
            }

            if (key == GLFW_KEY_UP && action != GLFW_RELEASE) {
                if (userMoving) {
                    return;
                }
                juego.usuarioCamina(Orientacion.NORTE);
            }

            if (key == GLFW_KEY_DOWN && action != GLFW_RELEASE) {
                if (userMoving) {
                    return;
                }
                juego.usuarioCamina(Orientacion.SUR);
            }

            if ((key == GLFW_KEY_LEFT_CONTROL || key == GLFW_KEY_RIGHT_CONTROL) && action != GLFW_RELEASE) {

                juego.usuarioGolpea();
            }
        }

        interfaz.entradaTeclado(window, key, scancode, action, mods);
    }

    /**
     * Procesamos eventos del mouse
     *
     * @param window
     * @param x
     * @param y
     * @param button
     * @param action
     * @param mods
     */
    public void entradaMouse(long window, int x, int y, int button, int action, int mods) {
        // Los eventos del juego los procesamos solo si el usuario esta jugando
        if (cliente.isJugando()) {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                // Verificamos que el click esta dentro del area de dibujo del juego
                if (x > viewportX && x < viewportX + viewportAncho && y > viewportY && y < viewportY + viewportAncho) {
                    // Convertimos la posicion del mouse en coordenadas del juego
                    int cX = x - viewportX;
                    int cY = y - viewportY;
                    int tX = juego.getUsuario().getPosicion().x() + cX / TILE_PIXEL_WIDTH - anchoEnBaldosas / 2;
                    int tY = juego.getUsuario().getPosicion().y() + cY / TILE_PIXEL_HEIGHT - altoEnBaldosas / 2;
                    cliente.getConexion().enviarClick(tX, tY);

                }
            }
        }
        interfaz.mouseEvents(window, x, y, button, action, mods);
    }

    /**
     * Creamos un nuevo personaje que va a ser dibujado por el motor
     *
     * @param charindex ID del personaje
     * @param nombre
     * @param x Posicion horizontal (el baldosas)
     * @param y Posicion vertical (en baldosas)
     * @param orientacion
     * @param cabeza ID de la animacion de la cabeza
     * @param cuerpo ID de la animacion del cuerpo
     * @param casco ID de la animacion del casco
     * @param arma ID de la animacion del arma
     * @param escudo ID de la animacion del escudo
     *
     * @return Charindex del personaje creado
     */
    public int crearPersonaje(int charindex, String nombre, int x, int y, Orientacion orientacion, int cabeza, int cuerpo, int casco, int arma, int escudo) {
        if (charindex > ultimoCharindex) {
            ultimoCharindex = charindex;
        }
        if (personajes[charindex].isActivo()) {
            return 0;
        }
        personajes[charindex] = new Personaje(
                nombre,
                orientacion,
                new Posicion(x, y),
                new AnimCabeza(Recursos.getCabeza(cabeza)),
                new AnimCuerpo(Recursos.getCuerpo(cuerpo)),
                new AnimCabeza(Recursos.getCasco(casco)),
                new AnimArma(Recursos.getArma(arma)),
                new AnimEscudo(Recursos.getEscudo(escudo)));

        personajes[charindex].setActivo(true);
        return charindex;
    }

    /**
     * Asegurarse que todos los personajes estan ubicados en la baldosa que les
     * corresponde
     *
     * @see RefreshAllChars
     */
    public void personajesActualizarTodos() {
        for (short i = 1; i <= ultimoCharindex; i++) {
            if (personajes[i].isActivo()) {
                juego.getMapa().getBaldosa(personajes[i].getPosicion()).setCharindex(i);
            }
        }
    }

    /**
     * @return Instancia de la interfaz grafica
     */
    public IInterfaz getInterfaz() {
        return interfaz;
    }

    /**
     * @param charindex
     * @return Obtiene la instancia del personaje correspondiente al charindex
     */
    public Personaje getPersonaje(int charindex) {
        return personajes[charindex];
    }

    /**
     * Dibujar texto dentro de la zona de dibujado del juego
     *
     * @param x
     * @param y
     * @param idFuente
     * @param color
     * @param texto
     */
    public void dibujarTexto(int x, int y, int idFuente, Color color, String texto) {
        fuente.dibujarTexto(x + viewportX, y + viewportY, idFuente, color, texto);
    }

    /**
     * Obtiene el charindex de mayor numero
     *
     * @return
     */
    public int getUltimoCharindex() {
        return ultimoCharindex;
    }

}
