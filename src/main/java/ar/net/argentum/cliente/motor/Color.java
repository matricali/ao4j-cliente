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
package ar.net.argentum.cliente.motor;

import org.joml.Vector3f;
import org.joml.Vector4f;

public final class Color {

    public static final Color WHITE = new Color(1f, 1f, 1f);
    public static final Color BLACK = new Color(0f, 0f, 0f);
    public static final Color RED = new Color(1f, 0f, 0f);
    public static final Color GREEN = new Color(0f, 1f, 0f);
    public static final Color BLUE = new Color(0f, 0f, 1f);

    /**
     * This value specifies the red component.
     */
    private float red;

    /**
     * This value specifies the green component.
     */
    private float green;

    /**
     * This value specifies the blue component.
     */
    private float blue;

    /**
     * This value specifies the transparency.
     */
    private float alpha;

    /**
     * The default color is black.
     */
    public Color() {
        this(0f, 0f, 0f);
    }

    /**
     * Creates a RGB-Color with an alpha value of 1.
     *
     * @param red The red component. Range from 0f to 1f.
     * @param green The green component. Range from 0f to 1f.
     * @param blue The blue component. Range from 0f to 1f.
     */
    public Color(float red, float green, float blue) {
        this(red, green, blue, 1f);
    }

    /**
     * Creates a RGBA-Color.
     *
     * @param red The red component. Range from 0f to 1f.
     * @param green The green component. Range from 0f to 1f.
     * @param blue The blue component. Range from 0f to 1f.
     * @param alpha The transparency. Range from 0f to 1f.
     */
    public Color(float red, float green, float blue, float alpha) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
        setAlpha(alpha);
    }

    /**
     * Creates a RGB-Color with an alpha value of 1.
     *
     * @param red The red component. Range from 0 to 255.
     * @param green The green component. Range from 0 to 255.
     * @param blue The blue component. Range from 0 to 255.
     */
    public Color(int red, int green, int blue) {
        this(red, green, blue, 1f);
    }

    /**
     * Creates a RGBA-Color.
     *
     * @param red The red component. Range from 0 to 255.
     * @param green The green component. Range from 0 to 255.
     * @param blue The blue component. Range from 0 to 255.
     * @param alpha The transparency. Range from 0 to 255.
     */
    public Color(int red, int green, int blue, int alpha) {
        setRed(red);
        setGreen(green);
        setBlue(blue);
        setAlpha(alpha);
    }

    /**
     * Returns the red component.
     *
     * @return The red component.
     */
    public float getRed() {
        return red;
    }

    /**
     * Sets the red component.
     *
     * @param red The red component. Range from 0f to 1f.
     */
    public void setRed(float red) {
        if (red < 0f) {
            red = 0f;
        }
        if (red > 1f) {
            red = 1f;
        }
        this.red = red;
    }

    /**
     * Sets the red component.
     *
     * @param red The red component. Range from 0 to 255.
     */
    public void setRed(int red) {
        setRed(red / 255f);
    }

    /**
     * Returns the green component.
     *
     * @return The green component.
     */
    public float getGreen() {
        return green;
    }

    /**
     * Sets the green component.
     *
     * @param green The green component. Range from 0f to 1f.
     */
    public void setGreen(float green) {
        if (green < 0f) {
            green = 0f;
        }
        if (green > 1f) {
            green = 1f;
        }
        this.green = green;
    }

    /**
     * Sets the green component.
     *
     * @param green The green component. Range from 0 to 255.
     */
    public void setGreen(int green) {
        setGreen(green / 255f);
    }

    /**
     * Returns the blue component.
     *
     * @return The blue component.
     */
    public float getBlue() {
        return blue;
    }

    /**
     * Sets the blue component.
     *
     * @param blue The blue component. Range from 0f to 1f.
     */
    public void setBlue(float blue) {
        if (blue < 0f) {
            blue = 0f;
        }
        if (blue > 1f) {
            blue = 1f;
        }
        this.blue = blue;
    }

    /**
     * Sets the blue component.
     *
     * @param blue The blue component. Range from 0 to 255.
     */
    public void setBlue(int blue) {
        setBlue(blue / 255f);
    }

    /**
     * Returns the transparency.
     *
     * @return The transparency.
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Sets the transparency.
     *
     * @param alpha The transparency. Range from 0f to 1f.
     */
    public void setAlpha(float alpha) {
        if (alpha < 0f) {
            alpha = 0f;
        }
        if (alpha > 1f) {
            alpha = 1f;
        }
        this.alpha = alpha;
    }

    /**
     * Sets the transparency.
     *
     * @param alpha The transparency. Range from 0 to 255.
     */
    public void setAlpha(int alpha) {
        setAlpha(alpha / 255f);
    }

    /**
     * Returns the color as a (x,y,z)-Vector.
     *
     * @return The color as vec3.
     */
    public Vector3f toVector3f() {
        return new Vector3f(red, green, blue);
    }

    /**
     * Returns the color as a (x,y,z,w)-Vector.
     *
     * @return The color as vec4.
     */
    public Vector4f toVector4f() {
        return new Vector4f(red, green, blue, alpha);
    }
}
