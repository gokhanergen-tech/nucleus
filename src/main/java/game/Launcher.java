package game;

import nucleus.NucleusApp;


public class Launcher {

    public static void main(String[] args){
        ExampleGame exampleGame =new ExampleGame("Test App", 500, 500);

        NucleusApp.start(exampleGame);
    }
}
