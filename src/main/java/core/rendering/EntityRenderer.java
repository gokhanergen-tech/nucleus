package core.rendering;

import core.Camera;
import core.ShaderManager;
import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.lighting.SpotLight;
import core.models.Entity;
import core.models.Model;
import game.Launcher;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import utils.Transformation;
import utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRenderer implements IRenderer{

    private ShaderManager shaderManager;
    private Map<Model, List<Entity>> modelListMap;

    public EntityRenderer() throws Exception{
        modelListMap=new HashMap<>();
        this.shaderManager=new ShaderManager();
    }

    @Override
    public void init() throws Exception {
        shaderManager.createVertexShader(Utils.loadResource("/shaders/entity_vertex.vs.glsl"));
        shaderManager.createFragmentShader(Utils.loadResource("/shaders/entity_fragment.fs.glsl"));
        shaderManager.link();
        shaderManager.createUniform("textureSampler");
        shaderManager.createUniform("transformationMatrix");
        shaderManager.createUniform("projectionMatrix");
        shaderManager.createUniform("viewMatrix");
        shaderManager.createUniform("ambientLight");
        shaderManager.createMaterialUniform("material");
        shaderManager.createUniform("specularPower");
        shaderManager.createDirectionalLightUniform("directionalLight");
        shaderManager.createPointLightArrayUniform("pointLights",5);
        shaderManager.createSpotLightArrayUniform("spotLights",5);
    }

    @Override
    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if(model.getMaterial().isDisableCulling())
            RenderManager.enableCulling();
        else
            RenderManager.disableCulling();
        shaderManager.setUniform("material",model.getMaterial());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,model.getTexture().getId());
    }

    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Object entity, Camera camera) {
        shaderManager.setUniform("textureSampler",0);
        shaderManager.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Entity) entity));
        shaderManager.setUniform("viewMatrix",Transformation.getViewMatrix(camera));
    }

    @Override
    public void render(Camera camera, DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights) {
        shaderManager.bind();
        shaderManager.setUniform("projectionMatrix", Launcher.getWindowManager().updateProductionMatrix());
        RenderManager.renderLights(camera,pointLights,spotLights,directionalLight,shaderManager);
        for(Model model:modelListMap.keySet()){
            bind(model);
            List<Entity> entities=modelListMap.get(model);
            for(Entity takeEntity:entities){
                prepare(takeEntity,camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbind();
        }

        modelListMap.clear();
        shaderManager.unbind();
    }

    @Override
    public void cleanup() {
        shaderManager.cleanup();
    }

    public Map<Model, List<Entity>> getModelListMap() {
        return modelListMap;
    }
}
