package org.mqureshi.engine.gui;

import imgui.ImDrawData;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class GuiMesh {

    private final int indicesVBO;
    private final int vaoId;
    private final int verticesVBO;

    public GuiMesh() {
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Single VBO
        verticesVBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, verticesVBO);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, ImDrawData.sizeOfImDrawVert(), 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, ImDrawData.sizeOfImDrawVert(), 8);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, ImDrawData.sizeOfImDrawVert(), 16);

        indicesVBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(indicesVBO);
        glDeleteBuffers(verticesVBO);
        glDeleteVertexArrays(vaoId);
    }

    public int getIndicesVBO() {
        return indicesVBO;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVerticesVBO() {
        return verticesVBO;
    }

}
