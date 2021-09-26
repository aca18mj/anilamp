import gmaths.*;

import java.util.ArrayList;

import com.jogamp.opengl.*;

/**
 * This class was provided by the lecturer
 * Added dispose function for disposing all models in the scene graph
 */
public class SGNode {

    protected String name;
    protected ArrayList<SGNode> children;
    protected Mat4 worldTransform;

    public SGNode(String name) {
        children = new ArrayList<SGNode>();
        this.name = name;
        worldTransform = new Mat4(1);
    }

    public void addChild(SGNode child) {
        children.add(child);
    }

    public void update() {
        update(worldTransform);
    }

    protected void update(Mat4 t) {
        worldTransform = t;
        for (int i = 0; i < children.size(); i++) {
            children.get(i).update(t);
        }
    }

    protected String getIndentString(int indent) {
        String s = "" + indent + " ";
        for (int i = 0; i < indent; ++i) {
            s += "  ";
        }
        return s;
    }

    public void print(int indent, boolean inFull) {
        System.out.println(getIndentString(indent) + "Name: " + name);
        if (inFull) {
            System.out.println("worldTransform");
            System.out.println(worldTransform);
        }
        for (int i = 0; i < children.size(); i++) {
            children.get(i).print(indent + 1, inFull);
        }
    }

    public void draw(GL3 gl, Camera camera) {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).draw(gl, camera);
        }
    }

    /**
     * Recursively traverses the scene graph tree and disposes all models
     * @param gl
     * @param node
     */
    public void dispose(GL3 gl, SGNode node) {
        for (SGNode n : children)
            n.dispose(gl, n);

        if (node instanceof ModelNode) {
            ModelNode modelNode = (ModelNode) node;
            modelNode.model.dispose(gl);
        }
    }
}