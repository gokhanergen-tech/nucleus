package core.models;

import org.joml.Vector3f;

public class Entity {
    private Model model;
    private Vector3f rotation,position;
    private float scale;

    public Entity(Model model, Vector3f rotation, Vector3f position, Float scale){
        this.model=model;
        this.position=position;
        this.scale=scale;
        this.rotation=rotation;
    }

    public void incPos(float x,float y,float z){
        this.position.x=x;
        this.position.y=y;
        this.position.z=z;
    }

    public void incRotation(float x,float y,float z){
        this.rotation.x+=x;
        this.rotation.y+=y;
        this.rotation.z+=z;
    }
    public void setRotation(float x,float y,float z){
        this.rotation.x=x;
        this.rotation.y=y;
        this.rotation.z=z;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Model getModel() {
        return model;
    }
}
