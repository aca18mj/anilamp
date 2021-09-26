import gmaths.Vec3;

/**
 * DirectionalLight class extends the Light class and adds the direction variable
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class DirectionalLight extends Light {
    private Vec3 direction;


    public DirectionalLight(Vec3 direction, Material material) {
        super(material);
        this.direction = direction;
    }

    public DirectionalLight(Vec3 direction) {
        this.direction = direction;
        Material material = new Material();
        material.setAmbient(0.05f, 0.05f, 0.05f);
        material.setDiffuse(0.7f, 0.7f, 0.7f);
        material.setSpecular(0.8f, 0.8f, 0.8f);
        super.setMaterial(material);
    }

    public Vec3 getDirection() {
        return direction;
    }
}
