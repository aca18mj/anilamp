import com.jogamp.opengl.GL3;
import gmaths.Lerp;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import java.util.HashMap;

/**
 * Helicopter class is used to create and animate the helicopter model
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class Helicopter {

    private float baseSize = 0.5f;
    private float sphereDiameter = 0.7f;
    private NameNode base;

    // animation
    TransformNode translateAll;
    TransformNode rotateTop;
    private boolean animating = false;
    private float rotationSpeed = 500f;
    private float rotationProgress = 0;
    private static final float ANIMATION_LENGTH = 2f;
    private double animationStartTime;
    private String direction = "up";
    private float defaultPosY = 0;
    private float flightLatitude = 3f;
    private boolean land = false;

    public Helicopter(GL3 gl, Shader shader, Shader shader_specular, HashMap<String, int[]> textures) {
        buildHelicopter(gl, shader, shader_specular, textures);
    }

    /**
     * Builds the helicopter scenegraph
     * @param gl
     * @param shader shader without specular map
     * @param shader_specular shader with specular map
     */
    public void buildHelicopter(GL3 gl, Shader shader, Shader shader_specular, HashMap<String, int[]> textures) {
        // Base
        Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Material material = new Material(new Vec3(0.6f, 0.6f, 0.6f),
                                         new Vec3(0.8f, 0.8f, 0.8f),
                                         new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Model cube = new Model(shader_specular, material, new Mat4(1), mesh, textures.get("heli_base"), textures.get("heli_base_specular"));

        // Wings
        Material material_wings = new Material(new Vec3(0.8f, 0.8f, 0.8f),
                new Vec3(0.8f, 0.8f, 0.8f),
                new Vec3(0.9f, 0.9f, 0.9f), 64.0f);
        Model wing = new Model(shader, material_wings, new Mat4(1), mesh, textures.get("plastic"));

        // Sphere
        mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Model sphere = new Model(shader_specular, material, new Mat4(1), mesh, textures.get("heli_sphere"), textures.get("heli_sphere_specular"));

        // Scene Graph
        // Base
        base = new NameNode("base");
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(baseSize, baseSize, baseSize),
                Mat4Transform.translate(0, 0, 0));
        TransformNode baseTransform = new TransformNode("tn", modelMatrix);
        ModelNode baseShape = new ModelNode("cube", cube);

        // Top sphere
        NameNode topSphere = new NameNode("top_sphere");
        TransformNode translateToBaseTop = new TransformNode("translate", Mat4Transform.translate(0, baseSize / 2, 0));
        rotateTop = new TransformNode("rotate y", Mat4Transform.rotateAroundY(0));
        modelMatrix = Mat4.multiply(Mat4Transform.scale(sphereDiameter, sphereDiameter, sphereDiameter),
                Mat4Transform.translate(0, (sphereDiameter / 2) / sphereDiameter, 0));
        TransformNode topSphereTransform = new TransformNode("tn", modelMatrix);
        ModelNode topSphereShape = new ModelNode("sphere", sphere);

        // Left wing
        NameNode leftWing = new NameNode("left_wing");
        TransformNode translateSphereMiddle = new TransformNode("translate", Mat4Transform.translate(0, sphereDiameter / 2, 0));
        TransformNode rotateLeftWing = new TransformNode("rotate x", Mat4Transform.rotateAroundX(45));
        modelMatrix = Mat4.multiply(Mat4Transform.scale(1, 0.3f, 0.05f),
                Mat4Transform.translate(-0.5f, 0, 0));
        TransformNode leftWingTransform = new TransformNode("tn", modelMatrix);
        ModelNode leftWingShape = new ModelNode("cube", wing);

        // Right wing
        NameNode rightWing = new NameNode("right_wing");
        TransformNode rotateRightWing = new TransformNode("rotate x", Mat4Transform.rotateAroundX(-45));
        modelMatrix = Mat4.multiply(Mat4Transform.scale(1, 0.3f, 0.05f),
                Mat4Transform.translate(0.5f, 0, 0));
        TransformNode rightWingTransform = new TransformNode("tn", modelMatrix);
        ModelNode rightWingShape = new ModelNode("cube", wing);

        translateAll = new TransformNode("translate", Mat4Transform.translate(0, 0, 0));

        base.addChild(translateAll);
        translateAll.addChild(baseTransform);
        baseTransform.addChild(baseShape);

        translateAll.addChild(translateToBaseTop);
        translateToBaseTop.addChild(rotateTop);
        rotateTop.addChild(topSphere);
        topSphere.addChild(topSphereTransform);
        topSphereTransform.addChild(topSphereShape);

        rotateTop.addChild(translateSphereMiddle);
        translateSphereMiddle.addChild(rotateLeftWing);
        rotateLeftWing.addChild(leftWing);
        leftWing.addChild(leftWingTransform);
        leftWingTransform.addChild(leftWingShape);

        translateSphereMiddle.addChild(rotateRightWing);
        rotateRightWing.addChild(rightWing);
        rightWing.addChild(rightWingTransform);
        rightWingTransform.addChild(rightWingShape);
    }

    /**
     * Initiate the start of animation
     */
    public void startFlight() {
        if (animating)
            return;

        land = false;
        animating = true;
        animationStartTime = Scene.getSeconds();
        direction = "up";
    }

    /**
     * Animates the wing rotation and flight altitude based on time passed from start of the animation and
     * time passed since last frame was rendered
     * @param delta time passed since last frame was rendered
     */
    public void animate(float delta) {
        rotateWings(delta); //animate wings

        float timePassed = (float) (Scene.getSeconds() - animationStartTime);

        float newPosY;
        if (direction.equals("up")) {
            newPosY = Lerp.lerp(defaultPosY, defaultPosY + flightLatitude, timePassed / ANIMATION_LENGTH);
        } else {
            newPosY = Lerp.lerp(defaultPosY + flightLatitude, defaultPosY, timePassed / ANIMATION_LENGTH);
        }

        translateAll.setTransform(Mat4Transform.translate(0, newPosY, 0));
        translateAll.update();

        if (timePassed > ANIMATION_LENGTH) {
            animationStartTime = Scene.getSeconds();

            if (direction.equals("up"))
                direction = "down";
            else if (direction.equals("down") && !land)
                direction = "up";
            else {
                animating = false;
            }
        }
    }

    /**
     * Updates wings position
     * @param delta time passed since last frame was rendered
     */
    public void rotateWings(float delta) {
        rotationProgress = (rotationProgress + rotationSpeed * delta) % 360;
        rotateTop.setTransform(Mat4Transform.rotateAroundY(rotationProgress));
        rotateTop.update();
    }

    /**
     * When called and helicopter is animating it will not lift from the table once landed
     */
    public void turnOff() {
        land = true;
    }

    public boolean isAnimating() {
        return animating;
    }

    public NameNode getBase() {
        return base;
    }
}
