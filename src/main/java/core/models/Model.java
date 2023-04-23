package core.models;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private int id;

    private String modelId;
    private int vertexCount;
    private Material material;

    private List<Material> materialList;
    private List<Entity> entitiesList;

    public Model(int id, int vertexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material();
    }

    public Model(int id, int vertexCount, Texture texture) {
        this.vertexCount = vertexCount;
        this.id = id;
        this.material = new Material(texture);
    }

    public Model(Model model, Texture texture) {
        this.vertexCount = model.getVertexCount();
        this.id = model.getId();
        this.material = model.getMaterial();
        this.material.setTexture(texture);

    }

    public Model(String modelId, List<Material> materialList) {
        this.modelId = modelId;
        this.materialList = materialList;
        this.entitiesList = new ArrayList<>();
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<Material> materialList) {
        this.materialList = materialList;
    }

    public List<Entity> getEntitiesList() {
        return entitiesList;
    }

    public void setEntitiesList(List<Entity> entitiesList) {
        this.entitiesList = entitiesList;
    }

    public int getId() {
        return id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Texture getTexture() {
        return material.getTexture();
    }

    public void setTexture(Texture texture) {
        material.setTexture(texture);
    }

    public void setTexture(Texture texture,float reluctance){
        material.setTexture(texture);
        material.setReflectance(reluctance);
    }
}
