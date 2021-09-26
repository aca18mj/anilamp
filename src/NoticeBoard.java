import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import java.util.HashMap;

/**
 * NoticeBoard class is used to crete the notice board on the wall along with two drawings
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class NoticeBoard {

    private NameNode boardRoot;

    private float width = 8f;
    private float height = 5f;
    private float edgeWidth = 0.8f;
    private float edgeDepth = 0.2f;

    public NoticeBoard(GL3 gl, Shader shader, HashMap<String, int[]> textures) {

        Mesh mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        Material material = new Material(new Vec3(0.8f, 0.8f, 0.8f), new Vec3(0.8f, 0.5f, 0.8f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Model edge = new Model(shader, material, new Mat4(1), mesh, textures.get("wood"));

        boardRoot = new NameNode("board_root");
        //frame
        NameNode edgeTop = new NameNode("edge_top");
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(width - edgeWidth, edgeWidth, edgeDepth),
                Mat4Transform.translate(0, height / edgeWidth / 2 - edgeWidth / 2, 0));
        TransformNode topTransform = new TransformNode("Mat4(1)", modelMatrix);
        ModelNode topShape = new ModelNode("edge_top", edge);

        NameNode edgeBottom = new NameNode("edge_bottom");
        modelMatrix = Mat4.multiply(Mat4Transform.scale(width - edgeWidth, edgeWidth, edgeDepth),
                Mat4Transform.translate(0, -height / edgeWidth / 2 + edgeWidth / 2, 0));
        TransformNode bottomTransform = new TransformNode("Mat4(1)", modelMatrix);
        ModelNode bottomShape = new ModelNode("edge_bottom", edge);

        NameNode edgeLeft = new NameNode("edge_left");
        modelMatrix = Mat4.multiply(Mat4Transform.scale(edgeWidth, height, edgeDepth),
                Mat4Transform.translate(-width / edgeWidth / 2, 0, 0));
        TransformNode leftTransform = new TransformNode("Mat4(1)", modelMatrix);
        ModelNode leftShape = new ModelNode("edge_left", edge);

        NameNode edgeRight = new NameNode("edge_right");
        modelMatrix = Mat4.multiply(Mat4Transform.scale(edgeWidth, height, edgeDepth),
                Mat4Transform.translate(width / edgeWidth / 2, 0, 0));
        TransformNode rightTransform = new TransformNode("Mat4(1)", modelMatrix);
        ModelNode rightShape = new ModelNode("edge_right", edge);

        // Plane
        mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        material = new Material(new Vec3(0.5f, 0.47f, 0.3f), new Vec3(0.5f, 0.47f, 0.3f), new Vec3(0.2f, 0.2f, 0.2f), 10.0f);
        Model plane = new Model(shader, material, new Mat4(1), mesh, textures.get("board"));

        NameNode planeNode = new NameNode("plane");
        modelMatrix = Mat4.multiply(Mat4Transform.scale(width, height, 1),
                Mat4Transform.rotateAroundX(90));
        TransformNode planeTransform = new TransformNode("Mat4(1)", modelMatrix);
        ModelNode planeShape = new ModelNode("plane", plane);

        // Drawing
        material = new Material(new Vec3(1f, 1f, 1f), new Vec3(1f, 1f, 1f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Model drawing = new Model(shader, material, new Mat4(1), mesh, textures.get("drawing_1"));

        NameNode drawingNode = new NameNode("drawing");
        modelMatrix = Mat4.multiply(Mat4Transform.scale(1.5f, 2, 1), Mat4Transform.rotateAroundX(90));
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(30), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(-1f, 0, 0.2f), modelMatrix);
        TransformNode drawingTransform = new TransformNode("Mat4(1)", modelMatrix);
        ModelNode drawingShape = new ModelNode("plane", drawing);

        Model secondDrawing = new Model(shader, material, new Mat4(1), mesh, textures.get("drawing_2"));
        NameNode drawingNode_2 = new NameNode("drawing_2");
        modelMatrix = Mat4.multiply(Mat4Transform.scale(1.5f, 2, 1), Mat4Transform.rotateAroundX(90));
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(15), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(2.5f, 0, 0.2f), modelMatrix);
        TransformNode drawing_2_transform = new TransformNode("Mat4(1)", modelMatrix);
        ModelNode drawing_2_shape = new ModelNode("plane", secondDrawing);

        boardRoot.addChild(edgeTop);
        edgeTop.addChild(topTransform);
        topTransform.addChild(topShape);

        boardRoot.addChild(edgeBottom);
        edgeBottom.addChild(bottomTransform);
        bottomTransform.addChild(bottomShape);

        boardRoot.addChild(edgeLeft);
        edgeLeft.addChild(leftTransform);
        leftTransform.addChild(leftShape);

        boardRoot.addChild(edgeRight);
        edgeRight.addChild(rightTransform);
        rightTransform.addChild(rightShape);

        boardRoot.addChild(planeNode);
        planeNode.addChild(planeTransform);
        planeTransform.addChild(planeShape);

        boardRoot.addChild(drawingNode);
        drawingNode.addChild(drawingTransform);
        drawingTransform.addChild(drawingShape);

        boardRoot.addChild(drawingNode_2);
        drawingNode_2.addChild(drawing_2_transform);
        drawing_2_transform.addChild(drawing_2_shape);

        boardRoot.update(Mat4Transform.translate(0, 8, -7.9f));
    }

    // Returns the root of the board scene graph
    public NameNode getBoardRoot() {
        return boardRoot;
    }
}
