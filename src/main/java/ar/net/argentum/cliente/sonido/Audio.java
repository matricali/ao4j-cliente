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

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

/**
 * Representa un sonido que se puede reproducir
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Audio {

    private final int buffer;
    private final int source;

    FloatBuffer sonidoPosicion = (FloatBuffer) BufferUtils.createFloatBuffer(3)
            .put(new float[]{0.0f, 0.0f, 0.0f}).rewind();
    FloatBuffer sonidoVelocidad = (FloatBuffer) BufferUtils.createFloatBuffer(3)
            .put(new float[]{0.0f, 0.0f, 0.0f}).rewind();

    /**
     * Cargar un sonido OGG desde un archivo utilizando STB y Vorbis
     *
     * @param archivo Ruta hacia el archivo .ogg
     * @throws IOException
     */
    public Audio(String archivo) throws IOException {

        int canales;
        int sampleRate;
        ShortBuffer bufferAudioCrudo;

        /**
         * Cargamos el archivo Vorbis a un buffer crudo utilizando STB y
         * obtenemos informacion sobre el sample rate y la cantidad de canales
         */
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer bufferCanales = stack.mallocInt(1);
            IntBuffer bufferSampleRate = stack.mallocInt(1);

            bufferAudioCrudo = stb_vorbis_decode_filename(archivo, bufferCanales, bufferSampleRate);

            canales = bufferCanales.get();
            sampleRate = bufferSampleRate.get();
        }

        // Buscamos el formato coorrecto de OpenAL
        int format = -1;
        if (canales == 1) {
            format = AL10.AL_FORMAT_MONO16;
        } else if (canales == 2) {
            format = AL10.AL_FORMAT_STEREO16;
        }

        // Generamos un nuevo buffer de OpenAL para cargar nuestro sonido
        this.buffer = AL10.alGenBuffers();

        // Cargamos nuestro sonido al buffer
        AL10.alBufferData(buffer, format, bufferAudioCrudo, sampleRate);

        /**
         * Liberamos la memoria utilizada por el buffer que utilizamos con STB,
         * ya no lo necesitamos
         */
        free(bufferAudioCrudo);

        // Generamos un nuevo origen de sonido.
        this.source = AL10.alGenSources();

        // Asociamos el origen a nuestro buffer
        AL10.alSourcei(source, AL10.AL_BUFFER, buffer);

        /**
         * Asociamos los buffers de posicion, velocidad, y orientacion a nuestro
         * origen.
         *
         * @TODO Esto lo vamos a usar para el sonido 3D.
         */
        AL10.alSourcef(source, AL10.AL_PITCH, 1.0f);
        AL10.alSourcef(source, AL10.AL_GAIN, 1.0f);
        AL10.alSourcefv(source, AL10.AL_POSITION, sonidoPosicion);
        AL10.alSourcefv(source, AL10.AL_VELOCITY, sonidoVelocidad);
        AL10.alSourcei(source, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);
    }

    /**
     * Destruir los buffers y liberar la memoria
     */
    public void destruir() {
        AL10.alDeleteSources(source);
        AL10.alDeleteBuffers(buffer);
    }

    /**
     * Reproducir el sonido en 3D
     */
    public void reproducir(float x, float y) {
        sonidoPosicion.put(1, x);
        sonidoPosicion.put(2, y);
        AL10.alSourcefv(source, AL10.AL_POSITION, sonidoPosicion);
        AL10.alSourcePlay(source);
    }

    /**
     * Pausar el sonido
     */
    public void pausar() {
        AL10.alSourcePause(source);
    }

    /**
     * Detener el sonido
     */
    public void detener() {
        AL10.alSourceStop(source);
    }

    /**
     * @return Verdadero si el sonido se esta reproduciendo
     */
    public boolean reproduciendo() {
        return AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }
}
