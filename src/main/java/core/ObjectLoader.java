package core;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import core.models.Model;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import utils.Utils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class ObjectLoader {
    private List<Integer> vaos=new ArrayList<>();
    private List<Integer> vbos=new ArrayList<>();
    private List<Integer> textures=new ArrayList<>();

    public Model loadOBJModel(String filename){
        List<String> list=Utils.readAllLines(filename);

        List<Vector3f> vertices=new ArrayList<>();
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
                    processFace(tokens[1],faces);
                    processFace(tokens[2],faces);
                    processFace(tokens[3],faces);
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

        for(Vector3i face:faces){
                processVertex(face.x,face.y,face.z,textures,normals,indices,texCoordArray,normalArray);
        }

        int[] indicesArr=indices.stream().mapToInt((Integer v)->v).toArray();

        return loadModel(verticesArray,texCoordArray,normalArray,indicesArr);
    }

    private static void processVertex(int pos,int texCoord,int normal,List<Vector2f> texCoordList,
                                      List<Vector3f> normalList,List<Integer> indicesList,float[] texCoordArray,
                                      float[] normalArray){
        indicesList.add(pos);

        if(texCoord>=0){
            Vector2f texCoordVector=texCoordList.get(texCoord);
            texCoordArray[pos*2]=texCoordVector.x;
            texCoordArray[pos*2+1]=1-texCoordVector.y;
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
         int length=lineToken.length;
         int position=-1,coords=-1,normal=-1;
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
