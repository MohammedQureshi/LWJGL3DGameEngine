package org.mqureshi.engine;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import org.mqureshi.entities.Material;
import org.mqureshi.entities.Mesh;
import org.mqureshi.entities.Model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ModelLoader {

    private ModelLoader() {

    }

    public static Model loadModel(String modelId, String modelPath, TextureCache textureCache) {
        return loadModel(modelId, modelPath, textureCache, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                aiProcess_PreTransformVertices);
    }

    private static ByteBuffer convertModelIntoBytes(String path) {
        try (InputStream is = ModelLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) return null;

            byte[] bytes = inputStreamToByteArray(is);
            ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            return buffer;
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + path, e);
        }
    }

    public static Model loadModel(String modelId, String modelPath, TextureCache textureCache, int flags) {
        AIFileIO fileIo = AIFileIO.create()
                .OpenProc((pFileIO, fileName, openMode) -> {
                    ByteBuffer data = convertModelIntoBytes(memUTF8(fileName));
                    if (data == null) {
                        throw new RuntimeException("Could not find file: " + memUTF8(fileName));
                    }

                    return AIFile.create()
                            .ReadProc((pFile, pBuffer, size, count) -> {
                                long max = Math.min(data.remaining() / size, count);
                                memCopy(memAddress(data), pBuffer, max * size);
                                data.position(data.position() + (int) (max * size));
                                return max;
                            })
                            .SeekProc((pFile, offset, origin) -> {
                                if (origin == Assimp.aiOrigin_CUR) {
                                    data.position(data.position() + (int) offset);
                                } else if (origin == Assimp.aiOrigin_SET) {
                                    data.position((int) offset);
                                } else if (origin == Assimp.aiOrigin_END) {
                                    data.position(data.limit() + (int) offset);
                                }
                                return 0;
                            })
                            .FileSizeProc(pFile -> data.limit())
                            .address();
                })
                .CloseProc((pFileIO, pFile) -> {
                    AIFile aiFile = AIFile.create(pFile);
                    aiFile.ReadProc().free();
                    aiFile.SeekProc().free();
                    aiFile.FileSizeProc().free();
                });

        AIScene scene = aiImportFileEx(modelPath, flags, fileIo);

        fileIo.OpenProc().free();
        fileIo.CloseProc().free();

        if (scene == null) {
            throw new RuntimeException("Error loading model: " + aiGetErrorString());
        }

        return processScene(scene, modelId, modelPath, textureCache);
    }

    private static Model processScene(AIScene scene, String modelId, String modelPath, TextureCache textureCache) {
        int numMaterials = scene.mNumMaterials();
        List<Material> materialList = new ArrayList<>();

        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(scene.mMaterials().get(i));
            Material material = processMaterial(aiMaterial, modelPath, textureCache);
            materialList.add(material);
        }

        int numMeshes = scene.mNumMeshes();
        PointerBuffer aiMeshes = scene.mMeshes();
        Material defaultMaterial = new Material();

        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh);

            int materialIdx = aiMesh.mMaterialIndex();
            Material material = materialIdx >= 0 && materialIdx < materialList.size() ? materialList.get(materialIdx) : defaultMaterial;
            material.getMeshList().add(mesh);
        }

        if (!defaultMaterial.getMeshList().isEmpty()) {
            materialList.add(defaultMaterial);
        }

        return new Model(modelId, materialList);
    }

    private static Material processMaterial(AIMaterial aiMaterial, String modelDir, TextureCache textureCache) {
        Material material = new Material();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color = AIColor4D.create();

            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0,
                    color);
            if (result == aiReturn_SUCCESS) {
                material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            }

            AIString aiTexturePath = AIString.calloc(stack);
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null,
                    null, null, null, null, null);
            String texturePath = aiTexturePath.dataString();

            if (texturePath != null && texturePath.length() > 0) {
                material.setTexturePath(File.separator + new File(modelDir).getParent() + File.separator + texturePath);
                textureCache.createTexture(material.getTexturePath());
                material.setDiffuseColor(Material.DEFAULT_COLOR);
            }

            return material;
        }
    }

    private static Mesh processMesh(AIMesh aiMesh) {
        float[] vertices = processVertices(aiMesh);
        float[] textCoords = processTextCoords(aiMesh);
        int[] indices = processIndices(aiMesh);

        // Texture coordinates may not have been populated. We need at least the empty slots
        if (textCoords.length == 0) {
            int numElements = (vertices.length / 3) * 2;
            textCoords = new float[numElements];
        }

        return new Mesh(vertices, textCoords, indices);
    }

    private static int[] processIndices(AIMesh aiMesh) {
        List<Integer> indices = new ArrayList<>();
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    private static float[] processTextCoords(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        if (buffer == null) {
            return new float[]{};
        }
        float[] data = new float[buffer.remaining() * 2];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = 1 - textCoord.y();
        }
        return data;
    }

    private static byte[] inputStreamToByteArray(InputStream inputStream) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error reading model file into byte array", e);
        }
    }

    private static float[] processVertices(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = textCoord.y();
            data[pos++] = textCoord.z();
        }
        return data;
    }

    private static String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filePath.length() - 1) {
            return null;
        }
        return filePath.substring(dotIndex + 1).toLowerCase(); // Return extension in lowercase
    }

}
