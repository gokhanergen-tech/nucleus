package core.models;

import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.lighting.SpotLight;
import core.models.terrain.Terrain;
import org.joml.Vector3f;
import utils.Consts;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {

    private List<Entity> entitiesList;
    private List<Terrain> terrainList;

    private Vector3f ambientLight;
    private SpotLight[] spotLights;
    private PointLight[] pointLights;
    private DirectionalLight directionalLight;
    private float lightAngle;
    private float spotAngle=0;
    private float spotInc=1f;

    public SceneManager(float lightAngle) {
        entitiesList=new ArrayList<>();
        terrainList=new ArrayList<>();
        ambientLight= Consts.AMBIENT_LIGHT;
        this.lightAngle = lightAngle;
    }

    public List<Entity> getEntitiesList() {
        return entitiesList;
    }

    public void setEntitiesList(List<Entity> entitiesList) {
        this.entitiesList = entitiesList;
    }

    public List<Terrain> getTerrainList() {
        return terrainList;
    }

    public void setTerrainList(List<Terrain> terrainList) {
        this.terrainList = terrainList;
    }

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public SpotLight[] getSpotLights() {
        return spotLights;
    }

    public void setSpotLights(SpotLight[] spotLights) {
        this.spotLights = spotLights;
    }

    public PointLight[] getPointLights() {
        return pointLights;
    }

    public void setPointLights(PointLight[] pointLights) {
        this.pointLights = pointLights;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public float getLightAngle() {
        return lightAngle;
    }

    public void setLightAngle(float lightAngle) {
        this.lightAngle = lightAngle;
    }

    public float getSpotAngle() {
        return spotAngle;
    }

    public void incLightAngle(float incLightAngle){
        this.lightAngle+=incLightAngle;
    }

    public void incSpotAngle(){
        this.spotAngle+=spotInc;
    }

    public void setSpotAngle(float spotAngle) {
        this.spotAngle = spotAngle;
    }

    public float getSpotInc() {
        return spotInc;
    }

    public void setSpotInc(float spotInc) {
        this.spotInc = spotInc;
    }
}
