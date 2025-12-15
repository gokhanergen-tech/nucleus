package nucleus;

import nucleus.core.icore.ILogic;

public abstract class NucleusGame implements ILogic {
    private int width, height;
    private String title;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NucleusGame(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }
}
