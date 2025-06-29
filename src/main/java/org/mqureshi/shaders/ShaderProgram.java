package org.mqureshi.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public abstract class ShaderProgram {

    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public ShaderProgram(String vertexFile, String fragmentFile) {
        vertexShaderId = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShaderId = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vertexShaderId);
        GL20.glAttachShader(programId, fragmentShaderId);
        bindAttributes();
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.err.println("Program linking failed:");
            System.err.println(GL20.glGetProgramInfoLog(programId, 500));
            System.exit(-1);
        }
        GL20.glValidateProgram(programId);
    }

    public void start() {
        GL20.glUseProgram(programId);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void cleanUp() {
        stop();
        GL20.glDetachShader(programId, vertexShaderId);
        GL20.glDetachShader(programId, fragmentShaderId);
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);
        GL20.glDeleteProgram(programId);
    }

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programId, attribute, variableName);
    }

    protected abstract void bindAttributes();

    private static int loadShader(String path, int type) {
        StringBuilder shaderSource = new StringBuilder();

        try (InputStream in = ShaderProgram.class.getResourceAsStream(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n"); // remove the `//\n` comment if not needed
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.err.println("Could not read shader file: " + path);
            System.exit(-1);
        }

        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Shader compile error in " + path);
            System.err.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.exit(-1);
        }

        return shaderID;
    }


}
