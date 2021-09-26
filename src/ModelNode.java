import com.jogamp.opengl.*;

/**
 * This class was provided by the lecturer
 *
 */
public class ModelNode extends SGNode {

    protected Model model;

    public ModelNode(String name, Model m) {
        super(name);
        model = m;
    }

    public void draw(GL3 gl, Camera camera) {
        model.render(gl, worldTransform, camera);
        for (int i = 0; i < children.size(); i++) {
            children.get(i).draw(gl, camera);
        }
    }
}