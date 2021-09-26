import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import gmaths.Vec3;
import java.util.HashMap;

/**
 * The GLEventListener for the scene
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class Anilamp_GLEventListener extends MyGLEventListener {

    private Lamp lamp;
    private Helicopter helicopter;

    private DirectionalLight directionalLight;
    private boolean isDirLightOn = true;

    private PointLight pointLight;
    private boolean isPointLightOn = true;

    // constructor
    public Anilamp_GLEventListener(Camera camera) {
        this.camera = camera;
        this.camera.setPosition(new Vec3(8f, 12f, 12f));
        this.camera.setTarget(new Vec3(-8f, 2f, -8f));
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
    }

    /**
     * Initialises the scene by loading textures, shaders and all models
     * @param gl
     */
    @Override
    public void initialise(GL3 gl) {
        super.initialise(gl);

        // Loads all the textures and stores their ids in a hash map
        HashMap<String, int[]> textures = loadTextures(gl);

        // Create shaders in keep track of them in order to update light data
        Shader shader_no_specular = new Shader(gl, "vs.shader", "fs_no_specular.shader");
        Shader shader_specular = new Shader(gl, "vs.shader", "fs_specular.shader");
        addShader(shader_no_specular);
        addShader(shader_specular);

        // Create scene
        createRoom(gl, shader_no_specular, textures);
        createTable(gl, shader_no_specular, shader_specular, textures);
        createNoticeBoard(gl, shader_no_specular, textures);
    }

    // Called before the frame is rendered (to animate position of objects etc)
    @Override
    public void beforeRender() {
        super.beforeRender();
        if (lamp.isAnimating())
            lamp.animatePose();

        if (helicopter.isAnimating())
            helicopter.animate(delta);

        lamp.updateLight();
    }

    /**
     * Loads all textures in the scene and maps them in a hash map
     * @param gl
     * @return hash map String - texture ID
     */
    private HashMap<String, int[]> loadTextures(GL3 gl) {
        HashMap<String, int[]> textures = new HashMap<>();

        textures.put("wood", TextureLibrary.loadTexture(gl, "wood.jpg"));
        textures.put("floor", TextureLibrary.loadTexture(gl, "floor.jpg"));
        textures.put("wall", TextureLibrary.loadTexture(gl, "wall.jpg"));
        textures.put("landscape", TextureLibrary.loadTexture(gl, "landscape.jpg"));
        textures.put("table_drawing", TextureLibrary.loadTexture(gl, "table_drawing.jpg"));
        textures.put("board", TextureLibrary.loadTexture(gl, "board.jpg"));
        textures.put("drawing_1", TextureLibrary.loadTexture(gl, "drawing_1.jpg"));
        textures.put("drawing_2", TextureLibrary.loadTexture(gl, "drawing_2.jpg"));
        textures.put("aluminium", TextureLibrary.loadTexture(gl, "aluminium.jpg"));
        textures.put("aluminium_specular", TextureLibrary.loadTexture(gl, "aluminium_specular.jpg"));
        textures.put("iron", TextureLibrary.loadTexture(gl, "iron.jpg"));
        textures.put("iron_specular", TextureLibrary.loadTexture(gl, "iron_specular.jpg"));
        textures.put("zebra", TextureLibrary.loadTexture(gl, "zebra.jpg"));
        textures.put("heli_base", TextureLibrary.loadTexture(gl, "heli_base.jpg"));
        textures.put("heli_base_specular", TextureLibrary.loadTexture(gl, "heli_base_specular.jpg"));
        textures.put("heli_sphere", TextureLibrary.loadTexture(gl, "heli_sphere.jpg"));
        textures.put("heli_sphere_specular", TextureLibrary.loadTexture(gl, "heli_sphere_specular.jpg"));
        textures.put("plastic", TextureLibrary.loadTexture(gl, "plastic.jpg"));

        return textures;
    }

    /**
     * Creates the room objects and lights
     * @param gl
     * @param shader the shader with no specular map
     * @param textures  HashMap with all textures references
     */
    private void createRoom(GL3 gl, Shader shader, HashMap<String, int[]> textures) {
        Room room = new Room(gl, shader, textures);
        // Get created models and keep track of them
        for (Model model : room.getModels())
            addModel(model);

        // Keep track of the lights
        addPointLight(room.getPointLight());
        pointLight = room.getPointLight();
        addDirLight(room.getDirLight());
        directionalLight = room.getDirLight();
    }


    /**
     * Creates the table scene graph along with the lamp and helicopter
     * @param gl
     * @param shader the shader with no specular map
     * @param shader_specular the shader with specular map
     * @param textures  HashMap with all textures references
     */
    private void createTable(GL3 gl, Shader shader, Shader shader_specular, HashMap<String, int[]> textures) {
        Table table = new Table(gl, shader, shader_specular, textures);
        lamp = table.getLamp();
        helicopter = table.getHelicopter();
        addSpotLight(lamp.getSpotLight());

        addSceneGraph(table.getTableRoot());
    }

    /**
     * Creates the notice board scene graph
     * @param gl
     * @param shader the shader with no specular map
     * @param textures  HashMap with all textures references
     */
    private void createNoticeBoard(GL3 gl, Shader shader, HashMap<String, int[]> textures) {
        NoticeBoard noticeBoard = new NoticeBoard(gl, shader, textures);
        addSceneGraph(noticeBoard.getBoardRoot());
    }


    /**
     * Switches given light on or off
     * @param light the light to be switched on / off
     * @param isOn true if the light is already on
     */
    private void lightSwitch(Light light, boolean isOn) {
        Material material = new Material();
        if (isOn) {
            material.setAmbient(0, 0, 0);
            material.setDiffuse(0, 0, 0);
            material.setSpecular(0, 0, 0);
        } else {
            material.setAmbient(0.1f, 0.1f, 0.1f);
            material.setDiffuse(0.8f, 0.8f, 0.8f);
            material.setSpecular(0.8f, 0.8f, 0.8f);
        }

        light.setMaterial(material);
    }

    /**
     * Handles the interface events
     * @param actionCommand the name of the command
     */
    @Override
    public void actionPerformed(String actionCommand) {
        switch (actionCommand) {
            case "random pose":
                lamp.setNewPose(Lamp.PoseType.RANDOM);
                break;
            case "reset lamp":
                lamp.setNewPose(Lamp.PoseType.DEFAULT);
                break;
            case "lamp on / off":
                lamp.switchLight();
                break;
            case "start heli":
                helicopter.startFlight();
                break;
            case "stop heli":
                helicopter.turnOff();
                break;
            case "light 1 on / off":
                lightSwitch(directionalLight, isDirLightOn);
                isDirLightOn = !isDirLightOn;
                break;
            case "light 2 on / off":
                lightSwitch(pointLight, isPointLightOn);
                isPointLightOn = !isPointLightOn;
                break;
        }
    }
}
