package nucleus.core.icore;

import nucleus.core.MouseInput;
import nucleus.core.WindowManager;

public interface ILogic {
    void init(WindowManager windowManager) throws Exception;

    void input();

    void update(float interval, MouseInput mouseInput);

    void render();

    void cleanup();
}
