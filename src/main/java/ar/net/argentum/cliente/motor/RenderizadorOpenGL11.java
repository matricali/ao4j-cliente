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

import ar.net.argentum.cliente.motor.texturas.Textura;
import org.lwjgl.opengl.GL11;
import ar.net.argentum.cliente.motor.texturas.ITexturas;

/**
 * Clase que encapsula todo lo necesario para dibujar las texturas del juego en
 * la pantalla usando OpenGL
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class RenderizadorOpenGL11 implements Renderizador {

    protected boolean dibujando;
    protected final ITexturas texturas;

    protected final int x;
    protected final int y;
    protected final int ancho;
    protected final int alto;
    protected final int anchoVentana;
    protected final int altoVentana;

    public RenderizadorOpenGL11(ITexturas surfaces, int anchoVentana, int altoVentana, int x, int y, int ancho, int alto) {
        this.texturas = surfaces;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        this.anchoVentana = anchoVentana;
        this.altoVentana = altoVentana;
    }

    @Override
    public void iniciar() {
        if (dibujando) {
            throw new IllegalStateException("El renderizador ya estaba dibujando!");
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // Color del limpiado de pantalla
        GL11.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        // Configuramos nuestra area de dibujado
        GL11.glViewport(0, 0, anchoVentana, altoVentana);
        GL11.glOrtho(0, anchoVentana, altoVentana, 0, 1, -1);

        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        GL11.glEnable(GL11.GL_ALPHA);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public void limpiarPantalla() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void iniciarDibujado(long ventana) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(15, 15, ancho - 15, alto - 15);
        this.dibujando = true;
    }

    @Override
    public void finalizarDibujado() {
        if (!dibujando) {
            throw new IllegalStateException("Renderer isn't drawing!");
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        this.dibujando = false;
    }

    @Override
    public void dibujarSprite(Sprite sprite, int x, int y, boolean blend, Color color) {
        if (blend) {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        }

        Textura textura = texturas.getTextura(sprite);

        int textureWidth = textura.getWidth();
        int textureHeight = textura.getHeight();

        float src_left = sprite.sX;
        float src_top = sprite.sY;
        float src_right = src_left + sprite.pixelWidth;
        float src_bottom = src_top + (sprite.pixelHeight);

        float dest_left = x + this.x;
        float dest_top = y + this.y;
        float dest_right = x + this.x + (src_right - src_left);
        float dest_bottom = y + this.y + (src_bottom - src_top);

        float x_cor;
        float y_cor;
        textura.usar();
        GL11.glBegin(GL11.GL_QUADS);
        {
            //0 
            x_cor = dest_left;
            y_cor = dest_bottom;

            GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
            GL11.glTexCoord2f(src_left / textureWidth, (src_bottom) / textureHeight);
            GL11.glVertex2d(x_cor, y_cor);

            //1
            x_cor = dest_left;
            y_cor = dest_top;

            GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
            GL11.glTexCoord2f(src_left / textureWidth, src_top / textureHeight);
            GL11.glVertex2d(x_cor, y_cor);

            //3
            x_cor = dest_right;
            y_cor = dest_top;

            GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
            GL11.glTexCoord2f((src_right) / textureWidth, src_top / textureHeight);
            GL11.glVertex2d(x_cor, y_cor);

            //2
            x_cor = dest_right;
            y_cor = dest_bottom;

            GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
            GL11.glTexCoord2f((src_right) / textureWidth, (src_bottom) / textureHeight);
            GL11.glVertex2d(x_cor, y_cor);
        }
        GL11.glEnd();

        if (blend) {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

}
