package nucleus.core.rendering;

import nucleus.core.Camera;
import nucleus.core.ShaderManager;
import nucleus.core.WindowManager;
import nucleus.core.lighting.DirectionalLight;
import nucleus.core.lighting.PointLight;
import nucleus.core.lighting.SpotLight;
import nucleus.core.models.Entity;
import nucleus.core.models.SceneManager;
import nucleus.core.models.terrain.Terrain;
import game.Launcher;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import nucleus.utils.Consts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class RenderManager {
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private static boolean isCulling=false;

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

    public static void disableCulling() {
        if (isCulling) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            isCulling = false;
        }

    }

    /**
     * @param position
     * @return boolean whether the object is close or not
     */
    public static boolean isCloseToZFar(Vector3f position, Vector3f cameraPosition,float scale) {
        int zFar =(int) (WindowManager.Z_FAR*scale);
        return Math.abs(position.x - cameraPosition.x) < zFar &&
                Math.abs(position.y - cameraPosition.y) < zFar &&
                Math.abs(position.z - cameraPosition.z) < zFar;
    }

    public static void renderLights(Camera camera,
                                    PointLight[] pointLights,
                                    SpotLight[] spotLights,
                                    DirectionalLight directionalLight, ShaderManager shaderManager) {
        shaderManager.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
        shaderManager.setUniform("specularPower", Consts.SPECULAR_POWER);
        int numLights = spotLights != null ? spotLights.length : 0;

        for (int i = 0; i < numLights; i++) {
            SpotLight spotLight = spotLights[i];
            if (spotLight != null) {
                if (isCloseToZFar(spotLight.getPointLight().getPosition(), camera.getPosition(),1))
                    shaderManager.setUniform("spotLights", spotLight, i);
            }
        }

        numLights=pointLights!=null?pointLights.length:0;
        for (int i = 0; i <numLights ; i++) {
            PointLight pointLight = pointLights[i];
            if (pointLight != null) {
                {
                    if (isCloseToZFar(pointLight.getPosition(), camera.getPosition(),1))
                        shaderManager.setUniform("pointLights", pointLights[i], i);
                }
            }
        }
        if (isCloseToZFar(directionalLight.getDirection(), camera.getPosition(),1))
         shaderManager.setUniform("directionalLight",directionalLight);

    }

    public void render(Camera camera, SceneManager sceneManager){
       clear();
       entityRenderer.render(camera, sceneManager.getDirectionalLight(), sceneManager.getPointLights(), sceneManager.getSpotLights());
       terrainRenderer.render(camera, sceneManager.getDirectionalLight(), sceneManager.getPointLights(), sceneManager.getSpotLights());
    }

    public void processEntity(Entity entity){

        Optional<List<Entity>> optionalEntities = Optional.ofNullable(entityRenderer.getModelListMap().get(entity.getModel()));
        List<Entity> entities = optionalEntities.orElse(null);
        if (entities != null) {
            entities.add(entity);
        } else {
            entities = new ArrayList<>() {{
                add(entity);
            }};
        }
        entityRenderer.getModelListMap().put(entity.getModel(), entities);
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
