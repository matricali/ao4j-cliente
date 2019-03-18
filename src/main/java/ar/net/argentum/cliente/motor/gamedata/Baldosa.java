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
package ar.net.argentum.cliente.motor.gamedata;

/**
 * Representa un casillero del mapa
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
    private Animacion[] capa = new Animacion[4 + 1];
    private short charindex;
    private Animacion objgrh;
    private short npcindex;
    protected Objeto objeto;
    /**
     * Posicion en el mundo a donde la persona tiene que ser teletransportada el
     * pisar esta baldosa.
     */
    private Coordenada teletransporte;
    private byte bloqueado;
    private short trigger;

    public Baldosa() {
        capa[1] = new Animacion();
        capa[2] = new Animacion();
        capa[3] = new Animacion();
        capa[4] = new Animacion();
        objgrh = new Animacion();
    }

    public Animacion getCapa(int num_capa) {
        return capa[num_capa];
    }

    public Baldosa setCapa(int num_capa, Animacion anim) {
        this.capa[num_capa] = anim;
        return this;
    }

    public int getCharindex() {
        return charindex;
    }

    public Baldosa setCharindex(short charindex) {
        this.charindex = charindex;
        return this;
    }
    
    public Baldosa setCharindex(int charindex) {
        return setCharindex((short) charindex);
    }

    public boolean isBloqueado() {
        return bloqueado == 1;
    }

    public Baldosa setBloqueado(byte bloq) {
        this.bloqueado = bloq;
        return this;
    }

    public Baldosa setTrigger(short trigger) {
        this.trigger = trigger;
        return this;
    }

    public Animacion getAnimObjecto() {
        return objgrh;
    }

    /**
     * @return the objeto
     */
    public Objeto getObjeto() {
        return objeto;
    }

    /**
     * @param objeto the objeto to set
     */
    public void setObjeto(Objeto objeto) {
        this.objgrh = new Animacion(objeto.getGrh(), false);
        this.objeto = objeto;
    }
}
