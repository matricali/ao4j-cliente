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

import ar.net.argentum.cliente.motor.gamedata.AnimCuerpo;
import ar.net.argentum.cliente.motor.gamedata.Animacion;
import ar.net.argentum.cliente.motor.gamedata.AnimCabeza;
import ar.net.argentum.cliente.motor.gamedata.Posicion;
import ar.net.argentum.cliente.motor.gamedata.AnimEscudo;
import ar.net.argentum.cliente.motor.gamedata.AnimArma;
import ar.net.argentum.cliente.motor.user.Orientacion;

/**
 * Representa un personaje en el mundo
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Personaje {

    protected byte activo;
    protected String nombre;
    protected Orientacion orientacion;
    protected Posicion posicion = new Posicion(0, 0);

    protected AnimCuerpo cuerpo;
    protected AnimCabeza cabeza;
    protected AnimCabeza casco;
    protected AnimArma arma;
    protected AnimEscudo escudo;

    byte Moving;
    protected short scrollDirectionX;
    protected short scrollDirectionY;
    protected float moveOffsetX;
    protected float moveOffsetY;

    Animacion fX;
    short FxIndex;
    byte criminal;
    boolean attackable;
    boolean pie;
    boolean muerto;
    boolean invisible;
    byte priv;

    public Personaje() {

    }

    public Personaje(String nombre, Orientacion orientacion, Posicion pos, AnimCabeza cabeza, AnimCuerpo cuerpo, AnimCabeza casco, AnimArma arma, AnimEscudo escudo) {
        this.nombre = nombre;
        this.orientacion = orientacion;
        this.posicion = pos;
        this.cabeza = cabeza;
        this.cuerpo = cuerpo;
        this.casco = casco;
        this.arma = arma;
        this.escudo = escudo;
        this.Moving = 0;
        setMoveOffsetX(0);
        setMoveOffsetY(0);
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public Personaje setPosicion(int x, int y) {
        this.setPosicion(new Posicion(x, y));
        return this;
    }

    public Personaje setMovimiento(int x, int y, Orientacion orientacion) {
        this.setMoveOffsetX(-1 * (MotorGrafico.TILE_PIXEL_WIDTH * x));
        this.setMoveOffsetY(-1 * (MotorGrafico.TILE_PIXEL_HEIGHT * y));
        this.Moving = 1;
        this.setOrientacion(orientacion);
        this.setScrollDirectionX((short) x);
        this.setScrollDirectionY((short) y);
        return this;
    }

    public Personaje pararMovimiento() {
        getCuerpo().getAnimacion(getOrientacion()).reiniciar();
        getArma().getAnimacion(getOrientacion()).reiniciar();
        getEscudo().getAnimacion(getOrientacion()).reiniciar();

        this.Moving = 0;
        return this;
    }

    public boolean estaMoviendose() {
        return Moving != 0;
    }

    public boolean esVisible() {
        return !invisible;
    }

    public short getScrollDirectionX() {
        return scrollDirectionX;
    }

    /**
     * @return the MoveOffsetX
     */
    public float getMoveOffsetX() {
        return moveOffsetX;
    }

    /**
     * @param MoveOffsetX the MoveOffsetX to set
     */
    public void setMoveOffsetX(float MoveOffsetX) {
        this.moveOffsetX = MoveOffsetX;
    }

    /**
     * @return the MoveOffsetY
     */
    public float getMoveOffsetY() {
        return moveOffsetY;
    }

    /**
     * @param MoveOffsetY the MoveOffsetY to set
     */
    public void setMoveOffsetY(float MoveOffsetY) {
        this.moveOffsetY = MoveOffsetY;
    }

    /**
     * @return the scrollDirectionY
     */
    public short getScrollDirectionY() {
        return scrollDirectionY;
    }

    /**
     * @return the cuerpo
     */
    public AnimCuerpo getCuerpo() {
        return cuerpo;
    }

    /**
     * @return the cabeza
     */
    public AnimCabeza getCabeza() {
        return cabeza;
    }

    /**
     * @return the casco
     */
    public AnimCabeza getCasco() {
        return casco;
    }

    /**
     * @return the arma
     */
    public AnimArma getArma() {
        return arma;
    }

    /**
     * @return the escudo
     */
    public AnimEscudo getEscudo() {
        return escudo;
    }

    /**
     * @return the orientacion
     */
    public Orientacion getOrientacion() {
        return orientacion;
    }

    /**
     * @param scrollDirectionX the scrollDirectionX to set
     */
    public void setScrollDirectionX(short scrollDirectionX) {
        this.scrollDirectionX = scrollDirectionX;
    }

    /**
     * @param scrollDirectionY the scrollDirectionY to set
     */
    public void setScrollDirectionY(short scrollDirectionY) {
        this.scrollDirectionY = scrollDirectionY;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return the activo
     */
    public boolean isActivo() {
        return activo == 1;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(boolean activo) {
        this.activo = activo ? (byte) 1 : 0;
    }

    /**
     * @param posicion the posicion to set
     */
    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    /**
     * @param cuerpo the cuerpo to set
     */
    public void setCuerpo(AnimCuerpo cuerpo) {
        this.cuerpo = cuerpo;
    }

    /**
     * @param cabeza the cabeza to set
     */
    public void setCabeza(AnimCabeza cabeza) {
        this.cabeza = cabeza;
    }

    /**
     * @param casco the casco to set
     */
    public void setCasco(AnimCabeza casco) {
        this.casco = casco;
    }

    /**
     * @param arma the arma to set
     */
    public void setArma(AnimArma arma) {
        this.arma = arma;
    }

    /**
     * @param escudo the escudo to set
     */
    public void setEscudo(AnimEscudo escudo) {
        this.escudo = escudo;
    }

    /**
     * @param orientacion the orientacion to set
     */
    public void setOrientacion(Orientacion orientacion) {
        this.orientacion = orientacion;
    }
}
