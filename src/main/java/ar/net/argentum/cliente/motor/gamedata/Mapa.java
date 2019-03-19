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

import ar.net.argentum.general.UtilLegacy;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.log4j.Logger;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Mapa {

    protected static final Logger LOGGER = Logger.getLogger(Mapa.class);
    protected final GameData juego;

    protected Baldosa[][] baldosas;
    protected int numMapa;
    protected int minXBorder = 5;
    protected int maxXBorder = 90;
    protected int minYBorder = 5;
    protected int maxYBorder = 90;

    public Mapa(GameData juego, int numMapa) {
        LOGGER.info("Iniciando carga del mapa " + numMapa + "...");

        this.juego = juego;
        this.baldosas = new Baldosa[101][101];
        this.numMapa = numMapa;

        try (RandomAccessFile f = new RandomAccessFile("recursos/mapas/mapa" + numMapa + ".map", "r")) {
            f.seek(0);

            short version = UtilLegacy.bigToLittle(f.readShort());
            byte[] cabecera = new byte[263];
            f.read(cabecera);

            byte byflags = 0;
            short tempint;

            tempint = UtilLegacy.bigToLittle(f.readShort());
            tempint = UtilLegacy.bigToLittle(f.readShort());
            tempint = UtilLegacy.bigToLittle(f.readShort());
            tempint = UtilLegacy.bigToLittle(f.readShort());

            byte bloq;
            short tempshort;

            for (int y = 1; y <= 100; y++) {
                for (int x = 1; x <= 100; x++) {
                    Baldosa md = new Baldosa();

                    byflags = UtilLegacy.bigToLittle(f.readByte());
                    bloq = (byte) (byflags & 1);
                    md.setBloqueado(bloq);

                    // Grafico de la capa 1
                    tempshort = UtilLegacy.bigToLittle(f.readShort());
                    // if (tempshort < cantidadGraficos) {
                    md.setCapa(1, new Animacion(tempshort, true));
                    // }

                    // Graficoo de la capa 2
                    if ((byte) (byflags & 2) != 0) {
                        tempshort = UtilLegacy.bigToLittle(f.readShort());
                        // if (tempshort < cantidadGraficos) {
                        md.setCapa(2, new Animacion(tempshort, true));
                        // }
                    } else {
                        md.setCapa(2, new Animacion());
                    }

                    // Grafico de la capa 3
                    if ((byte) (byflags & 4) != 0) {
                        tempshort = UtilLegacy.bigToLittle(f.readShort());
                        // if (tempshort < cantidadGraficos) {
                        md.setCapa(3, new Animacion(tempshort, true));
                        // }
                    } else {
                        md.setCapa(3, new Animacion());
                    }

                    // Grafico de la capa 4
                    if ((byte) (byflags & 8) != 0) {
                        tempshort = UtilLegacy.bigToLittle(f.readShort());
                        // if (tempshort < cantidadGraficos) {
                        md.setCapa(4, new Animacion(tempshort, true));
                        // }
                    } else {
                        md.setCapa(4, new Animacion());
                    }

                    if ((byte) (byflags & 16) != 0) {
                        md.setTrigger(UtilLegacy.bigToLittle(f.readShort()));
                    } else {
                        md.setTrigger((short) 0);
                    }

                    if (md.getCharindex() > 0) {
                        //EraseChar;
                    }

                    baldosas[x][y] = md;
                }
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        LOGGER.info("Carga del mapa finalizada.");
    }

    public Baldosa getBaldosa(int x, int y) {
        return baldosas[x][y];
    }

    public Baldosa getBaldosa(Posicion pos) {
        return baldosas[pos.x()][pos.y()];
    }

    /**
     * Verifica si la posicion dada es apta para nuestro personaje
     *
     * @param posicion
     * @return
     */
    public boolean isPosicionValida(Posicion posicion) {
        return isPosicionValida(posicion.x(), posicion.y());
    }

    /**
     * Verifica si la posicion dada es apta para nuestro personaje
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isPosicionValida(int x, int y) {
        // Verificamso los limites del mapa
        if (x < minXBorder || x > maxXBorder || y < minYBorder || y > maxXBorder) {
            return false;
        }

        Baldosa baldosa = baldosas[x][y];

        // Celda bloqueada?
        if (baldosa.isBloqueado()) {
            return false;
        }

        // Hay un personaje?
        if (baldosa.getCharindex() > 0) {
            // Es un fantasma?
            // @TODO: Si es un fantasma tengo que intercambiar la posicion con el
            return false;
        }

        // El usuario esta navegando? Es agua?
        if (juego.getUsuario().isNavegando() != baldosa.isAgua()) {
            return false;
        }

        return true;
    }

    /**
     * @return the numMapa
     */
    public int getNumMapa() {
        return numMapa;
    }
}
