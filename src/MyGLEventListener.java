import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import gmaths.Mat4Transform;
import java.util.ArrayList;

/**
 * Abstract class that shapes how the GLEventListeners are used in the scene
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public abstract class MyGLEventListener implements GLEventListener {

    protected Camera camera;
    private double startTime;

    // keeping track of the models and scene graphs on the scene
    private ArrayList<Model> models;
    private ArrayList<SGNode> sceneGraphs;

    // keep track of shaders used in the scene
    private ArrayList<Shader> shaders;

    // light controller for keeping tack of the lights on the scene
    private LightsController lightsController;

    private long lastFrameTime;
    public float delta;

    /**
     * Initialises the scene
     * @param drawable
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
        gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
        gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
        models = new ArrayList<>();
        sceneGraphs = new ArrayList<>();
        lightsController = new LightsController();
        shaders = new ArrayList<>();
        initialise(gl);
        lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Disposes all models
     * @param drawable
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        for (Model m : models)
            m.dispose(gl);

        for (SGNode node : sceneGraphs)
            node.dispose(gl, null);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        render(gl);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(x, y, width, height);
        float aspect = (float) width / (float) height;
        camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
    }

    public void initialise(GL3 gl) {

    }

    public void addModel(Model model) {models.add(model);}

    public void addSceneGraph(SGNode sgNode) {sceneGraphs.add(sgNode);}

    public void addPointLight(PointLight light) {lightsController.addPointLight(light);}

    public void addDirLight(DirectionalLight light) {lightsController.addDirLight(light);}

    public void addSpotLight(SpotLight light) {lightsController.addSpotLight(light);}

    public void addShader(Shader shader) {shaders.add(shader);}

    // extra method for to make any updates before rendering the frame
    public void beforeRender() {
        // calculate delta time since last frame was rendered
        delta = (float) (System.currentTimeMillis() - lastFrameTime) / 1000f;
        lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Render method:
     *      1. Calls pre-render update function
     *      2. Updates shaders light data
     *      3. Renders all models outside of scene graphs
     *      4. Renders scene graphs
     * @param gl
     */
    public void render(GL3 gl) {
        beforeRender();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        lightsController.updateShaders(gl, shaders);
        for (Model model : models)
            model.render(gl, camera);

        for (SGNode node : sceneGraphs)
            node.draw(gl, camera);
    }

    public abstract void actionPerformed(String actionCommand);
}
