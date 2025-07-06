package org.mqureshi.renderEngine;

import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.mqureshi.models.RawModel;
import org.mqureshi.toolbox.Utils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class ObjLoader {

    public static RawModel loadObjModel(String resourcePath, Loader loader) {

        ByteBuffer fileBuffer = Utils.ioResourceToByteBuffer(resourcePath, 8 * 1024);

        AIScene scene = aiImportFileFromMemory(fileBuffer,
                aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FlipUVs,
                "obj");

        if (scene == null || scene.mNumMeshes() == 0) {
            throw new RuntimeException("Could not load model: " + aiGetErrorString());
        }

        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));

        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < mesh.mNumVertices(); i++) {
            AIVector3D vec = mesh.mVertices().get(i);
            vertices.add(vec.x());
            vertices.add(vec.y());
            vertices.add(vec.z());

            if (mesh.mTextureCoords(0) != null) {
                AIVector3D tex = mesh.mTextureCoords(0).get(i);
                textures.add(tex.x());
                textures.add(tex.y());
            } else {
                textures.add(0.0f);
                textures.add(0.0f);
            }

            AIVector3D normal = mesh.mNormals().get(i);
            normals.add(normal.x());
            normals.add(normal.y());
            normals.add(normal.z());
        }

        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            IntBuffer buffer = face.mIndices();
            while (buffer.hasRemaining()) {
                indices.add(buffer.get());
            }
        }

        float[] verticesArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            verticesArray[i] = vertices.get(i);
        }

        float[] texturesArray = new float[textures.size()];
        for (int i = 0; i < textures.size(); i++) {
            texturesArray[i] = textures.get(i);
        }

        float[] normalsArray = new float[normals.size()];
        for (int i = 0; i < normals.size(); i++) {
            normalsArray[i] = normals.get(i);
        }

        int[] indicesArray = indices.stream().mapToInt(i -> i).toArray();

        return loader.loadToVao(verticesArray, texturesArray, normalsArray, indicesArray);
    }

}
