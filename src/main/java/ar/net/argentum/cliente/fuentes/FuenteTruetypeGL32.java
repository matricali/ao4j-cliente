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
package ar.net.argentum.cliente.fuentes;

import ar.net.argentum.cliente.motor.Color;
import ar.net.argentum.cliente.motor.RenderizadorOpenGL32;
import static ar.net.argentum.cliente.motor.Utils.ioResourceToByteBuffer;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.stb.*;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class FuenteTruetypeGL32 implements IFuente {

    private static final int BITMAP_W = 512;
    private static final int BITMAP_H = 512;

    private static final float[] scale = {
        14.0f,
        24.0f,};

    private static final int[] sf = {
        0, 1, 2,
        0, 1, 2
    };

    private final STBTTAlignedQuad q = STBTTAlignedQuad.malloc();
    private final FloatBuffer xb = memAllocFloat(1);
    private final FloatBuffer yb = memAllocFloat(1);
    private STBTTPackedchar.Buffer chardata;
    private boolean integer_align;

    private FloatBuffer vertices;
    private int numVertices;
    private int textura;
    private final RenderizadorOpenGL32 renderizador;

    public FuenteTruetypeGL32(String archivo, RenderizadorOpenGL32 renderizador) {
        this.renderizador = renderizador;

        // Generamos una nueva textura
        this.textura = GL32C.glGenTextures();
        this.chardata = STBTTPackedchar.malloc(6 * 128);

        try (STBTTPackContext pc = STBTTPackContext.malloc()) {
            ByteBuffer ttf = ioResourceToByteBuffer(new File(archivo), 512 * 1024);

            ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);

            stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL);
            for (int i = 0; i < 2; i++) {
                int p = (i * 3 + 0) * 128 + 32;
                chardata.limit(p + 95);
                chardata.position(p);
                stbtt_PackSetOversampling(pc, 1, 1);
                stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, chardata);

                p = (i * 3 + 1) * 128 + 32;
                chardata.limit(p + 95);
                chardata.position(p);
                stbtt_PackSetOversampling(pc, 2, 2);
                stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, chardata);

                p = (i * 3 + 2) * 128 + 32;
                chardata.limit(p + 95);
                chardata.position(p);
                stbtt_PackSetOversampling(pc, 3, 1);
                stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, chardata);
            }
            chardata.clear();
            stbtt_PackEnd(pc);

            GL32C.glBindTexture(GL32C.GL_TEXTURE_2D, textura);
            GL32C.glTexImage2D(GL32C.GL_TEXTURE_2D, 0, GL32C.GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL32C.GL_ALPHA, GL32C.GL_UNSIGNED_BYTE, bitmap);
            GL32C.glTexParameteri(GL32C.GL_TEXTURE_2D, GL32C.GL_TEXTURE_MAG_FILTER, GL32C.GL_LINEAR);
            GL32C.glTexParameteri(GL32C.GL_TEXTURE_2D, GL32C.GL_TEXTURE_MIN_FILTER, GL32C.GL_LINEAR);

            // Creamos un FloatBuffer para guardar los datos de los vertices a dibujar
            this.vertices = MemoryUtil.memAllocFloat(4096);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void crearVertices(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2, Color c) {
        if (vertices.remaining() < 7 * 6) {
            // Necesitamos más espacio en el búfer, así que lo vaciamos
            renderizador.flush(vertices, numVertices);
            this.numVertices = 0;
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

    @Override
    public void dibujarTexto(float x, float y, int font, Color color, String text) {
        xb.put(0, x);
        yb.put(0, y);

        chardata.position(font * 128);

        GL32C.glDisable(GL32C.GL_CULL_FACE);
        GL32C.glDisable(GL32C.GL_DEPTH_TEST);

        GL32C.glEnable(GL32C.GL_TEXTURE_2D);
        GL32C.glBindTexture(GL32C.GL_TEXTURE_2D, textura);

        // Habilitamos las transparencias
        GL32C.glEnable(GL_BLEND);
        GL32C.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        for (int i = 0; i < text.length(); i++) {
            stbtt_GetPackedQuad(chardata, BITMAP_W, BITMAP_H, text.charAt(i), xb, yb, q, font == 0 && integer_align);
            crearVertices(
                    q.x0(), q.y0(), q.x1(), q.y1(),
                    q.s0(), q.t0(), q.s1(), q.t1(),
                    color
            );
        }

        // Enviamos a la GPU
        renderizador.flush(vertices, numVertices);
        this.numVertices = 0;
    }

    @Override
    public void destruir() {
        chardata.free();

        memFree(yb);
        memFree(xb);

        q.free();
    }

    @Override
    public void iniciarDibujado() {

    }

    @Override
    public void terminarDibujado() {

    }

}
