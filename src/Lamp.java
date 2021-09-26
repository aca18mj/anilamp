import com.jogamp.opengl.GL3;
import gmaths.Lerp;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import java.util.HashMap;
import java.util.Random;

/**
 * Lamp class is used to create the lamp scene graph and keep track of the position and light
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class Lamp {

    // default position
    public static final float DEFAULT_LOWER_Y = 0f;
    public static final float DEFAULT_LOWER_Z = 15f;
    public static final float DEFAULT_UPPER_Z = -60f;
    public static final float DEFAULT_HEAD_Z  = -15f;

    public enum PoseType {RANDOM, DEFAULT}

    // animation
    private static final float ANIMATION_LENGTH = 1f;
    private boolean animationFinished = true;
    private double animationStartTime;
    private float newLowerY;
    private float newLowerZ;
    private float newUpperZ;
    private float newHeadZ;
    private float currentLowerZ, oldLowerZ;
    private float currentLowerY, oldLowerY;
    private float currentUpperZ, oldUpperZ;
    private float currentHeadZ, oldHeadZ;

    private TransformNode headBumpTransform;
    private SpotLight spotLight;
    private boolean isOn = true;

    // lamp dimensions
    float baseHeight = 0.5f;
    float lowerBranchHeight = 1.5f;
    float middleSphereRadius = 0.5f;
    float upperBranchHeight = 1.5f;
    float headLength = 0.7f;
    float headWidth = 0.3f;
    float headHeight = 0.3f;
    float headBumpLength = 0.3f;
    float headBumpWidth = 0.5f;
    float headBumpHeight = 0.5f;

    // lamp rotation
    private TransformNode rotateHeadZ;
    private TransformNode rotateUpperZ;
    private TransformNode rotateLowerZ;
    private TransformNode rotateLowerY;

    private NameNode lampRoot;


    public Lamp(GL3 gl, Shader shader, HashMap<String, int[]> textures) {
        currentLowerZ = oldLowerZ = DEFAULT_LOWER_Z;
        currentLowerY = oldLowerY = DEFAULT_LOWER_Y;
        currentUpperZ = oldUpperZ = DEFAULT_UPPER_Z;
        currentHeadZ = oldHeadZ = DEFAULT_HEAD_Z;

        buildLamp(gl, shader, textures);
    }

    private void buildLamp(GL3 gl, Shader shaderSpecular, HashMap<String, int[]> textures) {
        // Cube model
        Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Material material = new Material(new Vec3(0.5f, 0.5f, 0.5f),
                new Vec3(0.5f, 0.5f, 0.5f),
                new Vec3(0.5f, 0.5f, 0.5f),
                16.0f);
        Model cube = new Model(shaderSpecular, material, new Mat4(1), mesh, textures.get("iron"), textures.get("iron_specular"));
        Model cubeZebra = new Model(shaderSpecular, material, new Mat4(1), mesh, textures.get("zebra"), textures.get("zebra"));

        // Scene graph
        lampRoot = new NameNode("lamp_root");

        // Base
        NameNode baseNode = new NameNode("base");
        Mat4 modelMatrix = Mat4Transform.scale(1,baseHeight,1);
        TransformNode baseTransform = new TransformNode("tn", modelMatrix);
        ModelNode baseShape = new ModelNode("cube", cube);


        mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Model sphere = new Model(shaderSpecular, material, new Mat4(1), mesh, textures.get("aluminium"), textures.get("aluminium_specular"));
        Model sphereMiddle = new Model(shaderSpecular, material, new Mat4(1), mesh, textures.get("iron"), textures.get("iron_specular"));
        Model ear = new Model(shaderSpecular, material, new Mat4(1), mesh,  textures.get("zebra"), textures.get("zebra"));

        // Lower branch
        NameNode lowerBranch = new NameNode("lower_branch");
        TransformNode translateToBaseTop = new TransformNode("translate", Mat4Transform.translate(0, baseHeight / 2, 0));
        rotateLowerZ = new TransformNode("rotate z", Mat4Transform.rotateAroundZ(currentLowerZ));
        rotateLowerY = new TransformNode("rotate x", Mat4Transform.rotateAroundY(currentLowerY));
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.3f, lowerBranchHeight,0.3f),
                Mat4Transform.translate(0, (lowerBranchHeight / 2) / lowerBranchHeight, 0));
        TransformNode lowerTransform = new TransformNode("tn", modelMatrix);
        ModelNode lowerShape = new ModelNode("sphere", sphere);

        // Middle thingy
        NameNode middleSphere = new NameNode("middle_sphere");
        TransformNode translateLowerTop = new TransformNode("translate", Mat4Transform.translate(0, lowerBranchHeight, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.scale(middleSphereRadius, middleSphereRadius, middleSphereRadius),
                Mat4Transform.translate(0, 0, 0));
        TransformNode middleSphereTransform = new TransformNode("tn", modelMatrix);
        ModelNode middleShape = new ModelNode("sphere", sphereMiddle);

        // Upper branch
        NameNode upperBranch = new NameNode("upper_branch");
        rotateUpperZ = new TransformNode("rotate z", Mat4Transform.rotateAroundZ(currentUpperZ));
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.3f, upperBranchHeight,0.3f),
                Mat4Transform.translate(0, (upperBranchHeight / 2) / upperBranchHeight, 0));
        TransformNode upperTransform = new TransformNode("tn", modelMatrix);
        ModelNode upperShape = new ModelNode("sphere", sphere);

        // Head
        NameNode head = new NameNode("head");
        rotateHeadZ = new TransformNode("rotate z", Mat4Transform.rotateAroundZ(currentHeadZ));
        TransformNode translateUpperTop = new TransformNode("translate", Mat4Transform.translate(0, upperBranchHeight, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.scale(headLength, headHeight, headWidth),
                Mat4Transform.translate(0, (headHeight / 2) / headHeight, 0));
        TransformNode headTransform = new TransformNode("hn", modelMatrix);
        ModelNode headShape = new ModelNode("cube", cubeZebra);

        // Head bump
        NameNode headBump = new NameNode("head_bump");
        TransformNode translateHeadFront = new TransformNode("translate", Mat4Transform.translate(headLength / 2, 0, 0));
        modelMatrix = Mat4.multiply(Mat4Transform.scale(headBumpLength, headBumpHeight, headBumpWidth),
                Mat4Transform.translate((headBumpLength / 2) / headBumpLength, 0, 0));
        headBumpTransform = new TransformNode("hn", modelMatrix);
        ModelNode headBumpShape = new ModelNode("cube", cube);

        // Ears
        NameNode ears = new NameNode("ears");
        TransformNode translateHeadTop = new TransformNode("translate", Mat4Transform.translate(0, headHeight, 0));
        // Ear 1
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.2f, 0.2f, 0.2f),
                Mat4Transform.translate(1f, 0, 0.8f));
        TransformNode ear_1_transform = new TransformNode("ear_1", modelMatrix);
        ModelNode ear_1_shape = new ModelNode("sphere", ear);
        // Ear 2
        modelMatrix = Mat4.multiply(Mat4Transform.scale(0.2f, 0.2f, 0.2f),
                Mat4Transform.translate(1f, 0, -0.8f));
        TransformNode ear_2_transform = new TransformNode("ear_2", modelMatrix);
        ModelNode ear_2_shape = new ModelNode("sphere", ear);

        // Scene graph
        lampRoot.addChild(baseNode);
        baseNode.addChild(baseTransform);
        baseTransform.addChild(baseShape);

        // lower branch
        baseNode.addChild(translateToBaseTop);
        translateToBaseTop.addChild(rotateLowerZ);
        rotateLowerZ.addChild(rotateLowerY);
        rotateLowerY.addChild(lowerBranch);
        lowerBranch.addChild(lowerTransform);
        lowerTransform.addChild(lowerShape);

        // middle sphere
        lowerBranch.addChild(translateLowerTop);
        translateLowerTop.addChild(middleSphere);
        middleSphere.addChild(middleSphereTransform);
        middleSphereTransform.addChild(middleShape);

        // upper branch
        translateLowerTop.addChild(rotateUpperZ);
        rotateUpperZ.addChild(upperBranch);
        upperBranch.addChild(upperTransform);
        upperTransform.addChild(upperShape);

        // head
        upperBranch.addChild(translateUpperTop);
        translateUpperTop.addChild(rotateHeadZ);
        rotateHeadZ.addChild(head);
        head.addChild(headTransform);
        headTransform.addChild(headShape);

        // head bump
        headTransform.addChild(translateHeadFront);
        translateHeadFront.addChild(headBump);
        headBump.addChild(headBumpTransform);
        headBumpTransform.addChild(headBumpShape);

        // ears
        head.addChild(translateHeadTop);
        translateHeadTop.addChild(ears);
        // ear 1
        ears.addChild(ear_1_transform);
        ear_1_transform.addChild(ear_1_shape);
        // ear 2
        ears.addChild(ear_2_transform);
        ear_2_transform.addChild(ear_2_shape);

        // Spotlight
        spotLight = new SpotLight(new Vec3(0, 0, 0), new Vec3(0, 0, 0));
    }

    /**
     * Recalculates position and direction of the postlight based on the head position and orientation
     */
    public void updateLight() {
        //update spotlight direction + position
        float[][] worldMatrix = headBumpTransform.worldTransform.getValues();
        Vec3 lightPosition = new Vec3(worldMatrix[0][3], worldMatrix[1][3], worldMatrix[2][3]);
        Vec3 lightDirection = new Vec3(worldMatrix[0][0], worldMatrix[1][0], worldMatrix[2][0]);

        spotLight.setPosition(lightPosition);
        spotLight.setDirection(lightDirection);
    }

    public SpotLight getSpotLight() {
        return spotLight;
    }

    /**
     * Calculates new position variables
     * @param poseType either RANDOM or DEFAULT position
     */
    public void setNewPose(PoseType poseType) {
        if (!animationFinished)
            return;

        Random random = new Random(System.currentTimeMillis());

        if (poseType == PoseType.RANDOM) {
            newLowerZ = (float) random.nextInt(45);
            newLowerY = (float) random.nextInt(45) - 23;
            newUpperZ = (float) random.nextInt(45) - 45;
            newHeadZ = (float) random.nextInt(45) - 45;
        } else {
            newLowerZ = DEFAULT_LOWER_Z;
            newLowerY = DEFAULT_LOWER_Y;
            newUpperZ = DEFAULT_UPPER_Z;
            newHeadZ = DEFAULT_HEAD_Z;
        }

        animationFinished = false;
        animationStartTime = Scene.getSeconds();
    }

    /**
     * Updates the lamp pose based on time passed since animation started
     */
    public void animatePose() {
        float timePassed = (float) (Scene.getSeconds() - animationStartTime);

        if (timePassed >= ANIMATION_LENGTH) {
            rotateLowerY.setTransform(Mat4Transform.rotateAroundY(newLowerY));
            rotateLowerZ.setTransform(Mat4Transform.rotateAroundZ(newLowerY));
            rotateUpperZ.setTransform(Mat4Transform.rotateAroundZ(newUpperZ));
            rotateHeadZ.setTransform( Mat4Transform.rotateAroundZ(newHeadZ));

            oldLowerY = newLowerY;
            oldLowerZ = newLowerZ;
            oldUpperZ = newUpperZ;
            oldHeadZ = newHeadZ;

            rotateLowerZ.update();
            animationFinished = true;
        }

        currentLowerY = Lerp.lerp(oldLowerY, newLowerY, timePassed / ANIMATION_LENGTH);
        currentLowerZ = Lerp.lerp(oldLowerZ, newLowerZ, timePassed / ANIMATION_LENGTH);
        currentUpperZ = Lerp.lerp(oldUpperZ, newUpperZ, timePassed / ANIMATION_LENGTH);
        currentHeadZ = Lerp.lerp(oldHeadZ, newHeadZ, timePassed / ANIMATION_LENGTH);

        rotateLowerY.setTransform(Mat4Transform.rotateAroundY(currentLowerY));
        rotateLowerZ.setTransform(Mat4Transform.rotateAroundZ(currentLowerZ));
        rotateUpperZ.setTransform(Mat4Transform.rotateAroundZ(currentUpperZ));
        rotateHeadZ.setTransform( Mat4Transform.rotateAroundZ(currentHeadZ));

        lampRoot.update();
    }

    public boolean isAnimating() {
        return !animationFinished;
    }

    public NameNode getLampRoot() {
        return lampRoot;
    }

    /**
     * Turns the spotlight on or off
     */
    public void switchLight() {
        Material material = new Material();
        if (isOn) {
            material.setAmbient(0, 0, 0);
            material.setDiffuse(0, 0, 0);
            material.setSpecular(0, 0, 0);
        } else {
            material.setAmbient(0.05f, 0.05f,0.05f);
            material.setDiffuse(0.8f, 0.8f, 0.8f);
            material.setSpecular(1f, 1f, 1f);
        }

        isOn = !isOn;

        spotLight.setMaterial(material);
    }
}
