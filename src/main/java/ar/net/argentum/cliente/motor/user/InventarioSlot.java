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
package ar.net.argentum.cliente.motor.user;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class InventarioSlot {

    protected int id;
    protected int grhIndex;
    protected String nombre;
    protected int cantidad;
    protected boolean equipado;

    public InventarioSlot(int id, int grhIndex, String nombre, int cantidad, boolean equipado) {
        this.id = id;
        this.grhIndex = grhIndex;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.equipado = equipado;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the grhIndex
     */
    public int getGrhIndex() {
        return grhIndex;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return the cantidad
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * @return the equipado
     */
    public boolean isEquipado() {
        return equipado;
    }
    
    
}
