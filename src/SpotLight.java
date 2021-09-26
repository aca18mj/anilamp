import gmaths.Vec3;

/**
 * SpotLight class extends the Light class and adds additional variables for defining diameter, direction and position
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class SpotLight extends Light{

    // Spotlight diameter
    private float cutOff = (float) Math.cos(Math.toRadians(12.5f));
    private float outerCutOff = (float) Math.cos(Math.toRadians(17.5f));

    private Vec3 position;
    private Vec3 direction;

    public SpotLight(Vec3 position, Vec3 direction) {
        this.position = position;
        this.direction = direction;

        setDefaultMaterial();
    }

    public SpotLight(Vec3 position, Vec3 direction, Material material) {
        super(material);
        this.position = position;
        this.direction = direction;
    }

    public void setDefaultMaterial() {
        Material material = new Material();
        material.setAmbient(0.05f, 0.05f, 0.05f);
        material.setDiffuse(0.7f, 0.7f, 0.7f);
        material.setSpecular(1f, 1f, 1f);
        setMaterial(material);
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec3 getDirection() {
        return direction;
    }

    public float getCutOff() {
        return cutOff;
    }

    public float getOuterCutOff() {
        return outerCutOff;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public void setDirection(Vec3 direction) {
        this.direction = direction;
    }

    public void setMaterial(Material material) {
        super.setMaterial(material);
    }
}
