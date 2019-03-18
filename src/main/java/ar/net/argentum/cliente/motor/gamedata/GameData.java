package ar.net.argentum.cliente.motor.gamedata;

import ar.net.argentum.cliente.motor.user.Usuario;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GameData {

    private static GameData instancia;

    private static int bigToLittle_Int(int bigendian) {
        ByteBuffer buf = ByteBuffer.allocate(4);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putInt(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.getInt(0);
    }

    private static float bigToLittle_Float(float bigendian) {
        ByteBuffer buf = ByteBuffer.allocate(4);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putFloat(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.getFloat(0);
    }

    private static short bigToLittle_Short(short bigendian) {
        ByteBuffer buf = ByteBuffer.allocate(2);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.getShort(0);
    }

    private static byte bigToLittle_Byte(byte bigendian) {
        ByteBuffer buf = ByteBuffer.allocate(1);

        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put(bigendian);

        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.get(0);
    }

    public static GameData getInstancia() {
        if (instancia == null) {
            instancia = new GameData();
        }
        return instancia;
    }
    private AnimCuerpo[] d_cuerpos;
    private AnimCabeza[] d_cabezas;
    private AnimCabeza[] d_cascos;
    private AnimArma[] d_armas;
    private AnimEscudo[] d_escudos;

    private Sprite[] grh_data;
    private Baldosa[][] map_data;
    public int last_char = 0;
    private int cantidadGraficos;
    protected Usuario usuario;
    protected int mapaActual = 0;

    private GameData() {
        this.usuario = new Usuario(this);
    }

    public void initialize() {
        System.out.println("Cargando y procesando archivos del juego...");

        System.out.println("Iniciando la carga de graficos...");
        cargarGraficos("recursos/datos/graficos.ind");
        System.out.println("Carga de graficos finalizada.");

        System.out.println("Cargando animaciones de las cabezas...");
        cargarCabezas("recursos/datos/cabezas.ind");
        System.out.println("Carga de cabezas finalizada.");

        System.out.println("Cargando animaciones de los cascos...");
        cargarCascos("recursos/datos/cascos.ind");
        System.out.println("Carga de cascos finalizada.");

        System.out.println("Cargando animaciones de los cuerpos...");
        cargarCuerpos("recursos/datos/cuerpos.ind");
        System.out.println("Carga de cuerpos finalizada.");

        System.out.println("Cargando animaciones de las armas...");
        cargarArmas("recursos/datos/armas.ind");
        System.out.println("Carga de armas finalizada.");

        System.out.println("Cargando animaciones de los escudos...");
        cargarEscudos("recursos/datos/escudos.ind");
        System.out.println("Carga de escudos finalizada.");
    }

    private void cargarGraficos(String archivo) {
        try {

            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                f.seek(0);

                int version = bigToLittle_Int(f.readInt());
                cantidadGraficos = bigToLittle_Int(f.readInt());

                float tempfloat;

                grh_data = new Sprite[cantidadGraficos + 1];

                // El primer grafico es un grafico vacio.
                grh_data[0] = new Sprite();

                int Grh = 0;
                while (Grh < cantidadGraficos) {
                    Grh = bigToLittle_Int(f.readInt());
                    grh_data[Grh] = new Sprite();
                    grh_data[Grh].numFrames = bigToLittle_Short(f.readShort());

                    if (grh_data[Grh].numFrames <= 0) {
                        System.err.println("ERROR: El grafico " + Grh + " tiene una cantidad de cuadros invalida.");
                        return;
                    }

                    grh_data[Grh].frames = new int[grh_data[Grh].numFrames + 1];

                    if (grh_data[Grh].numFrames > 1) {
                        for (short frame = 1; frame <= grh_data[Grh].numFrames; frame++) {
                            grh_data[Grh].frames[frame] = bigToLittle_Int(f.readInt());
                            if (grh_data[Grh].frames[frame] <= 0) {
                                System.err.println("ERROR: El grafico " + Grh + " tiene un cuadro invalido (" + frame + ").");
                                return;
                            }
                        }

                        tempfloat = bigToLittle_Float(f.readFloat());
                        if (tempfloat <= 0) {
                            System.err.println("ERROR: Error al cargar grafico " + Grh + ".");
                            return;
                        }
                        grh_data[Grh].speed = tempfloat;

                        grh_data[Grh].pixelHeight = grh_data[grh_data[Grh].frames[1]].pixelHeight;
                        if (grh_data[Grh].pixelHeight <= 0) {
                            System.err.println("ERROR: Grafico " + Grh + " - pixelHeight invalido.");
                            return;
                        }
                        grh_data[Grh].pixelWidth = grh_data[grh_data[Grh].frames[1]].pixelWidth;
                        if (grh_data[Grh].pixelWidth <= 0) {
                            System.err.println("ERROR: Grafico " + Grh + " - pixelWidth invalido.");
                            return;
                        }
                        grh_data[Grh].tileWidth = grh_data[grh_data[Grh].frames[1]].tileWidth;
                        if (grh_data[Grh].tileWidth <= 0) {
                            System.err.println("ERROR: Grafico " + Grh + " - tileWidth invalido.");
                            return;
                        }
                        grh_data[Grh].tileHeight = grh_data[grh_data[Grh].frames[1]].tileHeight;
                        if (grh_data[Grh].tileHeight <= 0) {
                            System.err.println("ERROR: Grafico " + Grh + " - tileHeight invalido.");
                            return;
                        }
                    } else {
                        grh_data[Grh].fileNum = bigToLittle_Int(f.readInt());
                        if (grh_data[Grh].fileNum <= 0) {
                            System.err.println("ERROR: Grafico " + Grh + " - fileNum invalido.");
                            return;
                        }
                        grh_data[Grh].sX = bigToLittle_Short(f.readShort());
                        if (grh_data[Grh].sX < 0) {
                            System.err.println("ERROR: Grafico " + Grh + " - sX invalido.");
                            return;
                        }

                        grh_data[Grh].sY = bigToLittle_Short(f.readShort());
                        if (grh_data[Grh].sY < 0) {
                            System.err.println("ERROR: Grafico " + Grh + " - sY invalido.");
                            return;
                        }

                        grh_data[Grh].pixelWidth = bigToLittle_Short(f.readShort());
                        if (grh_data[Grh].pixelWidth <= 0) {
                            System.err.println("ERROR: Grafico " + Grh + " - pixelWidth invalido.");
                            return;
                        }

                        grh_data[Grh].pixelHeight = bigToLittle_Short(f.readShort());
                        if (grh_data[Grh].pixelHeight <= 0) {
                            System.err.println("ERROR: Grafico " + Grh + " - pixelHeight invalido.");
                            return;
                        }

                        grh_data[Grh].tileWidth = (float) grh_data[Grh].pixelWidth / 32;
                        grh_data[Grh].tileHeight = (float) grh_data[Grh].pixelHeight / 32;

                        grh_data[Grh].frames[1] = (short) Grh;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarCabezas(String archivo) {
        try {
            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                f.seek(0);

                short cantCabezas;
                byte[] cabecera = new byte[263];

                f.read(cabecera);
                cantCabezas = bigToLittle_Short(f.readShort());

                this.d_cabezas = new AnimCabeza[cantCabezas + 1];
                short a1, a2, a3, a4;

                for (int i = 1; i <= cantCabezas; ++i) {
                    a1 = bigToLittle_Short(f.readShort());
                    a2 = bigToLittle_Short(f.readShort());
                    a3 = bigToLittle_Short(f.readShort());
                    a4 = bigToLittle_Short(f.readShort());

                    if (a1 != 0) {
                        d_cabezas[i] = new AnimCabeza(grh_data[a1], a1, a2, a3, a4, false);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarCascos(String archivo) {
        try {
            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                f.seek(0);

                short cantCascos;
                byte[] cabecera = new byte[263];

                f.read(cabecera);
                cantCascos = bigToLittle_Short(f.readShort());

                d_cascos = new AnimCabeza[cantCascos + 1];
                short a1, a2, a3, a4;

                for (int i = 1; i <= cantCascos; ++i) {
                    a1 = bigToLittle_Short(f.readShort());
                    a2 = bigToLittle_Short(f.readShort());
                    a3 = bigToLittle_Short(f.readShort());
                    a4 = bigToLittle_Short(f.readShort());

                    if (a1 != 0) {
                        d_cascos[i] = new AnimCabeza(grh_data[a1], a1, a2, a3, a4, false);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarCuerpos(String archivo) {
        try {
            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                f.seek(0);

                short cantCuerpos;
                byte[] cabecera = new byte[263];

                f.read(cabecera);
                cantCuerpos = bigToLittle_Short(f.readShort());

                d_cuerpos = new AnimCuerpo[cantCuerpos + 1];
                short a1, a2, a3, a4, off1, off2;

                for (int i = 1; i <= cantCuerpos; i++) {
                    a1 = bigToLittle_Short(f.readShort());
                    a2 = bigToLittle_Short(f.readShort());
                    a3 = bigToLittle_Short(f.readShort());
                    a4 = bigToLittle_Short(f.readShort());
                    off1 = bigToLittle_Short(f.readShort());
                    off2 = bigToLittle_Short(f.readShort());

                    if (a1 != 0) {
                        d_cuerpos[i] = new AnimCuerpo(grh_data[a1], a1, a2, a3, a4, i, i, false);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarArmas(String archivo) {
        try {
            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                int cantArmas = bigToLittle_Short(f.readShort());
                d_armas = new AnimArma[cantArmas + 1];

                short a1, a2, a3, a4;
                for (int i = 1; i <= cantArmas; i++) {
                    a1 = bigToLittle_Short(f.readShort());
                    a2 = bigToLittle_Short(f.readShort());
                    a3 = bigToLittle_Short(f.readShort());
                    a4 = bigToLittle_Short(f.readShort());

                    if (a1 != 0) {
                        d_armas[i] = new AnimArma(grh_data[a1], a1, a2, a3, a4, true);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarEscudos(String archivo) {
        try {
            try (RandomAccessFile f = new RandomAccessFile(archivo, "r")) {
                int numShields = bigToLittle_Short(f.readShort());
                d_escudos = new AnimEscudo[numShields + 1];

                short a1, a2, a3, a4;
                for (int i = 1; i <= numShields; i++) {
                    a1 = bigToLittle_Short(f.readShort());
                    a2 = bigToLittle_Short(f.readShort());
                    a3 = bigToLittle_Short(f.readShort());
                    a4 = bigToLittle_Short(f.readShort());

                    if (a1 != 0) {
                        d_escudos[i] = new AnimEscudo(grh_data[a1], a1, a2, a3, a4, false);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void cargarMapa(int num_mapa) {
        try {
            System.out.print("Iniciando carga del mapa " + num_mapa + "...");
            reiniciar();
            this.mapaActual = num_mapa;
            try (RandomAccessFile f = new RandomAccessFile("recursos/mapas/mapa" + num_mapa + ".map", "r")) {
                f.seek(0);

                short version = bigToLittle_Short(f.readShort());
                byte[] cabecera = new byte[263];
                f.read(cabecera);

                byte byflags = 0;
                short tempint;

                tempint = bigToLittle_Short(f.readShort());
                tempint = bigToLittle_Short(f.readShort());
                tempint = bigToLittle_Short(f.readShort());
                tempint = bigToLittle_Short(f.readShort());

                byte bloq;
                short tempshort;

                for (int y = 1; y <= 100; y++) {
                    for (int x = 1; x <= 100; x++) {
                        Baldosa md = new Baldosa();

                        byflags = bigToLittle_Byte(f.readByte());
                        bloq = (byte) (byflags & 1);
                        md.setBloqueado(bloq);

                        // Grafico de la capa 1
                        tempshort = bigToLittle_Short(f.readShort());
                        if (tempshort < cantidadGraficos) {
                            md.setCapa(1, new Animacion(tempshort, true));
                        }

                        // Graficoo de la capa 2
                        if ((byte) (byflags & 2) != 0) {
                            tempshort = bigToLittle_Short(f.readShort());
                            if (tempshort < cantidadGraficos) {
                                md.setCapa(2, new Animacion(tempshort, true));
                            }
                        } else {
                            md.setCapa(2, new Animacion());
                        }

                        // Grafico de la capa 3
                        if ((byte) (byflags & 4) != 0) {
                            tempshort = bigToLittle_Short(f.readShort());
                            if (tempshort < cantidadGraficos) {
                                md.setCapa(3, new Animacion(tempshort, true));
                            }
                        } else {
                            md.setCapa(3, new Animacion());
                        }

                        // Grafico de la capa 4
                        if ((byte) (byflags & 8) != 0) {
                            tempshort = bigToLittle_Short(f.readShort());
                            if (tempshort < cantidadGraficos) {
                                md.setCapa(4, new Animacion(tempshort, true));
                            }
                        } else {
                            md.setCapa(4, new Animacion());
                        }

                        if ((byte) (byflags & 16) != 0) {
                            md.setTrigger(bigToLittle_Short(f.readShort()));
                        } else {
                            md.setTrigger((short) 0);
                        }

                        if (md.getCharindex() > 0) {
                            //EraseChar;
                        }

                        map_data[x][y] = md;
                    }
                }
            }
            System.out.print("...loadMapData OK!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Sprite getSprite(int index) {
        return grh_data[index];
    }

    public Baldosa getBaldosa(int x, int y) {
        return map_data[x][y];
    }

    public Baldosa getBaldosa(Posicion pos) {
        return map_data[pos.x()][pos.y()];
    }

    public int getLastChar() {
        return last_char;
    }

    public AnimArma getArma(int index) {
        return d_armas[index];
    }

    public AnimCabeza getCabeza(int index) {
        return d_cabezas[index];
    }

    public AnimCabeza getCasco(int index) {
        return d_cascos[index];
    }

    public AnimCuerpo getCuerpo(int index) {
        return d_cuerpos[index];
    }

    public AnimEscudo getEscudo(int index) {
        return d_escudos[index];
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    public void reiniciar() {
        // Reiniciamos los tiles del mapa
        // Matriz de 100x100 baldosas
        this.map_data = new Baldosa[101][101];

        // Reiniciamos la data del usuario
        this.usuario = new Usuario(this);
    }
    
    public int getMapaActual() {
        return mapaActual;
    }
}
