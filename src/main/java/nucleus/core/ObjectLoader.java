package nucleus.core;

import nucleus.core.models.Material;
import nucleus.core.models.Model;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import nucleus.utils.Utils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class ObjectLoader {
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    private static Material processMaterial(AIMaterial aiMaterial, String modelDir) {
        Material material = new Material();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color = AIColor4D.create();

            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0,
                    color);
            if (result == aiReturn_SUCCESS) {
                material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            }

            AIString aiTexturePath = AIString.callocStack(stack);
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null,
                    null, null, null, null, null);
            String texturePath = aiTexturePath.dataString();
            if (texturePath != null && texturePath.length() > 0) {
                material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
                material.setDiffuseColor(Material.DEFAULT_COLOR);
            }

            return material;
        }
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

    private static void processVertex(int pos, int texCoord, int normal, List<Vector2f> texCoordList,
                                      List<Vector3f> normalList, List<Integer> indicesList, float[] texCoordArray,
                                      float[] normalArray) {
        indicesList.add(pos);

        if (texCoord >= 0) {
            Vector2f texCoordVector = texCoordList.get(texCoord);
            texCoordArray[pos * 2] = texCoordVector.x;
            texCoordArray[pos * 2 + 1] = 1 - texCoordVector.y;
        }

        if(normal>=0){
            Vector3f normalVec=normalList.get(normal);
            normalArray[pos*3]=normalVec.x;
            normalArray[pos*3+1]=normalVec.y;
            normalArray[pos*3+2]=normalVec.z;
        }
    }

    private static void processFace(String token, List<Vector3i> faces){
         String[] lineToken=token.split("/");
        int length = lineToken.length;
        int position, coords = -1, normal = -1;
         position=Integer.parseInt(lineToken[0])-1;
         if(length>1){
             String textCoord=lineToken[1];
             coords=textCoord.length()>0?Integer.parseInt(textCoord)-1 :-1;
             if(length>2)
                 normal=Integer.parseInt(lineToken[2])-1;
         }
         Vector3i facesVec=new Vector3i(position,coords,normal);
         faces.add(facesVec);
    }

    public Model loadOBJModel(String filename) {
        List<String> list = Utils.readAllLines(filename);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals=new ArrayList<>();
        List<Vector2f> textures=new ArrayList<>();
        List<Vector3i> faces=new ArrayList<>();

        for(String line:list){
            String[] tokens=line.split("\\s");
            switch (tokens[0]){
                case "v":
                    //vertices
                    Vector3f vector3f=new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );

                    vertices.add(vector3f);
                    break;
                case "vt":
                    //vertex textures
                    Vector2f vts=new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );

                    textures.add(vts);
                    break;
                case "vn":
                    Vector3f normalsVec=new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );

                    normals.add(normalsVec);
                    //vertex normals
                    break;
                case "f":
                    //faces
                    for (int i = 1; i < tokens.length; i++) {
                        processFace(tokens[i], faces);
                    }
                    break;
                default:
                    break;
            }
        }

        List<Integer> indices=new ArrayList<>();
        float[] verticesArray=new float[vertices.size()*3];
        int i=0;

        for(Vector3f pos:vertices){
            verticesArray[i*3]=pos.x;
            verticesArray[i*3+1]=pos.y;
            verticesArray[i*3+2]=pos.z;
            i+=1;
        }

        float[] texCoordArray=new float[vertices.size()*2];
        float[] normalArray=new float[vertices.size()*3];

        for (Vector3i face : faces) {
            processVertex(face.x, face.y, face.z, textures, normals, indices, texCoordArray, normalArray);
        }

        int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesArray, texCoordArray, normalArray, indicesArr);
    }

    public Model loadOBJModel(String modelId, String modelPath) {
        int flags = aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                aiProcess_PreTransformVertices;
        File file = new File(modelPath);
        if (!file.exists()) {
            throw new RuntimeException("Model path does not exist [" + modelPath + "]");
        }
        String modelDir = file.getParent();

        AIScene aiScene = aiImportFile(modelPath, flags);

        if (aiScene == null) {
            throw new RuntimeException("Error loading model [modelPath: " + modelPath + "]");
        }
        int numMaterials = aiScene.mNumMaterials();
        List<Material> materialList = new ArrayList<>();

        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            materialList.add(processMaterial(aiMaterial, modelDir));
        }

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Material defaultMaterial = new Material();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Model mesh = processMesh(aiMesh);
            int materialIdx = aiMesh.mMaterialIndex();
            Material material;
            if (materialIdx >= 0 && materialIdx < materialList.size()) {
                material = materialList.get(materialIdx);
            } else {
                material = defaultMaterial;
            }
            material.getMeshList().add(mesh);
        }

        if (!defaultMaterial.getMeshList().isEmpty()) {
            materialList.add(defaultMaterial);
        }

        return new Model(modelId, materialList);
    }

    private Model processMesh(AIMesh aiMesh) {
        float[] vertices = processVertices(aiMesh);
        float[] textCoords = processTextCoords(aiMesh);
        int[] indices = processIndices(aiMesh);

        // Texture coordinates may not have been populated. We need at least the empty slots
        if (textCoords.length == 0) {
            int numElements = (vertices.length / 3) * 2;
            textCoords = new float[numElements];
        }

        return loadModel(vertices, textCoords, new float[0], indices);
    }

    public Model loadModel(float[] vertices,float[] textureCoords,float[] normals,int[] indices){
      int id=createVAO();
      storeIndicesBuffer(indices);
      storeDataInAttribList(0,3,vertices);
      storeDataInAttribList(1,2,textureCoords);
      if(normals!=null)
       storeDataInAttribList(2,3,normals);
      unbind();

      return new Model(id,indices.length*3);
    }

    public int loadTexture(String texturePath) throws Exception{
        int width,height;
        ByteBuffer bufferByte;
        try(MemoryStack memoryStack=MemoryStack.stackPush()){
             IntBuffer w=memoryStack.mallocInt(1);
             IntBuffer h=memoryStack.mallocInt(1);
             IntBuffer c=memoryStack.mallocInt(1);

             bufferByte= STBImage.stbi_load(texturePath,w,h,c,4);
             if(bufferByte==null)
                 throw new Exception(texturePath+" dosyası yükelenemedi. Hata : "+STBImage.stbi_failure_reason());

             width=w.get();
             height=h.get();
        }
        int id=GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,id);

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT,1);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D,0,GL11.GL_RGBA,width,height,0,GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE,bufferByte);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);


        STBImage.stbi_image_free(bufferByte);

        return id;

    }

    private void storeIndicesBuffer(int[] indices){
        int vbo= GL15.glGenBuffers();
        vbos.add(vbo);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER,vbo);
        IntBuffer buffer= Utils.storeDataInIntBuffer(indices);

        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER,buffer,GL15.GL_STATIC_DRAW);

    }
    private int createVAO(){
        int id=GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    private void storeDataInAttribList(int attribNo,int vertexCount,float[] data){
       int vbo= GL15.glGenBuffers();
       vbos.add(vbo);

       GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,vbo);
       FloatBuffer buffer= Utils.storeDataInFloatBuffer(data);

       GL15.glBufferData(GL15.GL_ARRAY_BUFFER,buffer,GL15.GL_STATIC_DRAW);
       GL20.glVertexAttribPointer(attribNo,vertexCount, GL11.GL_FLOAT,false,0,0);
       GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);


    }

    private void unbind(){
        GL30.glBindVertexArray(0);
    }

    public void cleanup(){
        for(int vao:vaos)
            GL30.glDeleteVertexArrays(vao);
        for(int vbo:vaos)
            GL30.glDeleteBuffers(vbo);
        for(int texture:textures)
            GL11.glDeleteTextures(texture);
    }

}
