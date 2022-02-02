package renderer;

import java.awt.Graphics;
import java.io.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import linAlg.Vector;

public class Object {
    public Triangle[] tris;
    public Vector center;
    public int[] textureArray;
    public BufferedImage texture;

    public Object(Triangle[] tris, String texPath){
        this.tris = tris;
        this.center = this.getCenter();
        //get texture
        File tex = new File(texPath);
        try{
            texture = ImageIO.read(tex);
            textureArray = new int[texture.getWidth()*texture.getHeight()];
            texture.getRGB(0,0,texture.getWidth(),texture.getHeight(),textureArray,0,texture.getWidth());
        } 
        catch(Exception e){
            System.out.println("couldn't read texture file");
        }
    }

    //create an object from a .obj file
    public Object(String filePath, String texPath){
        //get texture
        File tex = new File(texPath);
        try{
            texture = ImageIO.read(tex);
            textureArray = new int[texture.getWidth()*texture.getHeight()];
            texture.getRGB(0,0,texture.getWidth(),texture.getHeight(),textureArray,0,texture.getWidth());
        } 
        catch(Exception e){
            System.out.println("couldn't read texture file");
        }

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String strLine;
        ArrayList<Point> vertices = new ArrayList<>();
        ArrayList<Triangle> faces = new ArrayList<>();
        ArrayList<Point> texVertices = new ArrayList<>();
        try {
            while ((strLine = reader.readLine()) != null) {
                String[] tokens = strLine.split(" ");
                //create new vertex
                if(tokens[0].equals("v")){
                    vertices.add(new Point(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3])));
                } 
                if(tokens[0].equals("vt")){
                    texVertices.add(new Point(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]), 1.0f));
                } 
                if(tokens[0].equals("f")){
                    String[][] faceTokens = new String[tokens.length][2];
                    for(int i = 0; i < tokens.length; i++){
                        faceTokens[i] = tokens[i].split("/");
                    }
                    Triangle newTri = new Triangle(new Point[]{vertices.get(Integer.parseInt(faceTokens[1][0])-1).copy(),vertices.get(Integer.parseInt(faceTokens[2][0])-1).copy(),vertices.get(Integer.parseInt(faceTokens[3][0])-1).copy()},
                                new Point[]{texVertices.get(Integer.parseInt(faceTokens[1][1])-1).copy(), texVertices.get(Integer.parseInt(faceTokens[2][1])-1).copy(), texVertices.get(Integer.parseInt(faceTokens[3][1])-1).copy()});
                    newTri.updateNormal();
                    faces.add(newTri);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //close the reader
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Triangle[] triList = new Triangle[faces.size()];
        for(int i = 0; i < faces.size(); i++){
            triList[i] = faces.get(i);
        }
        this.tris = triList;
        this.center = this.getCenter();
    }

    //display the object on screen
    public void draw(Graphics g, Camera cam){
        for(Triangle t : this.tris){
            t.draw(g, cam, this);
        }
    }

    //TODO rotate the object about its center
    //rotate the object about a point on the object
    public void rotate(Double angle, Vector axis){
        for(Triangle t : this.tris){
            for(Point p : t.ps){
                Point tempP = new Point(p.pos.sub(center));
                tempP.rotate(angle, axis);
                p.pos = tempP.pos.add(center);
            }
            t.updateNormal();
        }
    }

    public Vector getCenter(){
        Vector center = new Vector(0,0,0);
        int pCount = 0;
        for(Triangle t : tris){
            for(Point p : t.ps){
                center = center.add(p.pos);
                pCount++;
            }
        }
        return center.mul(1/pCount);
    }

    //translate the object
    public void move(Vector dir){
        for(Triangle t : this.tris){
            for(Point p : t.ps){
                p.pos = p.pos.add(dir);
            }
        }
    }
    
    public String toString(){
        String res = "";
        res = "Object: ";
        for(Triangle t : this.tris){
            res += t.toString() + ", ";
        }
        return res;
    }
}