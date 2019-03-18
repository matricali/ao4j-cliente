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
 * along with this programa.  If not, see <https://www.gnu.org/licenses/>.
 */
package ar.net.argentum.cliente.motor;

import ar.net.argentum.cliente.motor.gamedata.Sprite;
import ar.net.argentum.cliente.motor.surface.ISurface;
import ar.net.argentum.cliente.motor.surface.Textura;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32C;
import static org.lwjgl.opengl.GL32C.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * Clase que encapsula todo lo necesario para dibujar las texturas del juego en
 * la pantalla usando OpenGL
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class RenderizadorOpenGL32 implements Renderizador {

    protected final ISurface texturas;

    protected long ventana;
    protected final int x;
    protected final int y;
    protected final int ancho;
    protected final int alto;
    protected final int anchoVentana;
    protected final int altoVentana;

    private int vao;
    private int vbo;
    private int programa;

    private FloatBuffer vertices;
    private int numVertices;
    private boolean dibujando;

    public RenderizadorOpenGL32(long ventana, ISurface surfaces, int anchoVentana, int altoVentana, int x, int y, int ancho, int alto) {
        this.ventana = ventana;
        this.texturas = surfaces;
        this.anchoVentana = anchoVentana;
        this.altoVentana = altoVentana;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    @Override
    public void iniciar() {
        // Iniciamos y configuramos los shaders
        configurarShaders();

        // Habilitamos alpha blending
        GL32C.glEnable(GL_BLEND);
        GL32C.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void limpiarPantalla() {
        GL32C.glClear(GL32C.GL_COLOR_BUFFER_BIT | GL32C.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void iniciarDibujado(long ventana) {
        limpiarPantalla();

        if (dibujando) {
            throw new IllegalStateException("El renderizador ya estaba dibujando!");
        }

        this.dibujando = true;
        this.numVertices = 0;

        // setup program
        GL32C.glUseProgram(programa);

        // Habilitamos alpha blending
        GL32C.glEnable(GL_BLEND);
        GL32C.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL32C.glEnable(GL_ALPHA);
        GL32C.glEnable(GL_SCISSOR_TEST);
        GL32C.glScissor(15, 15, ancho - 15, alto - 15);
    }

    @Override
    public void finalizarDibujado() {
        if (!dibujando) {
            throw new IllegalStateException("El renderizador no esta dibujando!");
        }
        flush();
        GL32C.glDisable(GL_SCISSOR_TEST);
        this.dibujando = false;
    }

    @Override
    public void dibujarSprite(Sprite sprite, int x, int y, boolean blend, Color color) {
        Textura textura = texturas.getTextura(sprite);
        textura.usar();
        drawTextureRegion(textura, this.x + x, this.y + y, sprite.sX, sprite.sY, sprite.pixelWidth, sprite.pixelHeight, color);
        flush();
    }

    private void configurarShaders() {
        if (GL.getCapabilities().OpenGL32) {
            /* Generate Vertex Array Object */
            vao = GL32C.glGenVertexArrays();
            GL32C.glBindVertexArray(vao);
        } else {
            vao = -1;
        }

        /* Generate Vertex Buffer Object */
        vbo = GL32C.glGenBuffers();
        GL32C.glBindBuffer(GL_ARRAY_BUFFER, vbo);

        /* Create FloatBuffer */
        vertices = MemoryUtil.memAllocFloat(4096);

        /* Upload null data to allocate storage for the VBO */
        long size = vertices.capacity() * Float.BYTES;
        GL32C.glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);

        /* Initialize variables */
        numVertices = 0;
        dibujando = false;

        /* Load shaders */
        int vertexShader;
        int fragmentShader;

        if (GL.getCapabilities().OpenGL32) {
            vertexShader = cargarShader(GL_VERTEX_SHADER, "recursos/shaders/default.vert");
            fragmentShader = cargarShader(GL_FRAGMENT_SHADER, "recursos/shaders/default.frag");
        } else {
            vertexShader = cargarShader(GL_VERTEX_SHADER, "recursos/shaders/legacy.vert");
            fragmentShader = cargarShader(GL_FRAGMENT_SHADER, "recursos/shaders/legacy.frag");
        }

        // Creamos el programa
        this.programa = GL32C.glCreateProgram();
        GL32C.glAttachShader(programa, vertexShader);
        GL32C.glAttachShader(programa, fragmentShader);

        if (GL.getCapabilities().OpenGL32) {
            GL32C.glBindFragDataLocation(programa, 0, "fragColor");
        }

        // Linkeamos todo
        GL32C.glLinkProgram(programa);
        GL32C.glUseProgram(programa);

        // En este punto ya no necesitamos los shaders en la memoria
        GL32C.glDeleteShader(vertexShader);
        GL32C.glDeleteShader(fragmentShader);

        /* Get width and height of framebuffer */
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(ventana, widthBuffer, heightBuffer);
            width = widthBuffer.get();
            height = heightBuffer.get();
        }

        /* Specify Vertex Pointers */
        specifyVertexAttributes(programa);

        /* Set texture uniform */
        int uniTex = GL32C.glGetUniformLocation(programa, "texImage");
        GL32C.glUniform1i(uniTex, 0);

        /* Set model matrix to identity matrix */
        Matrix4f model = new Matrix4f();
        int uniModel = GL32C.glGetUniformLocation(programa, "model");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            glUniformMatrix4fv(uniModel, false, model.get(buffer));
        }

        /* Set view matrix to identity matrix */
        Matrix4f view = new Matrix4f();
        int uniView = GL32C.glGetUniformLocation(programa, "view");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            glUniformMatrix4fv(uniView, false, view.get(buffer));
        }

        /* Set projection matrix to an orthographic projection */
        Matrix4f projection = new Matrix4f();
        projection.ortho(0f, width, height, 0f, -1f, 1f);
        int uniProjection = GL32C.glGetUniformLocation(programa, "projection");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            glUniformMatrix4fv(uniProjection, false, projection.get(buffer));
        }
    }

    /**
     * Draws the currently bound texture on specified coordinates.
     *
     * @param texture Used for getting width and height of the texture
     * @param x X position of the texture
     * @param y Y position of the texture
     */
    private void drawTexture(Textura texture, float x, float y) {
        drawTexture(texture, x, y, Color.WHITE);
    }

    /**
     * Draws the currently bound texture on specified coordinates and with
     * specified color.
     *
     * @param texture Used for getting width and height of the texture
     * @param x X position of the texture
     * @param y Y position of the texture
     * @param c The color to use
     */
    private void drawTexture(Textura texture, float x, float y, Color c) {
        /* Vertex positions */
        float x1 = x;
        float y1 = y;
        float x2 = x1 + texture.getWidth();
        float y2 = y1 + texture.getHeight();

        /* Texture coordinates */
        float s1 = 0f;
        float t1 = 0f;
        float s2 = 1f;
        float t2 = 1f;

        drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, c);
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     * @param texture Used for getting width and height of the texture
     * @param x X position of the texture
     * @param y Y position of the texture
     * @param regX X position of the texture region
     * @param regY Y position of the texture region
     * @param regWidth Width of the texture region
     * @param regHeight Height of the texture region
     */
    private void drawTextureRegion(Textura texture, float x, float y, float regX, float regY, float regWidth, float regHeight) {
        drawTextureRegion(texture, x, y, regX, regY, regWidth, regHeight, Color.WHITE);
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     * @param texture Used for getting width and height of the texture
     * @param x X position of the texture
     * @param y Y position of the texture
     * @param regX X position of the texture region
     * @param regY Y position of the texture region
     * @param regWidth Width of the texture region
     * @param regHeight Height of the texture region
     * @param c The color to use
     */
    private void drawTextureRegion(Textura texture, float x, float y, float regX, float regY, float regWidth, float regHeight, Color c) {
        /* Vertex positions */
        float x1 = x;
        float y1 = y;
        float x2 = x + regWidth;
        float y2 = y + regHeight;

        /* Texture coordinates */
        float s1 = regX / texture.getWidth();
        float t1 = regY / texture.getHeight();
        float s2 = (regX + regWidth) / texture.getWidth();
        float t2 = (regY + regHeight) / texture.getHeight();

        drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, c);
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     * @param x1 Bottom left x position
     * @param y1 Bottom left y position
     * @param x2 Top right x position
     * @param y2 Top right y position
     * @param s1 Bottom left s coordinate
     * @param t1 Bottom left t coordinate
     * @param s2 Top right s coordinate
     * @param t2 Top right t coordinate
     */
    private void drawTextureRegion(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2) {
        drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, Color.WHITE);
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     * @param x1 Bottom left x position
     * @param y1 Bottom left y position
     * @param x2 Top right x position
     * @param y2 Top right y position
     * @param s1 Bottom left s coordinate
     * @param t1 Bottom left t coordinate
     * @param s2 Top right s coordinate
     * @param t2 Top right t coordinate
     * @param c The color to use
     */
    private void drawTextureRegion(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2, Color c) {
        if (vertices.remaining() < 7 * 6) {
            // Necesitamos más espacio en el búfer, así que lo vaciamos
            flush();
        }

        float r = c.getRed();
        float g = c.getGreen();
        float b = c.getBlue();
        float a = c.getAlpha();

        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
        vertices.put(x1).put(y2).put(r).put(g).put(b).put(a).put(s1).put(t2);
        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);

        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);
        vertices.put(x2).put(y1).put(r).put(g).put(b).put(a).put(s2).put(t1);

        numVertices += 6;
    }

    /**
     * Vacía los datos a la GPU para permitir que se procesen.
     */
    public void flush() {
        if (numVertices > 0) {
            vertices.flip();

            if (vao != -1) {
                GL32C.glBindVertexArray(vao);
            } else {
                GL32C.glBindBuffer(GL_ARRAY_BUFFER, vbo);
                specifyVertexAttributes(programa);
            }
            GL32C.glUseProgram(programa);

            /* Upload the new vertex data */
            GL32C.glBindBuffer(GL_ARRAY_BUFFER, vbo);
            GL32C.glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

            /* Draw batch */
            GL32C.glDrawArrays(GL_TRIANGLES, 0, numVertices);

            /* Clear vertex data for next batch */
            vertices.clear();
            numVertices = 0;
        }
    }

    /**
     * Cargar shader desde un archivo
     *
     * @param type (GL_VERTEX_SHADER|GL_FRAGMENT_SHADER)
     * @param path Ruta del archivo
     * @return ID del shader creado
     */
    public int cargarShader(int type, String path) {
        // Cargamos el archivo con la informacion del shader        
        StringBuilder builder = new StringBuilder();

        try (InputStream in = new FileInputStream(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Ocurrio un error al cargar el archivo del Shader!"
                    + System.lineSeparator() + ex.getMessage());
        }
        CharSequence source = builder.toString();

        // Creamos un shader
        int shader = GL32C.glCreateShader(type);

        // Ponemos el codigo del shader
        GL32C.glShaderSource(shader, source);

        // Compilamos el shader
        GL32C.glCompileShader(shader);

        // Verificamos el estado
        int status = GL32C.glGetShaderi(shader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(GL32C.glGetShaderInfoLog(shader));
        }

        return shader;
    }

    /**
     * Especifica los punteros de vértice.
     */
    private void specifyVertexAttributes(int program) {
        // Specify Vertex Pointer
        int posAttrib = GL32C.glGetAttribLocation(program, "position");
        GL32C.glEnableVertexAttribArray(posAttrib);
        GL32C.glVertexAttribPointer(posAttrib, 2, GL_FLOAT, false, 8 * Float.BYTES, 0);

        // Specify Color Pointer
        int colAttrib = GL32C.glGetAttribLocation(program, "color");
        GL32C.glEnableVertexAttribArray(colAttrib);
        GL32C.glVertexAttribPointer(colAttrib, 4, GL_FLOAT, false, 8 * Float.BYTES, 2 * Float.BYTES);

        // Specify Texture Pointer
        int texAttrib = GL32C.glGetAttribLocation(program, "texcoord");
        GL32C.glEnableVertexAttribArray(texAttrib);
        GL32C.glVertexAttribPointer(texAttrib, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
    }
}
