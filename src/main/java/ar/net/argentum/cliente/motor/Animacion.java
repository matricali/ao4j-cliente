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

import ar.net.argentum.cliente.Recursos;

/**
 * Una animacion que se puede dibujar
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Animacion {

    /**
     * Numero de sprite
     */
    private short grhIndex;
    /**
     * Contador de cuadros
     */
    private float frameCounter;
    /**
     * Velocidad
     */
    protected float velocidad;
    /**
     * Iniciamos la animacion?
     */
    private boolean started;
    /**
     * Cantidad de repeticiones restantes de la animacion
     */
    private int repeticiones;
    /**
     * Angulo, actualmente en desuso
     */
    private final float angulo;

    public Animacion(short grhindex, boolean iniciado) {
        this(grhindex, Recursos.getSprite(grhindex), iniciado);
    }

    public Animacion(short grhindex, Sprite sprite, boolean iniciado) {
        this.started = false;
        this.grhIndex = grhindex;
        this.frameCounter = 1.0f;
        this.repeticiones = 0;
        this.velocidad = 0.0f;
        this.angulo = 0.0f;

        this.grhIndex = grhindex;

        if (iniciado) {
            // Si el sprite tiene un solo cuadro no hay animacion
            this.started = sprite.numFrames > 1;
        } else {
            if (sprite.numFrames == 1) {
                iniciado = false;
            }
            this.started = iniciado;
        }

        if (this.started) {
            this.repeticiones = -1;
        } else {
            this.repeticiones = 0;
        }

        this.frameCounter = 1;
        this.velocidad = sprite.speed;
    }

    public Animacion() {
        this.started = false;
        this.grhIndex = 1;
        this.frameCounter = 1.0f;
        this.repeticiones = 0;
        this.velocidad = 0.0f;
        this.angulo = 0.0f;
    }

    public Animacion(Animacion original) {
        this.grhIndex = original.grhIndex;
        this.frameCounter = original.frameCounter;
        this.velocidad = original.velocidad;
        this.started = original.started;
        this.repeticiones = original.repeticiones;
        this.angulo = original.angulo;
    }

    public Animacion setStarted(boolean started) {
        this.started = started;
        return this;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean esValido() {
        return grhIndex > 1;
    }

    public Sprite getSprite() {
        return Recursos.getSprite(grhIndex);
    }

    /**
     * Calcula el cuadro que vamos a dibujar.
     *
     * @param timerElapsedTime
     */
    public void animar(long timerElapsedTime) {
        this.frameCounter = frameCounter + (timerElapsedTime * getSprite().numFrames / getVelocidad());
        if (frameCounter > getSprite().numFrames) {
            this.frameCounter = (frameCounter % getSprite().numFrames) + 1;
            if (repeticiones != -1) {
                if (repeticiones > 0) {
                    this.repeticiones--;
                } else {
                    this.started = false;
                }
            }
        }
    }

    /**
     * Obtiene el Sprite del cuadro actual.
     *
     * @return
     */
    public Sprite getCuadroActual() {
        int currentGrhIndex = getSprite().frames[(int) (frameCounter)];
        return Recursos.getSprite(currentGrhIndex);
    }

    /**
     * @return Velocidad de la animacion.
     */
    public float getVelocidad() {
        return velocidad;
    }

    /**
     * Reiniciar la animacion al primer cuadro.
     */
    public void reiniciar() {
        this.started = false;
        this.frameCounter = 1;
    }
}
