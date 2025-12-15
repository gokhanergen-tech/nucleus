package nucleus.core;

import nucleus.core.lighting.DirectionalLight;
import nucleus.core.lighting.PointLight;
import nucleus.core.lighting.SpotLight;
import nucleus.core.models.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private final int programID;
    private int vertextShaderID,fragmentShaderID;


    private final Map<String,Integer> uniforms;
    public ShaderManager() throws Exception{
        programID= GL20.glCreateProgram();
        if(programID==0){
            throw new Exception("Shader oluşturulamıyor");
        }
        uniforms=new HashMap<>();
    }
    public void createUniform(String uniformName) throws Exception{
        int uniformLocation=GL20.glGetUniformLocation(programID,uniformName);
        if(uniformLocation<0)
            throw new Exception("Uniform ismi bulunamadı!");
        uniforms.put(uniformName,uniformLocation);
    }

    public void createMaterialUniform(String uniformName) throws Exception{
        createUniform(uniformName+".ambient");
        createUniform(uniformName+".diffuse");
        createUniform(uniformName+".specular");
        createUniform(uniformName+".hasTexture");
        createUniform(uniformName+".reflectance");

    }

    public void createDirectionalLightUniform(String uniformName) throws Exception{

        createUniform(uniformName+".colour");
        createUniform(uniformName+".intensity");
        createUniform(uniformName+".direction");

    }

    public void createPointLightUniform(String uniformName) throws Exception{
        createUniform(uniformName+".colour");
        createUniform(uniformName+".position");
        createUniform(uniformName+".intensity");
        createUniform(uniformName+".constant");
        createUniform(uniformName+".linear");
        createUniform(uniformName+".exponent");

    }

    public void createSpotLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".cutoff");
        createUniform(uniformName + ".coneDirection");
        createPointLightUniform(uniformName+".pointLight");
    }

    public void createPointLightArrayUniform(String uniformName,int size) throws Exception{
        for (int i=0;i<size;i++)
            createPointLightUniform(uniformName+"["+i+"]");
    }

    public void createSpotLightArrayUniform(String uniformName,int size) throws Exception{
        for (int i=0;i<size;i++)
            createSpotLightUniform(uniformName+"["+i+"]");
    }

    public void setUniform(String name, DirectionalLight directionalLight){
        setUniform(name+".intensity",directionalLight.getIntensity());
        setUniform(name+".colour",directionalLight.getColor());
        setUniform(name+".direction",directionalLight.getDirection());
    }

    public void setUniform(String uniformName, Matrix4f matrix4f){
        try(MemoryStack memoryStack=MemoryStack.stackPush()){
            GL20.glUniformMatrix4fv(uniforms.get(uniformName),false
                    ,matrix4f.get(matrix4f.get(memoryStack.mallocFloat(16))));
        }
    }

    public void setUniform(String uniformName, Material material){
        setUniform(uniformName+".ambient",material.getAmbientColor());
        setUniform(uniformName+".diffuse",material.getDiffuseColor());
        setUniform(uniformName+".specular",material.getSpecularColor());
        setUniform(uniformName+".hasTexture",material.hasTexture());
        setUniform(uniformName+".reflectance",material.getReflectance());
    }

    public void setUniform(String uniformName, Vector3f vector3f){
        GL20.glUniform3f(uniforms.get(uniformName),vector3f.x,vector3f.y,vector3f.z);
    }

    public void setUniform(String uniformName, Vector4f vector4f){
        GL20.glUniform4f(uniforms.get(uniformName),vector4f.x,vector4f.y,vector4f.z,vector4f.w);
    }

    public void setUniform(String uniformName, boolean value){
        float res=0;
        if(value)
            res=1;
        GL20.glUniform1f(uniforms.get(uniformName),res);
    }

    public void setUniform(String uniformName, float value){
        GL20.glUniform1f(uniforms.get(uniformName),value);
    }

    public void setUniform(String uniformName, int value){
       GL20.glUniform1i(uniforms.get(uniformName),value);

    }

    public void setUniform(String uniformName, PointLight pointLight){
        setUniform(uniformName+".colour",pointLight.getColour());
        setUniform(uniformName+".position",pointLight.getPosition());
        setUniform(uniformName+".linear",pointLight.getLinear());
        setUniform(uniformName+".intensity",pointLight.getIntensity());
        setUniform(uniformName+".constant",pointLight.getConstant());
        setUniform(uniformName+".exponent",pointLight.getExponent());


    }

    public void setUniform(String uniformName, SpotLight spotLight){
          setUniform(uniformName+".pointLight",spotLight.getPointLight());
          setUniform(uniformName+".cutoff",spotLight.getCutOff());
          setUniform(uniformName+".coneDirection",spotLight.getConeDirection());
    }

    public void setUniform(String uniformName,PointLight[] pointLights){
        int numLights=pointLights!=null?pointLights.length:0;
        for(int i=0;i<numLights;i++){
            setUniform(uniformName,pointLights[i],i);
        }
    }

    public void setUniform(String uniformName,SpotLight[] spotLights){
        int numLights=spotLights!=null?spotLights.length:0;
        for(int i=0;i<numLights;i++){
            setUniform(uniformName,spotLights[i],i);
        }
    }

      public void setUniform(String uniformName,PointLight pointLight,int indice){
        setUniform(uniformName+"["+indice+"]",pointLight);
    }

    public void setUniform(String uniformName,SpotLight spotLight,int indice){
        setUniform(uniformName+"["+indice+"]",spotLight);
    }

    public void createVertexShader(String shaderCode) throws Exception{
         vertextShaderID=createShader(shaderCode,GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception{
         fragmentShaderID=createShader(shaderCode,GL20.GL_FRAGMENT_SHADER);

    }

    public int createShader(String shaderCode,int shaderType) throws Exception{
       int shaderID=GL20.glCreateShader(shaderType);
       if(shaderID==0)
           throw new Exception("Shader oluşturulamadı! Type : "+shaderType);
       GL20.glShaderSource(shaderID,shaderCode);
       GL20.glCompileShader(shaderID);
       if(GL20.glGetShaderi(shaderID,GL20.GL_COMPILE_STATUS)==0)
           throw new Exception("Shader derleme hatası oluştu! Type : "+shaderType+ " \nInfo : "+GL20.glGetShaderInfoLog(shaderID,1024));
       GL20.glAttachShader(programID,shaderID);
       return shaderID;

    }

    public void link() throws Exception{
        GL20.glLinkProgram(programID);
        if(GL20.glGetProgrami(programID,GL20.GL_LINK_STATUS)==0)
            throw new Exception("Shader linkleme hatası oluştu!\nInfo : "+GL20.glGetProgramInfoLog(programID,1024));
        if(vertextShaderID!=0)
            GL20.glDetachShader(programID,vertextShaderID);
        if(fragmentShaderID!=0)
            GL20.glDetachShader(programID,fragmentShaderID);
        GL20.glValidateProgram(programID);
        if(GL20.glGetProgrami(programID,GL20.GL_VALIDATE_STATUS)==0){
            throw new Exception("Program doğrulama hatası oluştu!\nBİLGİ : "+GL20.glGetProgramInfoLog(programID,1024));

        }
    }

   public void bind(){
        GL20.glUseProgram(programID);
   }

   public void unbind(){
        GL20.glUseProgram(0);
   }

   public void cleanup(){
        unbind();
        if(programID!=0)
            GL20.glDeleteProgram(programID);
   }
}
