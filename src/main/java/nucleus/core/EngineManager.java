package nucleus.core;

import nucleus.core.icore.ILogic;
import nucleus.utils.Consts;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import game.Launcher;
public class EngineManager {
    public static final long NANOSCOND=1000000000l;
    public static final float FRAME_RATE=1000;

    private static int fps;
    private static float frameTime=1.0f/FRAME_RATE;

    private boolean isRunning;

    private WindowManager windowManager;
    private GLFWErrorCallback errorCallback;
    private MouseInput mouseInput;

    private ILogic iLogic;
    public void init(ILogic iLogic, WindowManager windowManager) throws Exception{
        GLFW.glfwSetErrorCallback(errorCallback=GLFWErrorCallback.createPrint(System.err));

        this.windowManager=windowManager;
        this.iLogic=iLogic;

        mouseInput=new MouseInput();
        windowManager.init();
        iLogic.init(windowManager);
        mouseInput.init();
    }
    public void start(ILogic iLogic, WindowManager windowManager) throws Exception{
        init(iLogic,windowManager);
        if(isRunning)
            return;
        run();
    }

    private void run() {
        this.isRunning=true;
        int frames=0;
        long frameCounter=0;
        long lastTime=System.nanoTime();
        double unProcessedTime=0;
        while (isRunning){
            boolean render=false;
            long startTime=System.nanoTime();
            long passedTime=startTime-lastTime;
            lastTime=startTime;
            unProcessedTime+=passedTime/(double)NANOSCOND;

            frameCounter+=passedTime;
            input();
            while (unProcessedTime>frameTime){
                render=true;
                unProcessedTime-=frameTime;
                if(windowManager.windowShouldClose()){
                    stop();
                }
                if(frameCounter>=NANOSCOND){
                    setFps(frames);
                    windowManager.setTitle(Consts.TITLE+" "+getFps()+" fps");
                    frames=0;
                    frameCounter=0;
                }
            }
            if(render){
                update(frameTime);
                render();
                frames++;
            }
        }
        cleanup();

    }

    private static void setFps(int fps) {
        EngineManager.fps = fps;
    }

    private static int getFps() {
        return fps;
    }

    private void stop(){
      if(!isRunning){
          return;
      }
      isRunning=false;
    }
    private void input(){
        mouseInput.input();
        iLogic.input();
    }
    private void update(float interval){
       iLogic.update(interval,mouseInput);
    }
    private void render(){
        iLogic.render();
        windowManager.update();

    }
    private void cleanup(){
        windowManager.cleanup();
        iLogic.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }
}
