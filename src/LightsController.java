import com.jogamp.opengl.GL3;
import java.util.ArrayList;

/**
 * LightsController keeps track of all the lights in the scene
 * It can also update all the light information in the shaders (uniform values)
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class LightsController {

    private ArrayList<DirectionalLight> dirLights;
    private ArrayList<PointLight> pointLights;
    private ArrayList<SpotLight> spotLights;

    public LightsController() {
        this.dirLights = new ArrayList<>();
        this.pointLights = new ArrayList<>();
        this.spotLights = new ArrayList<>();
    }

    public void addPointLight(PointLight light) {
        pointLights.add(light);
    }

    public void addDirLight(DirectionalLight light) {
        dirLights.add(light);
    }

    public void addSpotLight(SpotLight light) {
        spotLights.add(light);
    }

    /**
     * Used to update light uniform variables in all shaders that use light calculations
     * @param gl
     * @param shaders list of shaders used in the scene (all shaders affected by light must be here)
     */
    public void updateShaders(GL3 gl, ArrayList<Shader> shaders) {
        for (Shader shader : shaders) {

            if (!shader.isAffectedByLight())
                continue;

            shader.use(gl);


            for (int i = 0; i < dirLights.size(); i++) {
                shader.setVec3(gl, "dirLights["+i+"].direction", dirLights.get(i).getDirection());
                shader.setVec3(gl, "dirLights["+i+"].ambient", dirLights.get(i).getMaterial().getAmbient());
                shader.setVec3(gl, "dirLights["+i+"].diffuse", dirLights.get(i).getMaterial().getDiffuse());
                shader.setVec3(gl, "dirLights["+i+"].specular", dirLights.get(i).getMaterial().getSpecular());
            }

            for (int i = 0; i < pointLights.size(); i++) {
                shader.setVec3(gl, "pointLights["+i+"].position", pointLights.get(i).getPosition());
                shader.setVec3(gl, "pointLights["+i+"].ambient", pointLights.get(i).getMaterial().getAmbient());
                shader.setVec3(gl, "pointLights["+i+"].diffuse", pointLights.get(i).getMaterial().getDiffuse());
                shader.setVec3(gl, "pointLights["+i+"].specular", pointLights.get(i).getMaterial().getSpecular());
                shader.setFloat(gl, "pointLights["+i+"].constant", Light.LIGHT_CONSTANT);
                shader.setFloat(gl, "pointLights["+i+"].linear",  Light.LIGHT_LINEAR);
                shader.setFloat(gl, "pointLights["+i+"].quadratic", Light.LIGHT_QUADRATIC);
            }

            for (int i = 0; i < spotLights.size(); i++) {
                shader.setVec3(gl, "spotLights["+i+"].position", spotLights.get(i).getPosition());
                shader.setVec3(gl, "spotLights["+i+"].direction", spotLights.get(i).getDirection());
                shader.setVec3(gl, "spotLights["+i+"].ambient", spotLights.get(i).getMaterial().getAmbient());
                shader.setVec3(gl, "spotLights["+i+"].diffuse", spotLights.get(i).getMaterial().getDiffuse());
                shader.setVec3(gl, "spotLights["+i+"].specular", spotLights.get(i).getMaterial().getSpecular());
                shader.setFloat(gl, "spotLights["+i+"].cutOff", spotLights.get(i).getCutOff());
                shader.setFloat(gl, "spotLights["+i+"].outerCutOff", spotLights.get(i).getOuterCutOff());
                shader.setFloat(gl, "spotLights["+i+"].constant", Light.LIGHT_CONSTANT);
                shader.setFloat(gl, "spotLights["+i+"].linear", Light.LIGHT_LINEAR);
                shader.setFloat(gl, "spotLights["+i+"].quadratic", Light.LIGHT_QUADRATIC);
            }
        }
    }
}
