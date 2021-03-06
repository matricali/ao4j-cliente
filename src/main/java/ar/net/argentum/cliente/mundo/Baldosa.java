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
package ar.net.argentum.cliente.mundo;

import ar.net.argentum.cliente.juego.Objeto;
import ar.net.argentum.cliente.motor.Animacion;

/**
 * Representa un casillero del mapa
 *
 * @see MapBlock
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Baldosa {

    /**
     * Los graficos de cada capa. Son 4 capas 1 - Representa el grafico del
     * suelo 2 - Representa un objeto decorativo que se superpone sobre toda la
     * capa 1. 3 - Representa un objeto decorativo que se superpone sobre la
     * capa 2 y 1. 4 - Representa el techo, esta capa no se dibuja si el usuario
     * esta bajo techo.
     */
    protected Animacion[] capa = new Animacion[4 + 1];
    /**
     * Animacion del objeto arrojado en el suelo
     */
    protected Animacion objgrh;
    /**
     * Animacion del efecto
     */
    protected Animacion efecto;
    /**
     * Objeto arrojado en el suelo
     */
    protected Objeto objeto;
    /**
     * ID del personaje que esta parado en esta baldosa
     */
    protected short charindex;
    /**
     * ID del NPC que esta parado en esta baldosa
     */
    protected short npcindex;
    /**
     * Posicion en el mundo a donde la persona tiene que ser teletransportada el
     * pisar esta baldosa.
     */
    protected Coordenada teletransporte;
    /**
     * Verdadero si la baldosa esta bloqueada
     */
    protected boolean bloqueado;
    /**
     * Atributos especiales de la baldosa
     */
    protected short trigger;

    /**
     * Generar una nueva baldosa
     */
    public Baldosa() {
        capa[1] = new Animacion();
        capa[2] = new Animacion();
        capa[3] = new Animacion();
        capa[4] = new Animacion();
        objgrh = new Animacion();
        efecto = new Animacion();
    }

    /**
     * Obtiene el grafico de la capa dada
     *
     * @param num_capa
     * @return
     */
    public Animacion getCapa(int num_capa) {
        return capa[num_capa];
    }

    /**
     * Establece el grafico de la capa dada
     *
     * @param num_capa
     * @param anim
     * @return
     */
    public Baldosa setCapa(int num_capa, Animacion anim) {
        this.capa[num_capa] = anim;
        return this;
    }

    /**
     * Obtiene el ID del personaje ubicado en esta baldosa
     *
     * @return
     */
    public int getCharindex() {
        return charindex;
    }

    /**
     * Establece el ID del personaje ubicado en esta baldosa
     *
     * @param charindex
     * @return
     */
    public Baldosa setCharindex(short charindex) {
        this.charindex = charindex;
        return this;
    }

    /**
     * Establece el ID del personaje ubicado en esta baldosa
     *
     * @param charindex
     * @return
     */
    public Baldosa setCharindex(int charindex) {
        return setCharindex((short) charindex);
    }

    /**
     * Devuelve verdadero si la baldosa esta bloqueada
     *
     * @return
     */
    public boolean isBloqueado() {
        return bloqueado;
    }

    /**
     * Bloquea o desbloquea la baldosa
     *
     * @param bloq Verdadero si esta bloqueado
     * @return
     */
    public Baldosa setBloqueado(boolean bloq) {
        this.bloqueado = bloq;
        return this;
    }

    /**
     * Establece atributos de la baldosa
     *
     * @param trigger
     * @return
     */
    public Baldosa setTrigger(short trigger) {
        this.trigger = trigger;
        return this;
    }

    /**
     * Obtiene la animacion para dibujar el objeto que esta en esta baldosa
     *
     * @return
     */
    public Animacion getAnimObjecto() {
        return objgrh;
    }

    /**
     * Obtiene el objeto que esta arrojado en esta baldosa
     *
     * @return
     */
    public Objeto getObjeto() {
        return objeto;
    }

    /**
     * Establece un objeto en esta baldosa
     *
     * @param objeto
     */
    public void setObjeto(Objeto objeto) {
        if (objeto == null) {
            this.objgrh = null;
            this.objeto = null;
            return;
        }
        this.objgrh = new Animacion(objeto.getGrh(), false);
        this.objeto = objeto;
    }

    public boolean isAgua() {
        Animacion anim = getCapa(1);
        if (anim == null) {
            return false;
        }
        int grafico = anim.getSprite().frames[0];
        return (grafico >= 1505 && grafico <= 1520)
                || (grafico >= 5665 && grafico <= 5680)
                || (grafico >= 13547 && grafico <= 13562);
    }

    public boolean isNavegable() {
        if (!isAgua()) {
            return false;
        }
        return getCapa(2) == null;
    }

    /**
     * @return Animacion del efecto activo en esta baldosa
     */
    public Animacion getEfecto() {
        return efecto;
    }

    /**
     * @param efecto Animacion del efecto a activar en esta baldosa
     */
    public void setEfecto(Animacion efecto) {
        this.efecto = efecto;
    }

    /**
     * @return the trigger
     */
    public short getTrigger() {
        return trigger;
    }
}
