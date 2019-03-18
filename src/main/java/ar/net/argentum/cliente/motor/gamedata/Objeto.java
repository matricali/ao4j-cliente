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
 * Representa una pila de objetos.
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Objeto {

    /**
     * Nombre del objeto
     */
    protected String nombre;
    /**
     * ID del objeto
     */
    protected short id;
    /**
     * Cantidad de objetos
     */
    protected short cantidad;
    /**
     * Grafico
     */
    protected short grh;

    public Objeto(int id, int grh, String nombre, int cantidad) {
        this((short) id, (short) grh, nombre, (short) cantidad);
    }

    public Objeto(short id, short grh, String nombre, short cantidad) {
        this.id = id;
        this.grh = grh;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return the id
     */
    public short getId() {
        return id;
    }

    /**
     * @return the cantidad
     */
    public short getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(short cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(int cantidad) {
        setCantidad((short) cantidad);
    }

    /**
     * @return the grh
     */
    public short getGrh() {
        return grh;
    }
}
