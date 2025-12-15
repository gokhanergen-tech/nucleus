package nucleus;

import nucleus.core.EngineManager;
import nucleus.core.WindowManager;
import nucleus.core.icore.ILogic;
import nucleus.utils.Consts;

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

    public static void start(ILogic textGame) {
        windowManager=new WindowManager(Consts.TITLE,1300,600,false);

        EngineManager engineManager=new EngineManager();
        try {
            engineManager.start(textGame, windowManager);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
