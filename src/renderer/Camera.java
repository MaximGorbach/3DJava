package renderer;

import linAlg.Plane;
import linAlg.Vector;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class Camera {
    Vector pos;
    Vector screenSize;
    boolean forwardMove;
    boolean rightMove;
    boolean upMove;
    boolean backMove;
    boolean leftMove;
    boolean downMove;
    boolean rotateLeft;
    boolean rotateRight;
    boolean rotateDown;
    boolean rotateUp;
    double rotateSpeed;
    float moveSpeed;
    Vector forward;
    Vector right;
    Vector up;
    Float ar;
    Double fov;
    Float zFar;
    Float zNear;
    Plane nearBound; 
    Plane[] screenBounds;
    float[][] depthBuffer;
    BufferedImage image;
    int[] pixels;
    WritableRaster raster;
    Vector lightDir;

    public Camera(Vector pos, Vector screenSize){
        this.pos = pos;
        this.moveSpeed = 0.4f;
        this.rotateSpeed = 0.05;
        this.forward = new Vector(0,0,1);
        this.right = new Vector(1,0,0);
        this.up = new Vector(0,1,0);
        this.ar = screenSize.y/screenSize.x;
        this.fov = 1.7;
        this.zFar = 1000.0f;
        this.zNear = 0.1f;
        this.screenSize = screenSize;
        this.nearBound = new Plane(new Vector(0.0f,0.0f,zNear), new Vector(0,0,1));
        this.screenBounds = new Plane[]{
            new Plane(new Vector(-0.99f,-0.99f,0.0f),new Vector(0,1,0)),
            new Plane(new Vector(-0.99f,0.0f,0.0f),new Vector(1,0,0)),
            new Plane(new Vector(0.99f,0.0f,0.0f), new Vector(-1,0,0)),
            new Plane(new Vector(0.99f,0.99f,0.0f), new Vector(0,-1,0))
        };
        this.depthBuffer = new float[Math.round(screenSize.x)][Math.round(screenSize.y)];
        resetBuffer();
        this.image = new BufferedImage(Math.round(screenSize.x),Math.round(screenSize.y),BufferedImage.TYPE_INT_RGB);
        pixels = new int[image.getWidth() * image.getHeight()];
        raster = image.getRaster();
        lightDir = new Vector(1,0,0);
    }

    //move and rotate the camera, called every frame
    public void move(){ 
        //movement
        if(forwardMove) { this.pos = this.pos.add(forward.mul(moveSpeed));}
        if(backMove) { this.pos = this.pos.add(forward.mul(-moveSpeed));}
        if(rightMove) { this.pos = this.pos.add(right.mul(moveSpeed));}
        if(leftMove) { this.pos = this.pos.add(right.mul(-moveSpeed));}
        if(upMove) { this.pos = this.pos.add(up.mul(moveSpeed));}
        if(downMove) { this.pos = this.pos.add(up.mul(-moveSpeed));}

        //rotation
        if(rotateRight){ this.rotate(rotateSpeed, new Vector(0,1,0));}
        if(rotateLeft){ this.rotate(-rotateSpeed, new Vector(0,1,0));}
        if(rotateUp){ this.rotate(-rotateSpeed, this.right);}
        if(rotateDown){ this.rotate(rotateSpeed, this.right);}
    }

    //sets the buffer back to default
    public void resetBuffer(){ 
        for(int i = 0; i < depthBuffer.length; i++){
            for(int j = 0; j < depthBuffer[0].length; j++){
                depthBuffer[i][j] = Float.MAX_VALUE;
            }
        }
    }

    //rotates the camera by a given angle about a given axis
    public void rotate(Double rads, Vector axis){
        Point tempP = new Point(forward);
        tempP.rotate(rads, axis);
        this.forward = tempP.pos;
        Point tempP2 = new Point(up);
        tempP2.rotate(rads, axis);
        this.up = tempP2.pos;
        this.right = up.cross(forward);
    }

    //draws all objects in the given list on screen
    public void display(Graphics g, Object[] objs){
        clearImage();
        for(Object obj : objs){
            obj.draw(g, this);
        }
        resetBuffer();
        raster.setDataElements(0, 0,image.getWidth(), image.getHeight(), pixels);
        g.drawImage(image,0,0,null);
    }

    public void clearImage(){
        int white = Color.WHITE.getRGB();
        for(int i = 0; i < pixels.length; i++){
            pixels[i] = white;
        }
        
    }

    //draws the current heading of the camera on screen for debugging
    public void drawAxes(Graphics g){
        //draw forward axis
        g.setColor(Color.RED);
        g.drawLine(50,50,(int) (50 + 20*forward.x),(int) (50 - 20*forward.y));
        //draw up axis
        g.setColor(Color.BLUE);
        g.drawLine(50,50,(int) (50 + 20*up.x),(int) (50 - 20*up.y));
        //draw right axis
        g.setColor(Color.GREEN);
        g.drawLine(50,50,(int) (50 + 20*right.x),(int) (50 - 20*right.y));
    }

    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();
        //movement
        if (key == KeyEvent.VK_W) {
            forwardMove = true;
        }

        if (key == KeyEvent.VK_S) {
            backMove = true;
        }

        if (key == KeyEvent.VK_D) {
            rightMove = true;
        }

        if (key == KeyEvent.VK_A) {
            leftMove = true;
        }
        if (key == KeyEvent.VK_SPACE) {
            upMove = true;
        }

        if (key == KeyEvent.VK_CONTROL) {
            downMove = true;
        }

        //rotation
        if (key == KeyEvent.VK_UP) {
            rotateUp = true;
        }

        if (key == KeyEvent.VK_DOWN) {
            rotateDown = true;
        }

        if (key == KeyEvent.VK_LEFT) {
            rotateLeft = true;
        }

        if (key == KeyEvent.VK_RIGHT) {
            rotateRight = true;
        }
    }

    public void keyReleased(KeyEvent e) {
            
        int key = e.getKeyCode();
        //movement
        if (key == KeyEvent.VK_W) {
            forwardMove = false;
        }

        if (key == KeyEvent.VK_S) {
            backMove = false;
        }

        if (key == KeyEvent.VK_D) {
            rightMove = false;
        }

        if (key == KeyEvent.VK_A) {
            leftMove = false;
        }
        if (key == KeyEvent.VK_SPACE) {
            upMove = false;
        }

        if (key == KeyEvent.VK_CONTROL) {
            downMove = false;
        }

        //rotation
        if (key == KeyEvent.VK_UP) {
            rotateUp = false;
        }

        if (key == KeyEvent.VK_DOWN) {
            rotateDown = false;
        }

        if (key == KeyEvent.VK_LEFT) {
            rotateLeft = false;
        }

        if (key == KeyEvent.VK_RIGHT) {
            rotateRight = false;
        }
    }
}