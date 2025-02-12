package org.mqureshi.engine.terrain;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.stb.STBImage.*;

public class ColorMapLoader {
    private int width;
    private int height;
    private ByteBuffer imageData;

    public ColorMapLoader(String filePath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int[] w = new int[1];
            int[] h = new int[1];
            int[] channels = new int[1];
            stbi_set_flip_vertically_on_load(true);
            imageData = stbi_load(filePath, w, h, channels, 3);

            if (imageData == null) {
                throw new RuntimeException("Failed to load color map: " + filePath);
            }

            width = w[0];
            height = h[0];
        }
    }

    public List<Vector3f> getColors(int vertexCount) {
        List<Vector3f> colors = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            int pixelIndex = (i % width + (i / width) * width) * 3;
            float r = (imageData.get(pixelIndex) & 0xFF) / 255.0f;
            float g = (imageData.get(pixelIndex + 1) & 0xFF) / 255.0f;
            float b = (imageData.get(pixelIndex + 2) & 0xFF) / 255.0f;
            colors.add(new Vector3f(r, g, b));
        }
        return colors;
    }

    public void cleanup() {
        if (imageData != null) {
            stbi_image_free(imageData);
        }
    }
}

