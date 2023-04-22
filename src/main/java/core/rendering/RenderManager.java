package core.rendering;

import core.Camera;
import core.ShaderManager;
import core.WindowManager;
import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.lighting.SpotLight;
import core.models.Entity;
import core.models.Model;
import core.models.SceneManager;
import core.models.terrain.Terrain;
import utils.Consts;
import org.lwjgl.opengl.GL11;
import game.Launcher;

import java.util.*;


public class RenderManager {

    private final WindowManager window;
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private static boolean isCulling=false;

    public RenderManager(){
        window= Launcher.getWindowManager();
    }
    public void init() throws Exception{
        entityRenderer=new EntityRenderer();
        terrainRenderer=new TerrainRenderer();
        entityRenderer.init();
        terrainRenderer.init();

    }

    public static void enableCulling(){
        if(!isCulling){
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);
            isCulling=true;
        }

    }

    public static void disableCulling(){
        if(isCulling){
            GL11.glDisable(GL11.GL_CULL_FACE);
            isCulling=false;
        }

    }


    public static void renderLights(Camera camera,
                             PointLight[] pointLights,
                             SpotLight[] spotLights,
                             DirectionalLight directionalLight,ShaderManager shaderManager){
        shaderManager.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
        shaderManager.setUniform("specularPower",Consts.SPECULAR_POWER);
        int numLights=spotLights!=null?spotLights.length:0;
        for (int i = 0; i <numLights ; i++) {
            shaderManager.setUniform("spotLights",spotLights[i],i);
        }

        numLights=pointLights!=null?pointLights.length:0;
        for (int i = 0; i <numLights ; i++) {
            shaderManager.setUniform("pointLights",pointLights[i],i);
        }
        shaderManager.setUniform("directionalLight",directionalLight);

    }

    public void render(Camera camera, SceneManager sceneManager){
       clear();
       entityRenderer.render(camera, sceneManager.getDirectionalLight(), sceneManager.getPointLights(), sceneManager.getSpotLights());
       terrainRenderer.render(camera, sceneManager.getDirectionalLight(), sceneManager.getPointLights(), sceneManager.getSpotLights());
    }

    public void processEntity(Entity entity){
        Optional<List<Entity>> optionalEntities=Optional.ofNullable(entityRenderer.getModelListMap().get(entity.getModel()));
        List<Entity> entities=optionalEntities.orElse(null);
        if(entities!=null){
            entities.add(entity);
        }else{
            entities=new ArrayList<>(){{add(entity);}};
            entityRenderer.getModelListMap().put(entity.getModel(),entities);
        }
    }

    public void processTerrain(Terrain terrain){
        terrainRenderer.getTerrains().add(terrain);
    }

    public void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
    }
    public void cleanup(){
        entityRenderer.cleanup();
        terrainRenderer.cleanup();
    }
}
