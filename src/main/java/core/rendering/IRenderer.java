package core.rendering;

import core.Camera;
import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.lighting.SpotLight;
import core.models.Entity;
import core.models.Model;

public interface IRenderer {
    void init() throws Exception;
    void bind(Model model);
    void unbind();
    void prepare(Object entity, Camera camera);
    void render(Camera camera, DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights);
    void cleanup();
}
