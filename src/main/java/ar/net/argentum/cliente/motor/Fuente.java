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
package ar.net.argentum.cliente.motor;

import static ar.net.argentum.cliente.motor.Utils.ioResourceToByteBuffer;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorContentScale;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.stb.*;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Fuente {

    private static float scale(float center, float offset, float factor) {
        return (offset - center) * factor + center;
    }

    private static int getCP(String text, int to, int i, IntBuffer cpOut) {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to) {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2)) {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }

    private final ByteBuffer ttf;

    private final STBTTFontinfo info;

    private final int ascent;
    private final int descent;
    private final int lineGap;

    private float contentScaleX;
    private float contentScaleY;

    private int fontHeight;
    private int scale;

    private float lineHeight;

    private boolean kerningEnabled = true;
    private boolean lineBBEnabled;

    private STBTTBakedChar.Buffer cdata;
    private int BITMAP_W;
    private int BITMAP_H;

    public Fuente(int fontHeight, String filePath) {
        this.fontHeight = fontHeight;
        this.lineHeight = fontHeight;

        long monitor = glfwGetPrimaryMonitor();

        try (MemoryStack s = MemoryStack.stackPush()) {
            FloatBuffer px = s.mallocFloat(1);
            FloatBuffer py = s.mallocFloat(1);

            glfwGetMonitorContentScale(monitor, px, py);

            this.contentScaleX = px.get(0);
            this.contentScaleY = py.get(0);
        }

        try {
            File archivo = new File(filePath);
            this.ttf = ioResourceToByteBuffer(archivo, 512 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.info = STBTTFontinfo.create();
        if (!stbtt_InitFont(info, ttf)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pAscent = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

            this.ascent = pAscent.get(0);
            this.descent = pDescent.get(0);
            this.lineGap = pLineGap.get(0);
        }
    }

    private STBTTBakedChar.Buffer init(int BITMAP_W, int BITMAP_H) {
        int texID = glGenTextures();
        STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);

        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
        stbtt_BakeFontBitmap(ttf, fontHeight * contentScaleX, bitmap, BITMAP_W, BITMAP_H, 32, cdata);

        glBindTexture(GL_TEXTURE_2D, texID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
        glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        return cdata;
    }

    protected void iniciar() {
        this.BITMAP_W = Math.round(512 * contentScaleX);
        this.BITMAP_H = Math.round(512 * contentScaleY);

        this.cdata = init(BITMAP_W, BITMAP_H);
    }

    public void dibujarTexto(int x, int y, int fontHeight, Color color, String texto) {
//        float scaleFactor = 1.0f + scale * 0.25f;
//        glPushMatrix();
//        // Zoom
//        glScalef(scaleFactor, scaleFactor, 1f);
//        // Scroll
//        glTranslatef(4.0f, fontHeight * 0.5f + 4.0f - lineHeight * fontHeight, 0f);
//        renderText(x, y, fontHeight, color, texto);
//        glPopMatrix();

        glColor3f(color.getRed(), color.getGreen(), color.getBlue());
        renderText(x, y, fontHeight, color, texto);

    }

    public void terminar() {
        cdata.free();
    }

    private void renderText(int dx, int dy, int fontHeight, Color color, String texto) {
        float scale = stbtt_ScaleForPixelHeight(info, fontHeight);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pCodePoint = stack.mallocInt(1);

            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);

            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

            int lineStart = 0;

            float factorX = 1.0f / contentScaleX;
            float factorY = 1.0f / contentScaleY;

            float lineY = 0.0f;

            glBegin(GL_QUADS);
            for (int i = 0, to = texto.length(); i < to;) {
                i += getCP(texto, to, i, pCodePoint);

                int cp = pCodePoint.get(0);
                if (cp == '\n') {
                    if (lineBBEnabled) {
                        glEnd();
                        renderLineBB(texto, lineStart, i - 1, y.get(0), scale);
                        glBegin(GL_QUADS);
                    }

                    y.put(0, lineY = y.get(0) + (ascent - descent + lineGap) * scale);
                    x.put(0, 0.0f);

                    lineStart = i;
                    continue;
                } else if (cp < 32 || 128 <= cp) {
                    continue;
                }

                float cpX = x.get(0);
                stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, cp - 32, x, y, q, true);
                x.put(0, scale(cpX, x.get(0), factorX));
                if (kerningEnabled && i < to) {
                    getCP(texto, to, i, pCodePoint);
                    x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(info, cp, pCodePoint.get(0)) * scale);
                }

                float x0 = scale(cpX, q.x0(), factorX),
                        x1 = scale(cpX, q.x1(), factorX),
                        y0 = scale(lineY, q.y0(), factorY),
                        y1 = scale(lineY, q.y1(), factorY);

                glTexCoord2f(q.s0(), q.t0());
                glVertex2f(x0, y0);

                glTexCoord2f(q.s1(), q.t0());
                glVertex2f(x1, y0);

                glTexCoord2f(q.s1(), q.t1());
                glVertex2f(x1, y1);

                glTexCoord2f(q.s0(), q.t1());
                glVertex2f(x0, y1);
            }
            glEnd();
            if (lineBBEnabled) {
                renderLineBB(texto, lineStart, texto.length(), lineY, scale);
            }
        }
    }

    private void renderLineBB(String texto, int from, int to, float y, float scale) {
        glDisable(GL_TEXTURE_2D);
        glPolygonMode(GL_FRONT, GL_LINE);
        glColor3f(1.0f, 1.0f, 0.0f);

        float width = getStringWidth(info, texto, from, to, fontHeight);
        y -= descent * scale;

        glBegin(GL_QUADS);
        glVertex2f(0.0f, y);
        glVertex2f(width, y);
        glVertex2f(width, y - fontHeight);
        glVertex2f(0.0f, y - fontHeight);
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glPolygonMode(GL_FRONT, GL_FILL);
        glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color
    }

    private float getStringWidth(STBTTFontinfo info, String text, int from, int to, int fontHeight) {
        int width = 0;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pCodePoint = stack.mallocInt(1);
            IntBuffer pAdvancedWidth = stack.mallocInt(1);
            IntBuffer pLeftSideBearing = stack.mallocInt(1);

            int i = from;
            while (i < to) {
                i += getCP(text, to, i, pCodePoint);
                int cp = pCodePoint.get(0);

                stbtt_GetCodepointHMetrics(info, cp, pAdvancedWidth, pLeftSideBearing);
                width += pAdvancedWidth.get(0);

                if (kerningEnabled && i < to) {
                    getCP(text, to, i, pCodePoint);
                    width += stbtt_GetCodepointKernAdvance(info, cp, pCodePoint.get(0));
                }
            }
        }

        return width * stbtt_ScaleForPixelHeight(info, fontHeight);
    }
    
    

}
