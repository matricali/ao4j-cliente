/*
 * The MIT License (MIT)
 *
 * Copyright © 2014-2017, Heiko Brumme
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ar.net.argentum.cliente.motor.surface;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * Representa una textura.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class TexturaOpenGL implements Textura {

    private final int id;
    private int ancho;
    private int alto;

    public TexturaOpenGL() {
        id = glGenTextures();
    }

    @Override
    public void usar() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    @Override
    public void setParameter(int name, int value) {
        glTexParameteri(GL_TEXTURE_2D, name, value);
    }

    @Override
    public void uploadData(int width, int height, ByteBuffer data) {
        uploadData(GL_RGBA, width, height, GL_RGBA, data);
    }

    @Override
    public void uploadData(int internalFormat, int width, int height, int format, ByteBuffer data) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, data);
    }

    @Override
    public void borrar() {
        glDeleteTextures(id);
    }

    @Override
    public int getWidth() {
        return ancho;
    }

    @Override
    public void setWidth(int width) {
        if (width > 0) {
            this.ancho = width;
        }
    }

    @Override
    public int getHeight() {
        return alto;
    }

    @Override
    public void setHeight(int height) {
        if (height > 0) {
            this.alto = height;
        }
    }

    public static TexturaOpenGL createTexture(int width, int height, ByteBuffer data) {
        TexturaOpenGL texture = new TexturaOpenGL();
        texture.setWidth(width);
        texture.setHeight(height);

        texture.usar();

        texture.setParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        texture.setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        texture.uploadData(GL_RGBA, width, height, GL_RGBA, data);

        return texture;
    }

    public static TexturaOpenGL loadTexture(String path) {
        ByteBuffer imagen;
        int ancho, alto;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            /* Prepare image buffers */
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            // Cargamos la imagen
//            stbi_set_flip_vertically_on_load(true);
            imagen = stbi_load(path, w, h, comp, 4);
            if (imagen == null) {
                throw new RuntimeException("Failed to load a texture file!"
                        + System.lineSeparator() + stbi_failure_reason());
            }

            // Obtener ancho y alto de la imagen
            ancho = w.get();
            alto = h.get();
        }

        return createTexture(ancho, alto, imagen);
    }

    @Override
    public int getId() {
        return id;
    }

}
