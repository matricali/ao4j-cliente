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
package ar.net.argentum.cliente.interfaz.componentes;

import ar.net.argentum.cliente.interfaz.eventos.OnTextFieldEventPerformed;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkPluginFilter;
import org.lwjgl.nuklear.NkPluginFilterI;
import org.lwjgl.nuklear.Nuklear;
import static org.lwjgl.nuklear.Nuklear.*;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class TextField {

    private int options;
    private final int maxLength;
    private final ByteBuffer content; // Nuklear puts the data in here
    private final IntBuffer length; // Nuklear writes the length of the string in here
    private final NkPluginFilterI filter; // Restrict what the user can type
    private OnTextFieldEventPerformed callback;
    protected boolean focused;

    public TextField(int maxLength, boolean multiline, String def) {
        this.maxLength = maxLength;
        options = NK_EDIT_SIMPLE;
        if (multiline) {
            options |= NK_EDIT_MULTILINE;
        }
        // Since we're using ASCII, each character just takes one byte.
        // We use maxLength + 1 because Nuklear seems to omit the last character.
        this.content = BufferUtils.createByteBuffer(maxLength + 1);

        // The IntBuffer is size 1 because we only need one int
        this.length = BufferUtils.createIntBuffer(1); // BufferUtils from LWJGL

        if (!def.isEmpty()) {
            setValue(def);
        }

        // Setup a filter to restrict to ASCII
        this.filter = NkPluginFilter.create(Nuklear::nnk_filter_ascii);
    }

    public TextField(int maxLength, boolean multiline) {
        this(maxLength, multiline, "");
    }

    public TextField(int maxLength, boolean multiline, OnTextFieldEventPerformed callback) {
        this(maxLength, multiline, "");
        options |= NK_EDIT_SIG_ENTER;
        this.callback = callback;
    }

    /**
     * This method uses Nuklear to draw the text field
     *
     * @param context
     */
    public void render(NkContext context) {
        if (nk_widget_is_mouse_clicked(context, NK_BUTTON_LEFT)) {
            nk_edit_focus(context, NK_EDIT_DEFAULT);
        }
        // No olvidarse de usar `maxLength + 1` porque Nuklear omite el ultimo caracter
        int ret = nk_edit_string(context, options, content, length, maxLength + 1, filter);
        // Manejamos los eventos que nos interesa
        if (callback != null) {
            if ((ret & NK_EDIT_COMMITED) > 0) {
                callback.onCommit(this);
            }
        }
        this.focused = (ret & NK_EDIT_ACTIVE) != 0;
    }

    /**
     * This method returns the text that the user typed in
     *
     * @return
     */
    public String getValue() {
        // The way to get a string from a ByteBuffer is to pull out an array of
        // bytes and pass it into the String constructor.
        content.mark(); // Mark the buffer so that we can return the pointer here when we're done
        byte[] bytes = new byte[length.get(0)];
        content.get(bytes, 0, length.get(0));
        content.reset(); // Return to the previous marker so that Nuklear can write here again
        String out = new String(bytes, Charset.forName("ASCII"));
        return out;
    }

    public void appendString(String texto) {
        int newLen = length.get(0) + texto.length();
        if (newLen <= maxLength) {
            int n = length.get(0);
            for (int i = 0; n < newLen; ++i, ++n) {
                content.putChar(n, texto.charAt(i));
            }
            content.putChar(n, '\u0000');
            length.put(0, newLen);
        }
    }

    public void setValue(String texto) {
        int len = texto.length();
        int i;
        for (i = 0; i < len; ++i) {
            content.putChar(i, texto.charAt(i));
        }
        content.putChar(i, '\u0000');
        length.put(0, len);
    }

    public void reset() {
        setValue("");
    }

    /**
     * @return the focused
     */
    public boolean isFocused() {
        return focused;
    }
}
