import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import java.util.HashMap;

/**
 * Table class is used to create the table as well as the lamp and helicopter that stands on it.
 * It builds one scene graph with all those objects.
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class Table {

    private Lamp lamp;
    private Helicopter helicopter;
    private NameNode tableRoot;

    private float legDiameter = 0.4f;
    private float legHeight = 3.5f;
    private float length = 14f;
    private float width = 10f;


    public Table(GL3 gl, Shader shader, Shader shader_specular, HashMap<String, int[]> textures) {
        // Leg models
        Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Model cube = new Model(shader, material, new Mat4(1), mesh, textures.get("wood"));


        // Scene graph
        tableRoot = new NameNode("table_root");

        NameNode legLeftFront = new NameNode("left_front");
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(legDiameter, legHeight, legDiameter),
                Mat4Transform.translate(0f - length / 2, 0.5f, 0f + width / 2));
        TransformNode leftFrontTransform = new TransformNode("Mat4(1)", modelMatrix);
        ModelNode leftFrontShape = new ModelNode("cube_LF", cube);

        NameNode legRightFront = new NameNode("right_front");
        modelMatrix = Mat4.multiply(Mat4Transform.scale(legDiameter, legHeight, legDiameter),
                Mat4Transform.translate(0f + length / 2, 0.5f, 0f + width / 2));
        TransformNode rightFrontTransform = new TransformNode("Mat4(1)", modelMatrix);
        ModelNode rightFrontShape = new ModelNode("cube_RF", cube);

        NameNode legLeftRear = new NameNode("left_rear");
        modelMatrix = Mat4.multiply(Mat4Transform.scale(legDiameter, legHeight, legDiameter),
                Mat4Transform.translate(0f - length / 2, 0.5f, 0f - width / 2));
        TransformNode leftRearTransform = new TransformNode("Mat4(1)", modelMatrix);
        ModelNode leftRearShape = new ModelNode("cube_RF", cube);

        NameNode legRightRear = new NameNode("right_rear");
        modelMatrix = Mat4.multiply(Mat4Transform.scale(legDiameter, legHeight, legDiameter),
                Mat4Transform.translate(0f + length / 2, 0.5f, 0f - width / 2));
        TransformNode rightRearTransform = new TransformNode("scale + translate", modelMatrix);
        ModelNode rightRearShape = new ModelNode("cube_RF", cube);

        NameNode tableTop = new NameNode("table_top");
        modelMatrix = Mat4.multiply(Mat4Transform.scale(length / 2, 0.5f, width / 2),
                Mat4Transform.translate(0, legHeight * 2, 0));
        TransformNode tableTopTransform = new TransformNode("scale + translate", modelMatrix);
        ModelNode tableTopShape = new ModelNode("cube_top", cube);

        mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        material = new Material(new Vec3(1f, 1f, 1f), new Vec3(1f, 1f, 1f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Model rectangle = new Model(shader, material, new Mat4(1), mesh, textures.get("table_drawing"));
        NameNode tableDrawing = new NameNode("table_drawing");
        modelMatrix = Mat4.multiply(Mat4Transform.scale(1.5f, 1, 2),
                Mat4Transform.translate(1.7f, 0.05f, 0));
        TransformNode drawingTransform = new TransformNode("drawing_transform", modelMatrix);
        ModelNode drawingModel = new ModelNode("rectangle", rectangle);


        tableRoot.addChild(legLeftFront);
        legLeftFront.addChild(leftFrontTransform);
        leftFrontTransform.addChild(leftFrontShape);

        tableRoot.addChild(legRightFront);
        legRightFront.addChild(rightFrontTransform);
        rightFrontTransform.addChild(rightFrontShape);

        tableRoot.addChild(legLeftRear);
        legLeftRear.addChild(leftRearTransform);
        leftRearTransform.addChild(leftRearShape);

        tableRoot.addChild(legRightRear);
        legRightRear.addChild(rightRearTransform);
        rightRearTransform.addChild(rightRearShape);

        tableRoot.addChild(tableTop);
        tableTop.addChild(tableTopTransform);
        tableTopTransform.addChild(tableTopShape);


        // Lamp
        lamp = new Lamp(gl, shader_specular, textures);
        TransformNode translateTableTop = new TransformNode("translate", Mat4Transform.translate(-2f, 0.25f + legHeight, 0));

        tableTop.addChild(translateTableTop);
        translateTableTop.addChild(lamp.getLampRoot());

        tableTop.addChild(translateTableTop);
        translateTableTop.addChild(tableDrawing);
        tableDrawing.addChild(drawingTransform);
        drawingTransform.addChild(drawingModel);

        // Pen
        Pen pen = new Pen("pen", gl, shader, textures);
        translateTableTop.addChild(pen);

        // Heli
        helicopter = new Helicopter(gl, shader, shader_specular, textures);
        TransformNode translateHeli = new TransformNode("translate heli", Mat4Transform.translate(4.5f, 0.25f, 1.5f));
        translateTableTop.addChild(translateHeli);
        translateHeli.addChild(helicopter.getBase());

        tableRoot.update(Mat4Transform.translate(0, 0, -5));
    }

    // returns the lamp object
    public Lamp getLamp() {
        return lamp;
    }

    // returns the root of the scene graph
    public NameNode getTableRoot() {
        return tableRoot;
    }

    // returns the helicopter object
    public Helicopter getHelicopter() {
        return helicopter;
    }
}
