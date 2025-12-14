package game;

import core.Camera;
import core.MouseInput;
import core.ObjectLoader;
import core.WindowManager;
import core.icore.ILogic;
import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.lighting.SpotLight;
import core.models.*;
import core.models.terrain.BlendMapTerrain;
import core.models.terrain.Terrain;
import core.models.terrain.TerrainTexture;
import core.rendering.RenderManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import utils.Consts;

import java.util.ArrayList;
import java.util.List;


public class TextGame implements ILogic {
    private final RenderManager renderManager;
    private final ObjectLoader objectLoader;
    private final WindowManager windowManager;
    private final SceneManager sceneManager;
    private Camera camera;
    private Vector3f cameraInc;


    public TextGame() {
        renderManager = new RenderManager();
        objectLoader = new ObjectLoader();
        windowManager = Launcher.getWindowManager();
        sceneManager = new SceneManager(-90);
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    public List<Entity> loadHouses() throws Exception {
        Model model = objectLoader.loadOBJModel("house", "src/main/resources/models/house/Farmhouse_OBJ.obj");
        List<Entity> entities = new ArrayList<>();
        final Texture texture = new Texture(objectLoader.loadTexture("textures/house/Farmhouse_Texture.jpg"));
        try {
            model.getMaterialList().forEach(material -> {
                material.getMeshList().forEach(model1 -> {
                    try {
                        model1.setTexture(texture, 1f);
                        entities.add(new Entity(model1, new Vector3f(0, 0, 0), new Vector3f(0, 0.5f, 0), 1f));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            model.getMaterialList().forEach(material -> {
                material.getMeshList().forEach(model1 -> {
                    try {
                        model1.setTexture(texture, 1f);
                        entities.add(new Entity(model1, new Vector3f(0, 0, 0), new Vector3f(-50, 0.5f, 0), 1f));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return entities;

    }

    @Override
    public void init() throws Exception {
        renderManager.init();
        sceneManager.getEntitiesList().addAll(loadHouses());
        Model model = objectLoader.loadOBJModel("/models/cube.obj");
        model.setTexture(new Texture(objectLoader.loadTexture("textures/grassblock.png")), 1f);

        Model sun = objectLoader.loadOBJModel("mountain", "src/main/resources/models/sphere.obj");


        TerrainTexture backgroundTexture = new TerrainTexture(objectLoader.loadTexture("textures/basketball512.png"));
        TerrainTexture blueTexture = new TerrainTexture(objectLoader.loadTexture("textures/sand.png"));
        TerrainTexture redTexture = new TerrainTexture(objectLoader.loadTexture("textures/frosted_ice_0.png"));
        TerrainTexture greenTexture = new TerrainTexture(objectLoader.loadTexture("textures/cobblestone.png"));
        TerrainTexture blendMap = new TerrainTexture(objectLoader.loadTexture("textures/blendMap.png"));

        BlendMapTerrain blendMapTerrain = new BlendMapTerrain(backgroundTexture, redTexture, greenTexture, blueTexture);
        Terrain terrain = new Terrain(new Vector3f(0, -1, -800), objectLoader,
                new Material(new Vector4f(0f, 0f, 0f, 0f), 0.1f), blendMapTerrain, blendMap);


        TerrainTexture testMap = new TerrainTexture(objectLoader.loadTexture("textures/test.png"));
        BlendMapTerrain testBlendMap = new BlendMapTerrain(backgroundTexture, redTexture, greenTexture, blueTexture);

        Terrain testTerrain = new Terrain(new Vector3f(0, 100, -800), objectLoader,
                new Material(new Vector4f(0f, 0f, 0f, 0f), 0.1f), testBlendMap, testMap);



        sceneManager.getTerrainList().addAll(List.of(terrain,testTerrain));

        for (int i = -100; i < 100; i++) {
            for (int j = -100; j < 100; j++) {
                sceneManager.getEntitiesList().add(new Entity(model, new Vector3f(0, 0, 0), new Vector3f(i, 0, j), 0.5f));
            }
        }


        sun.getMaterialList().forEach(material -> {
            material.getMeshList().forEach(model1 -> {
                try {
                    model1.setTexture(new Texture(objectLoader.loadTexture("textures/sand.png")), 1);
                    sceneManager.getEntitiesList().add(new Entity(model1, new Vector3f(0, 0, 0), new Vector3f(400, -5, -400), 50f));

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        });
        Vector3f sunlightPosition = new Vector3f(50f, 20, -100);
        Vector3f sunlightColour = new Vector3f(1, 1, 1);
        PointLight sunLight = new PointLight(sunlightColour, sunlightPosition, 100.0f, 0, 0, 0.2f);


        float lightIntensity = 100.0f;
           //point light
           Vector3f lightPosition=new Vector3f(0f,10.2f,0f);
           Vector3f lightColour=new Vector3f(1,1,1);
           PointLight pointLight=new PointLight(lightColour,lightPosition,lightIntensity,0,0,1f);

           //directional light
        lightPosition = new Vector3f(0, 100, -400);
        lightColour = new Vector3f(1, 1, 1);
           sceneManager.setDirectionalLight(new DirectionalLight(lightColour,lightPosition,lightIntensity));

           //spot light
        Vector3f coneDirection = new Vector3f(25, 0f, 25);
        float cutoff = (float) Math.cos(Math.toRadians(180));

           SpotLight spotLight=new SpotLight(new PointLight(
                   lightColour,
                   new Vector3f(25, 3f, 25),
                   lightIntensity, 0, 0, 0.2f
           ),coneDirection,cutoff);


           sceneManager.setPointLights(new PointLight[]{pointLight, sunLight});
           sceneManager.setSpotLights(new SpotLight[]{spotLight});


    }

    @Override
    public void input() {
      cameraInc.set(0,0,0);
      if(windowManager.isKeyPressed(GLFW.GLFW_KEY_W))
          cameraInc.z=-1;
      if(windowManager.isKeyPressed(GLFW.GLFW_KEY_S))
            cameraInc.z=1;
      if(windowManager.isKeyPressed(GLFW.GLFW_KEY_A))
            cameraInc.x=-1;
      if(windowManager.isKeyPressed(GLFW.GLFW_KEY_D))
            cameraInc.x=1;
      if(windowManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT))
            cameraInc.y=1;
      if(windowManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL))
            cameraInc.y=-1;
      //pointLight.setPosition(camera.getPosition());



    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
      camera.movePosition(cameraInc.x*Consts.CAMERA_MOVE_SPEED,
              cameraInc.y*Consts.CAMERA_MOVE_SPEED,
              cameraInc.z*Consts.CAMERA_MOVE_SPEED);
      if(mouseInput.isLeftButton()){
          Vector2f vector2f=mouseInput.getDisplVec();

          camera.moveRotation(vector2f.x* Consts.MOUSE_SENSITIVITY,vector2f.y*Consts.MOUSE_SENSITIVITY,0);
      }


      sceneManager.incSpotAngle();
      float spotAngle=sceneManager.getSpotAngle();
      if(spotAngle>90000){
          sceneManager.setSpotAngle(-1);
      }
      else if(spotAngle<=-90000){
         sceneManager.setSpotInc(1);
      }

      double spotAngleRad=Math.toRadians(spotAngle);
      Vector3f coneDir=sceneManager.getSpotLights()[0].getPointLight().getPosition();
      coneDir.x=(float)Math.sin(spotAngleRad);
      //entity.incRotation(0.0f,0.25f,0.0f);

        sceneManager.incLightAngle(0.4f);
      float lightAngle=sceneManager.getLightAngle();
      DirectionalLight directionalLight=sceneManager.getDirectionalLight();
      if(lightAngle>90){
          directionalLight.setIntensity(0);
          if(lightAngle>=360){
              lightAngle = -90;
          }
      }else if(lightAngle<=-80 || lightAngle>=80){
          float factor=1-(Math.abs(lightAngle)-80)/10.0f;
          directionalLight.setIntensity(factor);
          directionalLight.getColor().y=Math.max(factor,0.9f);
          directionalLight.getColor().z=Math.max(factor,0.5f);
      }else{
          directionalLight.setIntensity(1);
          directionalLight.getColor().x = 1;
          directionalLight.getColor().z = 1;
          directionalLight.getColor().y = 1;
      }
        double angleRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angleRad);
        directionalLight.getDirection().y = (float) Math.cos(angleRad);

        for (Terrain terrain : sceneManager.getTerrainList()) {
            if (RenderManager.isCloseToZFar(terrain.getPosition(), camera.getPosition(),1))
                renderManager.processTerrain(terrain);
        }
        for (Entity entity : sceneManager.getEntitiesList()) {
            if (RenderManager.isCloseToZFar(entity.getPosition(), camera.getPosition(),0.8f))
                renderManager.processEntity(entity);
        }


    }

    @Override
    public void render() {
        if(windowManager.isResize()){
            GL11.glViewport(0,0,windowManager.getWidth(),windowManager.getHeight());
            windowManager.setResize(true);
        }
        renderManager.render(camera,sceneManager);
    }

    @Override
    public void cleanup() {
        renderManager.cleanup();
        objectLoader.cleanup();
    }
}
