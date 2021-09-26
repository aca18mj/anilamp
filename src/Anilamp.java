import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The main class of the project.
 * @author Michal Jarmocik mjarmocik1@sheffield.ac.uk
 */
public class Anilamp extends Scene implements ActionListener {

    // Window dimensions variables
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;
    private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);

    // Main method of the assignment. Setups the camera, GL and creates the scene
    public static void main(String[] args) {
        Camera camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
        Anilamp scene = new Anilamp(camera);
        scene.getContentPane().setPreferredSize(dimension);
        scene.pack();
        scene.setVisible(true);
    }

    // Creates the scene and interface
    public Anilamp(Camera camera) {
        super("Anilamp", new Anilamp_GLEventListener(camera), camera);
        //// Interface
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(this);
        fileMenu.add(quitItem);
        menuBar.add(fileMenu);

        // Buttons panel
        JPanel panel = new JPanel();
        JButton btn = new JButton("Random pose");
        btn.addActionListener(this);
        panel.add(btn);

        btn = new JButton("Reset lamp");
        btn.addActionListener(this);
        panel.add(btn);

        btn = new JButton("Lamp ON / OFF");
        btn.addActionListener(this);
        panel.add(btn);

        btn = new JButton("Start heli");
        btn.addActionListener(this);
        panel.add(btn);

        btn = new JButton("Stop heli");
        btn.addActionListener(this);
        panel.add(btn);

        btn = new JButton("Light 1 ON / OFF");
        btn.addActionListener(this);
        panel.add(btn);

        btn = new JButton("Light 2 ON / OFF");
        btn.addActionListener(this);
        panel.add(btn);

        this.add(panel, BorderLayout.SOUTH);
    }

    // Handles interface quit event or passes the event to the GLEventListener
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand().toLowerCase();

        if (actionCommand.equals("quit"))
            System.exit(0);
        else
            getGlEventListener().actionPerformed(actionCommand);
    }
}
