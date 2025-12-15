package nucleus;

import nucleus.core.EngineManager;
import nucleus.core.WindowManager;

public class NucleusApp {
    private static WindowManager windowManager;
    public static WindowManager getWindowManager() {
        return windowManager;
    }

    public static long getWindow(){
        if(getWindowManager()!=null)
         return getWindowManager().getWindow();
        throw new RuntimeException("Not Initialized App");
    }

    public static void start(NucleusGame textGame) {
        windowManager=new WindowManager(textGame.getTitle(), textGame.getWidth(), textGame.getHeight(), false);

        EngineManager engineManager=new EngineManager();
        try {
            engineManager.start(textGame, windowManager);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
