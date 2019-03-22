package ar.net.argentum.cliente.motor.texturas;

import ar.net.argentum.cliente.motor.Sprite;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class TexturasDB implements ITexturas {

    private static final Logger LOGGER = Logger.getLogger(TexturasDB.class);
    protected HashMap<Integer, Textura> texturas;

    public TexturasDB() {
        this.texturas = new HashMap<>();
    }

    @Override
    public void inicializar() {
        LOGGER.info("Inicializando un nuevo manejador de texturas...");
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
        LOGGER.info("Cargando nueva textura (" + fileNum + ".png)");
        Textura tx = TexturaOpenGL.loadTexture("recursos/graficos/" + fileNum + ".png");
        texturas.put(fileNum, tx);
        return tx;
    }

    @Override
    public void destruir() {
        LOGGER.info("Destruyendo texturas...");
        for (Map.Entry<Integer, Textura> elemento : texturas.entrySet()) {
            Textura tx = elemento.getValue();
            LOGGER.info("Destruyendo textura " + elemento.getKey());
            tx.borrar();
        }
    }

}
