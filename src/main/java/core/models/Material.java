package core.models;

import org.joml.Vector4f;
import utils.Consts;

public class Material {
    private Vector4f ambientColor,diffuseColor,specularColor;
    private float reflectance;
    private Texture texture;
    private boolean disableCulling;

    public Material() {
        this.ambientColor = Consts.DEFAULT_COLOR;
        this.diffuseColor = Consts.DEFAULT_COLOR;
        this.specularColor = Consts.DEFAULT_COLOR;
        this.reflectance = 0;
        this.disableCulling=false;
        this.texture = null;
    }

    public Material(Texture texture){
        this(Consts.DEFAULT_COLOR,Consts.DEFAULT_COLOR,Consts.DEFAULT_COLOR,0,texture);
    }

    public Material(Texture texture,float reflectance){
        this(Consts.DEFAULT_COLOR,Consts.DEFAULT_COLOR,Consts.DEFAULT_COLOR,reflectance,texture);
    }

    public Material(Vector4f colour,float reflectance) {
        this(colour,colour,colour,reflectance,null);

    }

    public Material(Vector4f colour,float reflectance,Texture texture) {
        this(colour,colour,colour,reflectance,texture);

    }

    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance, Texture texture) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.reflectance = reflectance;
        this.texture = texture;
    }

    public Vector4f getAmbientColor() {
        return ambientColor;
    }

    public void setAmbientColor(Vector4f ambientColor) {
        this.ambientColor = ambientColor;
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }

    public Vector4f getSpecularColor() {
        return specularColor;
    }

    public void setSpecularColor(Vector4f specularColor) {
        this.specularColor = specularColor;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean isDisableCulling() {
        return disableCulling;
    }

    public void setDisableCulling(boolean disableCulling) {
        this.disableCulling = disableCulling;
    }

    public boolean hasTexture(){
        return texture!=null;
    }
}
