package renderer;

import linAlg.Vector;
import java.awt.Color;

public class RasterThread implements Runnable{
    int[] boundingBox;
    Camera cam;
    Triangle t;
    Object parent;
    float[] xSteps;
    float[] ySteps;
    float area;
    float illum;
    
    public RasterThread(int[] boundingBox, Camera cam, Triangle t, Object parent, float[] xSteps, float[] ySteps, float illum, float area){
        this.boundingBox = boundingBox;
        this.t = t;
        this.parent = parent;
        this.cam = cam;
        this.xSteps = xSteps;
        this.ySteps = ySteps;
        this.area = area;
        this.illum = illum;
    }

    public void run(){
        int min_x = boundingBox[0];
        int min_y = boundingBox[1];
        int max_x = boundingBox[2];
        int max_y = boundingBox[3];
        Vector p = new Vector(min_x + 0.5f,min_y + 0.5f);
        float b2 = edgeFunction(p,t.ps[0].pos,t.ps[1].pos) * area;
        float b0 = edgeFunction(p,t.ps[1].pos,t.ps[2].pos) * area;
        float b1 = edgeFunction(p,t.ps[2].pos,t.ps[0].pos) * area;
        float currb0 = b0;
        float currb1 = b1;
        float currb2 = b2;
        boolean inTri;
        for(int i = min_x; i < max_x; i++){
            for(int j = min_y; j < max_y; j++){
                //check if every pixel in the bounding box is in the triangle
                //the center of each pixel is at (0.5,0.5) + (integer coord)
                inTri = true;
                inTri &= currb0 >= 0.0f;
                inTri &= currb1 >= 0.0f;
                inTri &= currb2 >= 0.0f;
                if(inTri){   
                    float u = currb0 * t.ts[0].pos.x + currb1 * t.ts[1].pos.x + currb2 * t.ts[2].pos.x;  
                    float v = currb0 * t.ts[0].pos.y + currb1 * t.ts[1].pos.y + currb2 * t.ts[2].pos.y;    
                    float w = currb0 * t.ts[0].pos.z + currb1 * t.ts[1].pos.z + currb2 * t.ts[2].pos.z;
                    w = 1/w;
                    if(cam.depthBuffer[i][j] > w){
                        cam.depthBuffer[i][j] = w;
                        u *= w*(parent.texture.getWidth()-1);
                        v *= w*(parent.texture.getHeight()-1); 
                        int col = parent.textureArray[Math.round(u) + Math.round(v) * parent.texture.getWidth()];
                        int r = (col >> 16) & 0xFF;
                        int g = (col >> 8) & 0xFF;
                        int b =  col & 0xFF;
                        col = new Color(Math.round(r*illum),Math.round(g*illum),Math.round(b*illum)).getRGB();
                        cam.pixels[i + j * Math.round(cam.screenSize.x)] = col;
                    }
                }
                currb0 += ySteps[0];
                currb1 += ySteps[1];
                currb2 += ySteps[2];
            } 
            b0 += xSteps[0];
            b1 += xSteps[1];
            b2 += xSteps[2];  
            currb0 = b0;
            currb1 = b1;
            currb2 = b2;
        }
    }

    private float edgeFunction(Vector p, Vector v0, Vector v1){
        return (p.x - v0.x) * (v1.y - v0.y) - (p.y - v0.y) * (v1.x - v0.x);
    }
}
