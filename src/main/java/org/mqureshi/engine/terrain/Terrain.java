package org.mqureshi.engine.terrain;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import static org.lwjgl.opengl.GL30.*;

public class Terrain {
    private HeightmapLoader heightmapLoader;
    private ColorMapLoader colorMapLoader;
    private List<Vector3f> vertices;
    private List<Vector3f> colors;
    private List<Integer> indices;
    private int vaoId;
    private int vboId;
    private int colorVboId;
    private int eboId;

    public Terrain(String heightmapPath, String colormapPath) {
        heightmapLoader = new HeightmapLoader(heightmapPath);
        colorMapLoader = new ColorMapLoader(colormapPath);
        vertices = heightmapLoader.getVertices();
        colors = colorMapLoader.getColors(vertices.size());
        indices = heightmapLoader.getIndices();
        setupMesh();
    }

    private void setupMesh() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        FloatBuffer vertexBuffer = MemoryStack.stackMallocFloat(vertices.size() * 3);
        for (Vector3f vertex : vertices) {
            vertexBuffer.put(vertex.x).put(vertex.y).put(vertex.z);
        }
        vertexBuffer.flip();

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        FloatBuffer colorBuffer = MemoryStack.stackMallocFloat(colors.size() * 3);
        for (Vector3f color : colors) {
            colorBuffer.put(color.x).put(color.y).put(color.z);
        }
        colorBuffer.flip();

        colorVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(1);

        IntBuffer indexBuffer = MemoryStack.stackMallocInt(indices.size());
        for (int index : indices) {
            indexBuffer.put(index);
        }
        indexBuffer.flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    public void render() {
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteBuffers(colorVboId);
        glDeleteBuffers(eboId);
        glDeleteVertexArrays(vaoId);
    }

    public List<Vector3f> getVertices() { return vertices; }
    public List<Vector3f> getColors() { return colors; }
    public List<Integer> getIndices() { return indices; }
}
