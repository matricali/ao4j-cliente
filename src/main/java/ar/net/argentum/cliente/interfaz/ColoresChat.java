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
package ar.net.argentum.cliente.interfaz;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public enum ColoresChat {
    NEGRO('0', 0x00),
    AZUL_OSCURO('1', 0x1),
    VERDE_OSCURO('2', 0x2),
    TURQUESA_OSCURO('3', 0x3),
    ROJO_OSCURO('4', 0x4),
    PURPURA('5', 0x5),
    AMARILLO_OSCURO('6', 0x6),
    GRIS('7', 0x7),
    GRIS_OSCURO('8', 0x8),
    AZUL('9', 0x9),
    VERDE('a', 0xA),
    TURQUESA('b', 0xB),
    ROJO('c', 0xC),
    MAGENTA('d', 0xD),
    AMARILLO('e', 0xE),
    BLANCO('f', 0xF),
    ALEATORIO('k', 0x10, true),
    NEGRITA('l', 0x11, true),
    TACHADO('m', 0x12, true),
    SUBRAYADO('n', 0x13, true),
    CURSIVA('o', 0x14, true),
    RESET('r', 0x15);

    /**
     * El carácter especial que prefija todos los códigos de color de chat.
     * Utilice esto si necesita convertir dinámicamente códigos de color de su
     * formato personalizado.
     */
    public static final char COLOR_CHAR = '\u00A7';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");
    private static final Pattern SPLIT_COLOR_PATTERN = Pattern.compile("(?<color>§[0-9A-FK-OR])?(?<texto>[^§]*)?");
    private final static Map<Integer, ColoresChat> BY_ID = new HashMap();
    private final static Map<Character, ColoresChat> BY_CHAR = new HashMap();

    static {
        for (ColoresChat color : values()) {
            BY_ID.put(color.intCodigo, color);
            BY_CHAR.put(color.codigo, color);
        }
    }

    public static ColoresChat getByChar(char codigo) {
        return BY_CHAR.get(codigo);
    }

    public static ColoresChat getByChar(String codigo) {
        return BY_CHAR.get(codigo.charAt(0));
    }

    public static String eliminarColores(final String entrada) {
        if (entrada == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(entrada).replaceAll("");
    }

    public static Matcher separarColores(final String entrada) {
        if (entrada == null) {
            return null;
        }

        return SPLIT_COLOR_PATTERN.matcher(entrada);
    }

    private final int intCodigo;
    private final char codigo;
    private final boolean esFormato;
    private final String toString;

    private ColoresChat(char codigo, int intCodigo) {
        this(codigo, intCodigo, false);
    }

    private ColoresChat(char codigo, int intCodigo, boolean esFormato) {
        this.codigo = codigo;
        this.intCodigo = intCodigo;
        this.esFormato = esFormato;
        this.toString = new String(new char[]{COLOR_CHAR, codigo});
    }

    public char getChar() {
        return codigo;
    }

    @Override
    public String toString() {
        return toString;
    }

    public boolean esFormato() {
        return esFormato;
    }

    public boolean esColor() {
        return !esFormato && this != RESET;
    }

}
