package nucleus.core.rendering;

import nucleus.core.Camera;
import nucleus.core.lighting.DirectionalLight;
import nucleus.core.lighting.PointLight;
import nucleus.core.lighting.SpotLight;
import nucleus.core.models.Model;

public interface IRenderer {
    void init() throws Exception;
    void bind(Model model);
    void unbind();
    void prepare(Object entity, Camera camera);
    void render(Camera camera, DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights);
    void cleanup();
}
