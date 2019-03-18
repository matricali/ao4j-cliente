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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Jorge Matricali <jorgematricali@gmail.com>
 */
public class Utils {

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param file the resource to read
     * @param bufferSize the initial buffer size
     *
     * @return the resource data
     *
     * @throws IOException if an IO error occurs
     */
    public static ByteBuffer ioResourceToByteBuffer(File file, int bufferSize) throws IOException {
        ByteBuffer buffer;

        FileInputStream fis = new FileInputStream(file);
        FileChannel fc = fis.getChannel();
        buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        fc.close();
        fis.close();

        return buffer;
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param stream the stream to be read
     * @param bufferSize the initial buffer size
     *
     * @return the resource data
     *
     * @throws IOException if an IO error occurs
     */
    public static ByteBuffer ioResourceToByteBuffer(InputStream stream, int bufferSize) throws IOException {
        ByteBuffer buffer;

        buffer = BufferUtils.createByteBuffer(bufferSize);

        try {
            byte[] buf = new byte[8192];
            while (true) {
                int bytes = stream.read(buf, 0, buf.length);
                if (bytes == -1) {
                    break;
                }
                if (buffer.remaining() < bytes) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                }
                buffer.put(buf, 0, bytes);
            }
            buffer.flip();
        } finally {
            stream.close();
        }

        return buffer;
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource the resource to read
     * @param bufferSize the initial buffer size
     *
     * @return the resource data
     *
     * @throws IOException if an IO error occurs
     */
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        System.out.println(url);
        File file = new File(url.getFile());
        if (file.isFile()) {
            return ioResourceToByteBuffer(file, bufferSize);
        } else {
            buffer = BufferUtils.createByteBuffer(bufferSize);
            InputStream source = url.openStream();
            if (source == null) {
                throw new FileNotFoundException(resource);
            }
            try {
                byte[] buf = new byte[8192];
                while (true) {
                    int bytes = source.read(buf, 0, buf.length);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() < bytes) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                    }
                    buffer.put(buf, 0, bytes);
                }
                buffer.flip();
            } finally {
                source.close();
            }
        }
        return buffer;
    }
}
