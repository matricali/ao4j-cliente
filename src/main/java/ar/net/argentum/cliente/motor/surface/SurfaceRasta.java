package ar.net.argentum.cliente.motor.surface;

import ar.net.argentum.cliente.motor.gamedata.Sprite;
import java.util.HashMap;
import java.util.Map;

public class SurfaceRasta implements ISurface {

    protected HashMap<Integer, Textura> texturas;

    public SurfaceRasta() {
        this.texturas = new HashMap<>();
    }

    @Override
    public void initialize() {
        System.out.println("Inicializando un nuevo manejador de texturas...");
    }

    @Override
    public Textura getTextura(Sprite sprite) {
        return getTextura(sprite.fileNum);
    }

    @Override
    public Textura getTextura(int fileNum) {
        Textura tx = texturas.get(fileNum);
        if (tx == null) {
            // No teniamos la textura cargada, vamos a crear una nueva
            return cargarTextura(fileNum);
        }
        return tx;
    }

    @Override
    public Textura cargarTextura(int fileNum) {
        System.out.println("Cargando nueva textura (" + fileNum + ".png)");
        Textura tx = TexturaOpenGL.loadTexture("recursos/graficos/" + fileNum + ".png");
        texturas.put(fileNum, tx);
        return tx;
    }

    @Override
    public void destruir() {
        System.out.println("Destruyendo texturas...");
        for (Map.Entry<Integer, Textura> elemento : texturas.entrySet()) {
            Textura tx = elemento.getValue();
            System.out.println("Destruyendo textura " + elemento.getKey());
            tx.borrar();
        }
    }

}
