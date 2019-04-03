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
package ar.net.argentum.cliente.sonido;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

/**
 * Clase encargada de manejar los sonidos del juego
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public abstract class Sonido {

    public static final int SND_SWING = 2;
    public static final int SND_WARP = 3;
    public static final int SND_PUERTA = 5;
    public static final int SND_NIVEL = 6;
    public static final int SND_IMPACTO = 10;
    public static final int SND_USERMUERTE = 11;
    public static final int SND_IMPACTO2 = 12;
    public static final int SND_LENADOR = 13;
    public static final int SND_TALAR = 13;
    public static final int SND_FOGATA = 14;
    public static final int SND_PESCAR = 14;
    public static final int SND_MINERO = 15;
    public static final int SND_RESUCITAR = 20;
    public static final int SND_AVE = 21;
    public static final int SND_AVE2 = 22;
    public static final int SND_PASOS_1 = 23;
    public static final int SND_PASOS_2 = 24;
    public static final int SND_SACARARMA = 25;
    public static final int SND_GRILLO = 28;
    public static final int SND_GRILLO2 = 29;
    public static final int SND_AVE3 = 34;
    public static final int SND_ESCUDO = 37;
    public static final int SND_HERRERO_TRABAJANDO = 41;
    public static final int SND_CARPINTERO_TRABAJANDO = 42;
    public static final int SND_BEBER = 46;

    private static final Logger LOGGER = Logger.getLogger(Sonido.class);
    private static final Map<Integer, Audio> SONIDOS = new HashMap<>();

    private static long device;
    private static long context;
    private static boolean iniciado;

    protected static FloatBuffer oyentePosicion = (FloatBuffer) BufferUtils.createFloatBuffer(3)
            .put(new float[]{0.0f, 0.0f, 0.0f}).rewind();
    protected static FloatBuffer oyenteVelocidad = (FloatBuffer) BufferUtils.createFloatBuffer(3)
            .put(new float[]{0.0f, 0.0f, 0.0f}).rewind();
    protected static FloatBuffer oyenteOrientacion = (FloatBuffer) BufferUtils.createFloatBuffer(6)
            .put(new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f}).rewind();

    public static Audio getSonido(int id) {
        if (!SONIDOS.containsKey(id)) {
            LOGGER.debug("Cargando sonido Nº" + id);
            try {
                Audio audio = new Audio("recursos/sonidos/" + id + ".ogg");
                SONIDOS.put(id, audio);
                return audio;
            } catch (Exception ex) {
                LOGGER.fatal("Error al cargar el sonido Nº" + id, ex);
                return null;
            }
        }
        LOGGER.debug("Reutilizando sonido Nº" + id);
        return SONIDOS.get(id);
    }

    /**
     * Reproducir un sonido
     *
     * @param id Numero de sonido a reproducir
     */
    public static void reproducirSonido(int id, float x, float y) {
        try {
            Audio sonido = getSonido(id);
            if (sonido != null) {
                sonido.reproducir(x, y);
                return;
            }
        } catch (Exception ex) {
            LOGGER.fatal(null, ex);
        }
        LOGGER.error("No se pudo reproducir el sonido Nº" + id);
    }

    /**
     * Inicializar contexto de sonido
     */
    public static void iniciar() {
        if (iniciado) {
            throw new IllegalStateException("El contexto de OpenAL debe ser iniciado una unica vez");
        }
        LOGGER.debug("Iniciando contexto de sonido...");
        iniciado = true;

        // Obtenemos el dispositivo predeterminado
        String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        device = ALC10.alcOpenDevice(defaultDeviceName);

        // Creamos el contexto
        int[] attributes = {0};
        context = ALC10.alcCreateContext(device, attributes);

        // Activamos el contexto
        ALC10.alcMakeContextCurrent(context);

        // Obtenemos las capacidades del dispositivo
        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (alCapabilities.OpenAL10) {
            LOGGER.info("OpenAL 1.0 soportado.");
        } else {
            LOGGER.error("OpenAL 1.0 no soportado.");
        }

        // Verificamos si ocurrio algun error
        if (AL10.alGetError() != AL10.AL_NO_ERROR) {
            LOGGER.error(AL10.AL_FALSE);
        }

        // Posicionamos al oyente
        AL10.alListenerfv(AL10.AL_POSITION, oyentePosicion);
        AL10.alListenerfv(AL10.AL_VELOCITY, oyenteVelocidad);
        AL10.alListenerfv(AL10.AL_ORIENTATION, oyenteOrientacion);
    }

    /**
     * Destruir contexto de sonido
     */
    public static void destruir() {
        LOGGER.debug("Destruyendo contexto de sonido...");

        // Destruimos los sonidos cargados
        for (Map.Entry<Integer, Audio> entry : SONIDOS.entrySet()) {
            LOGGER.debug("Liberando sonido nº" + entry.getKey());
            entry.getValue().destruir();
        }

        // Destruimos el contexto de Open AL
        ALC10.alcDestroyContext(context);

        // Cerramos el dispositivo de sonido
        ALC10.alcCloseDevice(device);
        LOGGER.debug("Sonido terminado.");
    }
}
