import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import java.util.HashMap;

/**
 * The pen object
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class Pen extends SGNode {

    private float penLength = 1f;
    private float penDiameter = 0.2f;

    public Pen(String name, GL3 gl, Shader shader, HashMap<String, int[]> textures) {
        super(name);
        buildPen(gl, shader, textures);
    }

    // Builds the simple scene graph of the pen
    private void buildPen(GL3 gl, Shader shader, HashMap<String, int[]> textures) {
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Material material = new Material(new Vec3(0f, 1f, 0.31f), new Vec3(0f, 1f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Model sphere = new Model(shader, material, new Mat4(1), mesh, textures.get("plastic"));

        //Scene graph
        NameNode penMain = new NameNode("main");
        TransformNode rotatePen = new TransformNode("rotate Y", Mat4Transform.rotateAroundY(30));
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(penLength, penDiameter, penDiameter),
                Mat4Transform.translate(4, (penDiameter / 2) / penDiameter, 6));
        TransformNode mainTransform = new TransformNode("tn", modelMatrix);
        ModelNode mainShape = new ModelNode("sphere", sphere);

        addChild(penMain);
        penMain.addChild(rotatePen);
        rotatePen.addChild(mainTransform);
            mainTransform.addChild(mainShape);
    }
}
