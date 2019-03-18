package ar.net.argentum.cliente.motor.user;

import ar.net.argentum.cliente.motor.gamedata.GameData;
import ar.net.argentum.cliente.motor.gamedata.Animacion;
import ar.net.argentum.cliente.motor.gamedata.Posicion;

/**
 * Representa al usuario que esta jugando en esta sesion
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Usuario {

    /**
     * Intancia del juego
     */
    protected final GameData game;

    protected String nombre;
    protected Posicion posicion = new Posicion();
    protected Inventario inventario;

    protected int minHP;
    protected int maxHP;
    protected int minMana;
    protected int maxMana;
    protected int minStamina;
    protected int maxStamina;
    protected int minHambre;
    protected int maxHambre;
    protected int minSed;
    protected int maxSed;

    public Usuario(GameData game) {
        this(game, "");
    }

    public Usuario(GameData game, String nombre) {
        this.game = game;
        this.nombre = nombre;
        this.inventario = new Inventario(20);
    }

    public Animacion reference(Animacion ref) {
        return ref;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public Usuario setPosicion(int x, int y) {
        this.posicion = new Posicion(x, y);
        return this;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the minHP
     */
    public int getMinHP() {
        return minHP;
    }

    /**
     * @param minHP the minHP to set
     */
    public void setMinHP(int minHP) {
        this.minHP = minHP;
    }

    /**
     * @return the maxHP
     */
    public int getMaxHP() {
        return maxHP;
    }

    /**
     * @param maxHP the maxHP to set
     */
    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public Inventario getInventario() {
        return inventario;
    }

    /**
     * @return the minMana
     */
    public int getMinMana() {
        return minMana;
    }

    /**
     * @param minMana the minMana to set
     */
    public void setMinMana(int minMana) {
        this.minMana = minMana;
    }

    /**
     * @return the maxMana
     */
    public int getMaxMana() {
        return maxMana;
    }

    /**
     * @param maxMana the maxMana to set
     */
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    /**
     * @return the minStamina
     */
    public int getMinStamina() {
        return minStamina;
    }

    /**
     * @param minStamina the minStamina to set
     */
    public void setMinStamina(int minStamina) {
        this.minStamina = minStamina;
    }

    /**
     * @return the maxStamina
     */
    public int getMaxStamina() {
        return maxStamina;
    }

    /**
     * @param maxStamina the maxStamina to set
     */
    public void setMaxStamina(int maxStamina) {
        this.maxStamina = maxStamina;
    }

    /**
     * @return the minHambre
     */
    public int getMinHambre() {
        return minHambre;
    }

    /**
     * @param minHambre the minHambre to set
     */
    public void setMinHambre(int minHambre) {
        this.minHambre = minHambre;
    }

    /**
     * @return the maxHambre
     */
    public int getMaxHambre() {
        return maxHambre;
    }

    /**
     * @param maxHambre the maxHambre to set
     */
    public void setMaxHambre(int maxHambre) {
        this.maxHambre = maxHambre;
    }

    /**
     * @return the minSed
     */
    public int getMinSed() {
        return minSed;
    }

    /**
     * @param minSed the minSed to set
     */
    public void setMinSed(int minSed) {
        this.minSed = minSed;
    }

    /**
     * @return the maxSed
     */
    public int getMaxSed() {
        return maxSed;
    }

    /**
     * @param maxSed the maxSed to set
     */
    public void setMaxSed(int maxSed) {
        this.maxSed = maxSed;
    }
}
