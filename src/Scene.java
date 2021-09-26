import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Scene class contains standard function calls and variables that must be present in every scene
 * MyKeyboardInput and MyMouseInput is taken from the code provided at lectures
 *
 * It's heavy influenced by the code provided in the practicals
 */
public class Scene extends JFrame {

    private GLCanvas canvas;
    private MyGLEventListener glEventListener;
    private final FPSAnimator animator;
    private Camera camera;

    public Scene(String title, GLEventListener glEventListener, Camera camera) {
        super(title);
        this.glEventListener = (MyGLEventListener) glEventListener;
        this.camera = camera;
        GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
        canvas = new GLCanvas(glcapabilities);
        canvas.addGLEventListener(glEventListener);
        canvas.addMouseMotionListener(new MyMouseInput(camera));
        canvas.addKeyListener(new MyKeyboardInput(camera));
        getContentPane().add(canvas, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                animator.stop();
                remove(canvas);
                dispose();
                System.exit(0);
            }
        });
        animator = new FPSAnimator(canvas, 60);
        animator.start();
    }

    // Get current time
    public static double getSeconds() {
        return (double) System.currentTimeMillis() / 1000L;
    }

    public GLCanvas getCanvas() {
        return canvas;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public MyGLEventListener getGlEventListener() {
        return glEventListener;
    }


    /**
     * This class was provided by the lecturer
     * I didn't make any changes
     */
    class MyKeyboardInput extends KeyAdapter {
        private Camera camera;

        public MyKeyboardInput(Camera camera) {
            this.camera = camera;
        }

        public void keyPressed(KeyEvent e) {
            Camera.Movement m = Camera.Movement.NO_MOVEMENT;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    m = Camera.Movement.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    m = Camera.Movement.RIGHT;
                    break;
                case KeyEvent.VK_UP:
                    m = Camera.Movement.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    m = Camera.Movement.DOWN;
                    break;
                case KeyEvent.VK_A:
                    m = Camera.Movement.FORWARD;
                    break;
                case KeyEvent.VK_Z:
                    m = Camera.Movement.BACK;
                    break;
            }
            camera.keyboardInput(m);
        }
    }

    /**
     * This class was provided by the lecturer
     * I didn't make any changes
     */
    class MyMouseInput extends MouseMotionAdapter {
        private Point lastpoint;
        private Camera camera;

        public MyMouseInput(Camera camera) {
            this.camera = camera;
        }

        /**
         * mouse is used to control camera position
         *
         * @param e instance of MouseEvent
         */
        public void mouseDragged(MouseEvent e) {
            Point ms = e.getPoint();
            float sensitivity = 0.001f;
            float dx = (float) (ms.x - lastpoint.x) * sensitivity;
            float dy = (float) (ms.y - lastpoint.y) * sensitivity;
            //System.out.println("dy,dy: "+dx+","+dy);
            if (e.getModifiers() == MouseEvent.BUTTON1_MASK)
                camera.updateYawPitch(dx, -dy);
            lastpoint = ms;
        }

        /**
         * mouse is used to control camera position
         *
         * @param e instance of MouseEvent
         */
        public void mouseMoved(MouseEvent e) {
            lastpoint = e.getPoint();
        }
    }
}
