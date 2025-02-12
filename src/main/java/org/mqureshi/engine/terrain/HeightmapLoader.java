package org.mqureshi.engine.terrain;

import org.lwjgl.system.MemoryStack;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.stb.STBImage.*;

import org.joml.Vector2f;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class HeightmapLoader {
    private int width, height;
    private float[][] heightData;
    private List<Vector3f> vertices;
    private List<Vector2f> texCoords;
    private List<Integer> indices;

    public HeightmapLoader(String heightmapPath) {
        loadHeightmap(heightmapPath);
        generateTerrainMesh();
    }

    private void loadHeightmap(String heightmapPath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer imageBuffer = stbi_load(heightmapPath, w, h, channels, 1);
            if (imageBuffer == null) {
                throw new RuntimeException("Failed to load heightmap: " + stbi_failure_reason());
            }

            width = w.get(0);
            height = h.get(0);
            heightData = new float[width][height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixelValue = imageBuffer.get(y * width + x) & 0xFF;
                    heightData[x][y] = pixelValue / 255.0f * 10.0f; // Scale height
                }
            }

            stbi_image_free(imageBuffer);
        }
    }

    private void generateTerrainMesh() {
        vertices = new ArrayList<>();
        texCoords = new ArrayList<>();
        indices = new ArrayList<>();

        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                float worldX = x;
                float worldZ = z;
                float worldY = heightData[x][z];

                vertices.add(new Vector3f(worldX, worldY, worldZ));
                texCoords.add(new Vector2f(x / (float) width, z / (float) height));
            }
        }

        for (int z = 0; z < height - 1; z++) {
            for (int x = 0; x < width - 1; x++) {
                int topLeft = (z * width) + x;
                int topRight = topLeft + 1;
                int bottomLeft = ((z + 1) * width) + x;
                int bottomRight = bottomLeft + 1;

                indices.add(topLeft);
                indices.add(bottomLeft);
                indices.add(topRight);

                indices.add(topRight);
                indices.add(bottomLeft);
                indices.add(bottomRight);
            }
        }
    }

    public float getHeight(int x, int y) {
        return heightData[x][y];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<Vector3f> getVertices() { return vertices; }
    public List<Vector2f> getTexCoords() { return texCoords; }
    public List<Integer> getIndices() { return indices; }
}

