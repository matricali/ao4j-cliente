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
package ar.net.argentum.cliente;

import static ar.net.argentum.cliente.motor.MotorGrafico.LOGGER;
import ar.net.argentum.cliente.motor.AnimArma;
import ar.net.argentum.cliente.motor.AnimCabeza;
import ar.net.argentum.cliente.motor.AnimCuerpo;
import ar.net.argentum.cliente.motor.AnimEfecto;
import ar.net.argentum.cliente.motor.AnimEscudo;
import ar.net.argentum.cliente.motor.Sprite;
import ar.net.argentum.general.UtilLegacy;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public abstract class Recursos {

    private static AnimCuerpo[] animCuerpos;
    private static AnimCabeza[] animCabezas;
    private static AnimCabeza[] animCascos;
    private static AnimArma[] animArmas;
    private static AnimEscudo[] animEscudos;
    private static AnimEfecto[] animEfectos;
    private static Sprite[] graficos;

    public static void cargar() {
        // Cargamos los graficos indexados
        cargarGraficos("recursos/datos/graficos.ind");
        // Cargamos las animaciones
        cargarArmas("recursos/datos/armas.ind");
        cargarCabezas("recursos/datos/cabezas.ind");
        cargarCascos("recursos/datos/cascos.ind");
        cargarCuerpos("recursos/datos/cuerpos.ind");
        cargarEscudos("recursos/datos/escudos.ind");
        cargarEfectos("recursos/datos/efectos.ind");
    }

    public static void cargarGraficos(String archivo) {
        LOGGER.info("Iniciando la carga de graficos...");
        try {

            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                f.seek(0);

                int version = UtilLegacy.bigToLittle(f.readInt());
                int cantidadGraficos = UtilLegacy.bigToLittle(f.readInt());

                float tempfloat;

                graficos = new Sprite[cantidadGraficos + 1];

                // El primer grafico es un grafico vacio.
                graficos[0] = new Sprite();

                int Grh = 0;
                while (Grh < cantidadGraficos) {
                    Grh = UtilLegacy.bigToLittle(f.readInt());
                    graficos[Grh] = new Sprite();
                    graficos[Grh].numFrames = UtilLegacy.bigToLittle(f.readShort());

                    if (graficos[Grh].numFrames <= 0) {
                        LOGGER.error("El grafico " + Grh + " tiene una cantidad de cuadros invalida.");
                        return;
                    }

                    graficos[Grh].frames = new int[graficos[Grh].numFrames + 1];

                    if (graficos[Grh].numFrames > 1) {
                        for (short frame = 1; frame <= graficos[Grh].numFrames; frame++) {
                            graficos[Grh].frames[frame] = UtilLegacy.bigToLittle(f.readInt());
                            if (graficos[Grh].frames[frame] <= 0) {
                                LOGGER.error("El grafico " + Grh + " tiene un cuadro invalido (" + frame + ").");
                                return;
                            }
                        }

                        tempfloat = UtilLegacy.bigToLittle(f.readFloat());
                        if (tempfloat <= 0) {
                            LOGGER.error("Error al cargar grafico " + Grh + ".");
                            return;
                        }
                        graficos[Grh].speed = tempfloat;

                        graficos[Grh].pixelHeight = graficos[graficos[Grh].frames[1]].pixelHeight;
                        if (graficos[Grh].pixelHeight <= 0) {
                            LOGGER.error("Grafico " + Grh + " - pixelHeight invalido.");
                            return;
                        }
                        graficos[Grh].pixelWidth = graficos[graficos[Grh].frames[1]].pixelWidth;
                        if (graficos[Grh].pixelWidth <= 0) {
                            LOGGER.error("Grafico " + Grh + " - pixelWidth invalido.");
                            return;
                        }
                        graficos[Grh].tileWidth = graficos[graficos[Grh].frames[1]].tileWidth;
                        if (graficos[Grh].tileWidth <= 0) {
                            LOGGER.error("Grafico " + Grh + " - tileWidth invalido.");
                            return;
                        }
                        graficos[Grh].tileHeight = graficos[graficos[Grh].frames[1]].tileHeight;
                        if (graficos[Grh].tileHeight <= 0) {
                            LOGGER.error("Grafico " + Grh + " - tileHeight invalido.");
                            return;
                        }
                    } else {
                        graficos[Grh].fileNum = UtilLegacy.bigToLittle(f.readInt());
                        if (graficos[Grh].fileNum <= 0) {
                            LOGGER.error("Grafico " + Grh + " - fileNum invalido.");
                            return;
                        }
                        graficos[Grh].sX = UtilLegacy.bigToLittle(f.readShort());
                        if (graficos[Grh].sX < 0) {
                            LOGGER.error("Grafico " + Grh + " - sX invalido.");
                            return;
                        }

                        graficos[Grh].sY = UtilLegacy.bigToLittle(f.readShort());
                        if (graficos[Grh].sY < 0) {
                            LOGGER.error("Grafico " + Grh + " - sY invalido.");
                            return;
                        }

                        graficos[Grh].pixelWidth = UtilLegacy.bigToLittle(f.readShort());
                        if (graficos[Grh].pixelWidth <= 0) {
                            LOGGER.error("Grafico " + Grh + " - pixelWidth invalido.");
                            return;
                        }

                        graficos[Grh].pixelHeight = UtilLegacy.bigToLittle(f.readShort());
                        if (graficos[Grh].pixelHeight <= 0) {
                            LOGGER.error("Grafico " + Grh + " - pixelHeight invalido.");
                            return;
                        }

                        graficos[Grh].tileWidth = (float) graficos[Grh].pixelWidth / 32;
                        graficos[Grh].tileHeight = (float) graficos[Grh].pixelHeight / 32;

                        graficos[Grh].frames[1] = (short) Grh;
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
        LOGGER.info("Carga de graficos finalizada.");
    }

    public static void cargarCabezas(String archivo) {
        try {
            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                f.seek(0);

                short cantCabezas;
                byte[] cabecera = new byte[263];

                f.read(cabecera);
                cantCabezas = UtilLegacy.bigToLittle(f.readShort());

                animCabezas = new AnimCabeza[cantCabezas + 1];
                short a1, a2, a3, a4;

                for (int i = 1; i <= cantCabezas; ++i) {
                    a1 = UtilLegacy.bigToLittle(f.readShort());
                    a2 = UtilLegacy.bigToLittle(f.readShort());
                    a3 = UtilLegacy.bigToLittle(f.readShort());
                    a4 = UtilLegacy.bigToLittle(f.readShort());

                    if (a1 != 0) {
                        animCabezas[i] = new AnimCabeza(graficos[a1], a1, a2, a3, a4, false);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public static void cargarCascos(String archivo) {
        try {
            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                f.seek(0);

                short cantCascos;
                byte[] cabecera = new byte[263];

                f.read(cabecera);
                cantCascos = UtilLegacy.bigToLittle(f.readShort());

                animCascos = new AnimCabeza[cantCascos + 1];
                short a1, a2, a3, a4;

                for (int i = 1; i <= cantCascos; ++i) {
                    a1 = UtilLegacy.bigToLittle(f.readShort());
                    a2 = UtilLegacy.bigToLittle(f.readShort());
                    a3 = UtilLegacy.bigToLittle(f.readShort());
                    a4 = UtilLegacy.bigToLittle(f.readShort());

                    if (a1 != 0) {
                        animCascos[i] = new AnimCabeza(graficos[a1], a1, a2, a3, a4, false);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public static void cargarCuerpos(String archivo) {
        try {
            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                f.seek(0);

                short cantCuerpos;
                byte[] cabecera = new byte[263];

                f.read(cabecera);
                cantCuerpos = UtilLegacy.bigToLittle(f.readShort());

                animCuerpos = new AnimCuerpo[cantCuerpos + 1];
                short a1, a2, a3, a4, off1, off2;

                for (int i = 1; i <= cantCuerpos; i++) {
                    a1 = UtilLegacy.bigToLittle(f.readShort());
                    a2 = UtilLegacy.bigToLittle(f.readShort());
                    a3 = UtilLegacy.bigToLittle(f.readShort());
                    a4 = UtilLegacy.bigToLittle(f.readShort());
                    off1 = UtilLegacy.bigToLittle(f.readShort());
                    off2 = UtilLegacy.bigToLittle(f.readShort());

                    if (a1 != 0) {
                        animCuerpos[i] = new AnimCuerpo(graficos[a1], a1, a2, a3, a4, off1, off2, false);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public static void cargarArmas(String archivo) {
        try {
            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                int cantArmas = (int) UtilLegacy.bigToLittle(f.readShort());
                animArmas = new AnimArma[cantArmas + 1];

                short a1, a2, a3, a4;
                for (int i = 1; i <= cantArmas; i++) {
                    a1 = UtilLegacy.bigToLittle(f.readShort());
                    a2 = UtilLegacy.bigToLittle(f.readShort());
                    a3 = UtilLegacy.bigToLittle(f.readShort());
                    a4 = UtilLegacy.bigToLittle(f.readShort());

                    if (a1 != 0) {
                        animArmas[i] = new AnimArma(graficos[a1], a1, a2, a3, a4, true);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public static void cargarEscudos(String archivo) {
        try {
            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                int numShields = UtilLegacy.bigToLittle(f.readShort());
                animEscudos = new AnimEscudo[numShields + 1];

                short a1, a2, a3, a4;
                for (int i = 1; i <= numShields; i++) {
                    a1 = UtilLegacy.bigToLittle(f.readShort());
                    a2 = UtilLegacy.bigToLittle(f.readShort());
                    a3 = UtilLegacy.bigToLittle(f.readShort());
                    a4 = UtilLegacy.bigToLittle(f.readShort());

                    if (a1 != 0) {
                        animEscudos[i] = new AnimEscudo(graficos[a1], a1, a2, a3, a4, false);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    public static void cargarEfectos(String archivo) {
        try {
            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {

                byte[] cabecera = new byte[263];
                f.read(cabecera);

                int numEfectos = UtilLegacy.bigToLittle(f.readShort());
                animEfectos = new AnimEfecto[numEfectos + 1];

                short a1, a2, a3, a4;
                for (int i = 1; i <= numEfectos; i++) {
                    a1 = UtilLegacy.bigToLittle(f.readShort());
                    a2 = UtilLegacy.bigToLittle(f.readShort());
                    a3 = UtilLegacy.bigToLittle(f.readShort());

                    if (a1 != 0) {
                        animEfectos[i] = new AnimEfecto(a1, a2, a3);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.fatal(null, ex);
        }
    }

    /**
     * Obtiene un grafico
     *
     * @param grhIndex ID del grafico
     * @return
     */
    public static Sprite getSprite(int grhIndex) {
        if (grhIndex > graficos.length) {
            return new Sprite();
        }
        return graficos[grhIndex];
    }

    public static AnimArma getArma(int id) {
        return animArmas[id];
    }

    public static AnimCabeza getCabeza(int id) {
        return animCabezas[id];
    }

    public static AnimCabeza getCasco(int id) {
        return animCascos[id];
    }

    public static AnimCuerpo getCuerpo(int id) {
        return animCuerpos[id];
    }

    public static AnimEscudo getEscudo(int id) {
        return animEscudos[id];
    }

    public static AnimEfecto getEfecto(int id) {
        return animEfectos[id];
    }

}
