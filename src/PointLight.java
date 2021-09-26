import gmaths.Vec3;

/**
 * PointLight is used for representing the point lights in the scene
 * It extends the general light class and adds position variable
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class PointLight extends Light {

    private Vec3 position;

    public PointLight(Vec3 position) {
        Material material = new Material();
        material.setAmbient(0.05f, 0.05f, 0.05f);
        material.setDiffuse(0.7f, 0.7f, 0.7f);
        material.setSpecular(0.8f, 0.8f, 0.8f);
        super.setMaterial(material);
        this.position = position;
    }

    public PointLight(Material material, Vec3 position) {
        super(material);
        this.position = position;
    }

    public Vec3 getPosition() {
        return position;
    }
}
