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

import ar.net.argentum.cliente.motor.gamedata.Objeto;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Inventario {
    InventarioSlot objetos[];
    
    public Inventario(int huecos) {
        this.objetos = new InventarioSlot[huecos];
    }
    
    public final InventarioSlot getSlot(int slot) {
        return objetos[slot];
    }
    
    public void setSlot(int slot, InventarioSlot objeto) {
        objetos[slot] = objeto;
    }
    
    public final InventarioSlot[] getObjetos() {
        return objetos;
    }
}
