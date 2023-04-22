package core.rendering;

import core.Camera;
import core.ShaderManager;
import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.lighting.SpotLight;
import core.models.Model;
import core.models.terrain.Terrain;
import game.Launcher;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import static utils.Consts.*;
import utils.Transformation;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class TerrainRenderer implements IRenderer{

    private ShaderManager shaderManager;
    private List<Terrain> terrains;

    public TerrainRenderer() throws Exception{
        terrains=new ArrayList<>();
        this.shaderManager=new ShaderManager();
    }

    @Override
    public void init() throws Exception {

        shaderManager.createVertexShader(Utils.loadResource("/shaders/terrain_vertex.vs.glsl"));
        shaderManager.createFragmentShader(Utils.loadResource("/shaders/terrain_fragment.fs.glsl"));
        shaderManager.link();
        shaderManager.createUniform("backgroundTexture");
        shaderManager.createUniform("redTexture");
        shaderManager.createUniform("greenTexture");
        shaderManager.createUniform("blueTexture");
        shaderManager.createUniform("blendMap");
        shaderManager.createUniform("transformationMatrix");
        shaderManager.createUniform("projectionMatrix");
        shaderManager.createUniform("viewMatrix");
        shaderManager.createUniform("ambientLight");
        shaderManager.createMaterialUniform("material");
        shaderManager.createUniform("specularPower");
        shaderManager.createDirectionalLightUniform("directionalLight");
        shaderManager.createPointLightArrayUniform("pointLights", MAX_POINT_LIGHTS);
        shaderManager.createSpotLightArrayUniform("spotLights",MAX_SPOT_LIGHTS);
    }

    @Override
    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        RenderManager.enableCulling();

        shaderManager.setUniform("backgroundTexture",0);
        shaderManager.setUniform("redTexture",1);
        shaderManager.setUniform("greenTexture",2);
        shaderManager.setUniform("blueTexture",3);
        shaderManager.setUniform("blendMap",4);

        shaderManager.setUniform("material",model.getMaterial());
    }

    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Object terrain, Camera camera) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,((Terrain)terrain).getBlendMapTerrain().getBackdround().getId());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,((Terrain)terrain).getBlendMapTerrain().getRedTexture().getId());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,((Terrain)terrain).getBlendMapTerrain().getGreenTexture().getId());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,((Terrain)terrain).getBlendMapTerrain().getBlueTexture().getId());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,((Terrain)terrain).getBlendMap().getId());

        shaderManager.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Terrain)terrain));
        shaderManager.setUniform("viewMatrix",Transformation.getViewMatrix(camera));
    }

    @Override
    public void render(Camera camera, DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights) {
        shaderManager.bind();
        shaderManager.setUniform("projectionMatrix", Launcher.getWindowManager().updateProductionMatrix());
        RenderManager.renderLights(camera,pointLights,spotLights,directionalLight,shaderManager);

        for(Terrain takeTerrain:terrains){
            bind(takeTerrain.getModel());
            prepare(takeTerrain,camera);
            GL11.glDrawElements(GL11.GL_TRIANGLES, takeTerrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbind();
        }

        terrains.clear();
        shaderManager.unbind();
    }

    @Override
    public void cleanup() {
        shaderManager.cleanup();
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }
}
