import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import java.util.HashMap;

/**
 * Room class is used for creating walls, floor, landscape objects and two light sources
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class Room {

    private Model[] models = new Model[4];
    private PointLight pointLight;
    private DirectionalLight dirLight;

    public Room(GL3 gl, Shader shader, HashMap<String, int[]> textures) {
        pointLight = new PointLight(new Vec3(0f, 5f, 0f));
        dirLight = new DirectionalLight(new Vec3(0.2f, -1, 0.2f));

        // Shader for the landscape. It does not need light updates.
        Shader unlit_shader = new Shader(gl, "vs_unlit.shader", "fs_unlit.shader");


        // Models
        // Floor
        Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        Material material = new Material(new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.5f, 0.5f, 0.5f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
        Mat4 modelMatrix = Mat4Transform.scale(16, 1f, 16);
        Model floor = new Model(shader, material, modelMatrix, mesh, textures.get("floor"));
        models[0] = floor;

        // Wall 1
        mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
        modelMatrix = Mat4Transform.scale(16, 1f, 16);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(0, 8, -8), modelMatrix);
        Model wall_1 = new Model(shader, material, modelMatrix, mesh, textures.get("wall"));
        models[1] = wall_1;

        // Wall with hole
        mesh = new Mesh(gl, SquareWithHole.vertices.clone(), SquareWithHole.indices.clone());
        material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
        modelMatrix = Mat4Transform.scale(16, 1f, 16);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(-8, 8, 0), modelMatrix);
        Model wall_hole = new Model(shader, material, modelMatrix, mesh, textures.get("wall"));
        models[2] = wall_hole;

        // View behind window
        mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
        material = new Material(new Vec3(1f, 1f, 1f), new Vec3(1f, 1f, 1f), new Vec3(0f, 0f, 0f), 32.0f);
        modelMatrix = Mat4Transform.scale(32, 1f, 32);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(-20, 8, -5), modelMatrix);
        Model landscape = new Model(unlit_shader, material, modelMatrix, mesh, textures.get("landscape"));
        models[3] = landscape;
    }

    // Returns all created models (walls, floor, landscape)
    public Model[] getModels() {
        return models;
    }

    // Returns the point light from middle of the room
    public PointLight getPointLight() {
        return pointLight;
    }

    // Returns the directional light that shines down on the room
    public DirectionalLight getDirLight() {
        return dirLight;
    }
}
