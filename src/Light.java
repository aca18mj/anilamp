/**
 * General light class
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class Light {

  // Some constants for light calculations (from Joey's tutorials)
  public static final float LIGHT_CONSTANT = 1.0f;
  public static final float LIGHT_LINEAR = 0.09f;
  public static final float LIGHT_QUADRATIC = 0.032f;

  private Material material;
    
  public Light() {}

  public Light(Material material) {
    this.material = material;
  }

  public void setMaterial(Material material) {this.material = material;}

  public Material getMaterial() {return this.material;}
}