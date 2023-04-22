package game;

import core.*;
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

import java.util.List;


public class TextGame implements ILogic {



    private final RenderManager renderManager;
    private final ObjectLoader objectLoader;
    private final WindowManager windowManager;
    private final SceneManager sceneManager;
    private Camera camera;
    private Vector3f cameraInc;


    public TextGame(){
        renderManager=new RenderManager();
        objectLoader=new ObjectLoader();
        windowManager=Launcher.getWindowManager();
        sceneManager=new SceneManager(-90);
        camera=new Camera();
        cameraInc=new Vector3f(0,0,0);
    }
    @Override
    public void init() throws Exception {
           renderManager.init();

           Model model=objectLoader.loadOBJModel("/models/cube.obj");
           model.setTexture(new Texture(objectLoader.loadTexture("textures/grassblock.png")),1f);

           Model sun = objectLoader.loadOBJModel("/models/sphere.obj");
           sun.setTexture(new Texture(objectLoader.loadTexture("textures/sun.png")),1f);

           TerrainTexture backgroundTexture=new TerrainTexture(objectLoader.loadTexture("textures/basketball512.png"));
           TerrainTexture blueTexture=new TerrainTexture(objectLoader.loadTexture("textures/sand.png"));
           TerrainTexture redTexture=new TerrainTexture(objectLoader.loadTexture("textures/frosted_ice_0.png"));
           TerrainTexture greenTexture=new TerrainTexture(objectLoader.loadTexture("textures/cobblestone.png"));
           TerrainTexture blendMap=new TerrainTexture(objectLoader.loadTexture("textures/blendMap.png"));

           BlendMapTerrain blendMapTerrain=new BlendMapTerrain(backgroundTexture,redTexture,greenTexture,blueTexture);
           Terrain terrain=new Terrain(new Vector3f(0,-1,-800),objectLoader,
                   new Material(new Vector4f(0f,0f,0f,0f),0.1f),blendMapTerrain,blendMap);

           Terrain terrain2=new Terrain(new Vector3f(-800,-1,-800),objectLoader,
                   new Material(new Vector4f(0,0,0,0),0.1f),blendMapTerrain,blendMap);

           sceneManager.getTerrainList().addAll(List.of(terrain,terrain2));

           for(int i=-5;i<6;i++){
             for(int j=-5;j<6;j++){
                sceneManager.getEntitiesList().add(new Entity(model,new Vector3f(0,0,0),new Vector3f(i,0,j),0.5f));
             }
           }

           sceneManager.getEntitiesList().add(new Entity(sun,new Vector3f(0,0,0),new Vector3f(0,110,-100),1.5f));
           Vector3f sunlightPosition=new Vector3f(0f,102, -100);
           Vector3f sunlightColour=new Vector3f(1,1,1);
           PointLight sunLight = new PointLight(sunlightColour,sunlightPosition,100.0f,0,0,0.2f);

           Model model1=objectLoader.loadOBJModel("/models/torch.obj");
           model1.setTexture(new Texture(objectLoader.loadTexture("textures/torch.png")));
           Entity chest=new Entity(model1,new Vector3f(0,0,0),new Vector3f(-0.5f,0.5f,-0.5f),1f);

           sceneManager.getEntitiesList().add(chest);

           float lightIntensity=100.0f;
           //point light
           Vector3f lightPosition=new Vector3f(0f,10.2f,0f);
           Vector3f lightColour=new Vector3f(1,1,1);
           PointLight pointLight=new PointLight(lightColour,lightPosition,lightIntensity,0,0,1f);

           //directional light
           lightPosition=new Vector3f(-1,100,0);
           lightColour=new Vector3f(1,1,1);
           sceneManager.setDirectionalLight(new DirectionalLight(lightColour,lightPosition,lightIntensity));

           //spot light
               Vector3f coneDirection = new Vector3f(0,-1f,0);
           float cutoff=(float)Math.cos(Math.toRadians(180));

           SpotLight spotLight=new SpotLight(new PointLight(
                   lightColour,
                   new Vector3f(50,3.2f,50),
                   lightIntensity,0,0,0.2f
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

      sceneManager.incLightAngle(2f);
      float lightAngle=sceneManager.getLightAngle();
      DirectionalLight directionalLight=sceneManager.getDirectionalLight();
      if(lightAngle>90){
          directionalLight.setIntensity(0);
          if(lightAngle>=360){
              sceneManager.setLightAngle(-90);
          }
      }else if(lightAngle<=-80 || lightAngle>=80){
          float factor=1-(Math.abs(lightAngle)-80)/10.0f;
          directionalLight.setIntensity(factor);
          directionalLight.getColor().y=Math.max(factor,0.9f);
          directionalLight.getColor().z=Math.max(factor,0.5f);
      }else{
          directionalLight.setIntensity(1);
          directionalLight.getColor().x=1;
          directionalLight.getColor().z=1;
          directionalLight.getColor().y=1;
      }
      double angleRad=Math.toRadians(lightAngle);
      directionalLight.getDirection().x=(float) Math.sin(angleRad);
      directionalLight.getDirection().y=(float) Math.cos(angleRad);

      for(Entity entity:sceneManager.getEntitiesList())
          renderManager.processEntity(entity);
      for (Terrain terrain:sceneManager.getTerrainList())
          renderManager.processTerrain(terrain);

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
