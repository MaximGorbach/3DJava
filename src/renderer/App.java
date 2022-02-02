package renderer;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import linAlg.*;

class Surface extends JPanel implements ActionListener {
    Timer timer;
    Camera cam;
    Object[] objs;

    public Surface(){
        initSurface();
    }

    private void initSurface(){
        addKeyListener(new TAdapter());
        setBackground(Color.white);
        setFocusable(true);
        cam = new Camera(new Vector(0,0,0), new Vector(1900,1000));

        Object obj = new Object("src/Objects/SkyBox.obj", "src/Textures/sky-07.jpg");
        obj.move(new Vector(0,0,0));
        Object monkey = new Object("src/Objects/WoodMonkey.obj", "src/Textures/WoodTexture.jpg");
        objs = new Object[]{monkey, obj};

        timer = new Timer(30, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        step();
    }

    private void step(){ 
        cam.resetBuffer();
        cam.move();
        repaint();
    }

    private void doDrawing(Graphics g) {
        cam.display(g, objs);
        cam.drawAxes(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            cam.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            cam.keyPressed(e);
        }
    }
}

class BasicEx extends JFrame {

    public BasicEx() {
        initUI();
    }

    private void initUI() {

        Surface surface = new Surface();
        add(surface);

        setTitle("3D Engine");
        setSize(1900, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                BasicEx ex = new BasicEx();
                ex.setVisible(true);
            }
        });
    }
}