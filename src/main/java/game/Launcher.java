package game;

import core.EngineManager;
import core.WindowManager;
import utils.Consts;


public class Launcher {
    private static WindowManager windowManager;
    public static WindowManager getWindowManager() {
        return windowManager;
    }
    private static TextGame textGame;

    public static void main(String[] args){

        windowManager=new WindowManager(Consts.TITLE,1300,600,false);
        textGame=new TextGame();

        EngineManager engineManager=new EngineManager();
        try {
             engineManager.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static long getWindow(){
        return windowManager.getWindow();
    }

    public static TextGame getTextGame() {
        return textGame;
    }
}
