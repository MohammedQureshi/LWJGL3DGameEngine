package org.mqureshi.toolbox;

import org.mqureshi.renderEngine.ObjLoader;

import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryUtil.memAlloc;

public class Utils {

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) {
        try (InputStream source = ObjLoader.class.getResourceAsStream(resource)) {
            if (source == null) throw new RuntimeException("Resource not found: " + resource);
            byte[] bytes = source.readAllBytes();
            ByteBuffer buffer = memAlloc(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            return buffer;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resource to buffer", e);
        }
    }
}