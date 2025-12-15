package nucleus.core.lighting;

import org.joml.Vector3f;

public class PointLight {

    private Vector3f colour,position;
    private float intensity,constant,linear,exponent;

    public PointLight(Vector3f colour, Vector3f position, float intensity, float constant, float linear, float exponent) {
        this.colour = colour;
        this.position = position;
        this.intensity = intensity;
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;
    }

    public PointLight(Vector3f colour, Vector3f position, float intensity) {
        this(colour,position,intensity,1,0,0);
    }

    public Vector3f getColour() {
        return colour;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getIntensity() {
        return intensity;
    }

    public float getConstant() {
        return constant;
    }

    public float getLinear() {
        return linear;
    }

    public float getExponent() {
        return exponent;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }
}
