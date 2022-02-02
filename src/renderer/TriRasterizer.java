package renderer;

import java.awt.Color;

import linAlg.Vector;

public class TriRasterizer{
    //TODO to increase speed check the if the 4 corners of a rect are in the triangle, if so then all the points in the rect
    //are in the tri
    //rasterizes a given triangle on-screen
    public static void rasterize(Triangle t, Camera cam, Object parent){
        for(int i = 0; i < 3; i++){
            t.ps[i].pos.x = (t.ps[i].pos.x + 1) * cam.screenSize.x/2;
            t.ps[i].pos.y = (1-(t.ps[i].pos.y)) * cam.screenSize.y/2; 
        }
        int[] boundingBox = boundingBox(t);
        int min_x = boundingBox[0];
        int min_y = boundingBox[1];
        int max_x = boundingBox[2];
        int max_y = boundingBox[3];
        float area = edgeFunction(t.ps[0].pos, t.ps[1].pos,t.ps[2].pos);
        area = 1/area;
        Vector p = new Vector(min_x + 0.5f,min_y + 0.5f);
        float illum = (t.normal.dot(cam.lightDir) + 1) /2.0f;
        float b2 = edgeFunction(p,t.ps[0].pos,t.ps[1].pos) * area;
        float b0 = edgeFunction(p,t.ps[1].pos,t.ps[2].pos) * area;
        float b1 = edgeFunction(p,t.ps[2].pos,t.ps[0].pos) * area;

        //the increase in barycentric coordinates is constant for an increase in x or y
        //so the steps can be calculated beforehand
        float b2_xStep = (t.ps[1].pos.y - t.ps[0].pos.y) * area;
        float b2_yStep = -(t.ps[1].pos.x - t.ps[0].pos.x) * area;
        float b0_xStep = (t.ps[2].pos.y - t.ps[1].pos.y) * area;
        float b0_yStep = -(t.ps[2].pos.x - t.ps[1].pos.x) * area;
        float b1_xStep = (t.ps[0].pos.y - t.ps[2].pos.y) * area;
        float b1_yStep = -(t.ps[0].pos.x - t.ps[2].pos.x) * area;

        // float[] xSteps = new float[]{b0_xStep,b1_xStep,b2_xStep};
        // float[] ySteps = new float[]{b0_yStep,b1_yStep,b2_yStep};

        // int[] topRboundingBox = new int[4];
        // int[] topLboundingBox = new int[4];
        // int[] botRboundingBox = new int[4];
        // int[] botLboundingBox = new int[4];
        // topLboundingBox[0] = min_x;
        // topLboundingBox[2] = (max_x-min_x)/2;
        // topLboundingBox[1] = min_y;
        // topLboundingBox[3] = (max_y - min_y)/2;

        // topRboundingBox[0] = (max_x-min_x)/2;
        // topRboundingBox[2] = max_x;
        // topRboundingBox[1] = min_y;
        // topRboundingBox[3] = (max_y - min_y)/2;

        // botLboundingBox[0] = min_x;
        // botLboundingBox[2] = (max_x-min_x)/2;
        // botLboundingBox[1] = (max_y - min_y)/2;
        // botLboundingBox[3] = max_y;

        // botRboundingBox[0] = (max_x-min_x)/2;
        // botRboundingBox[2] = max_x;
        // botRboundingBox[1] = (max_y - min_y)/2;
        // botRboundingBox[3] = max_y;

        // RasterThread topR = new RasterThread(topRboundingBox, cam, t, parent, xSteps, ySteps, illum, area);
        // RasterThread topL = new RasterThread(topLboundingBox, cam, t, parent, xSteps, ySteps, illum, area);
        // RasterThread botR = new RasterThread(botRboundingBox, cam, t, parent, xSteps, ySteps, illum, area);
        // RasterThread botL = new RasterThread(botLboundingBox, cam, t, parent, xSteps, ySteps, illum, area);
        // Thread topRThread = new Thread(topR);
        // Thread topLThread = new Thread(topL);
        // Thread botRThread = new Thread(botR);
        // Thread botLThread = new Thread(botL);
        // topRThread.start();
        // topLThread.start();
        // botRThread.start();
        // botLThread.start();
        // //wait for threads to finish
        // while(topRThread.isAlive() || topLThread.isAlive() || botRThread.isAlive() || botLThread.isAlive()){
        //     //do nothing
        // }
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
                currb0 += b0_yStep;
                currb1 += b1_yStep;
                currb2 += b2_yStep;
            } 
            b0 += b0_xStep;
            b1 += b1_xStep;
            b2 += b2_xStep;  
            currb0 = b0;
            currb1 = b1;
            currb2 = b2;
        }
    }

    //returns an array in the form [min_x,min_y,max_x,max_y]
    private static int[] boundingBox(Triangle t){
        int[] res = new int[4];
        float[] xs = new float[]{t.ps[0].pos.x, t.ps[1].pos.x, t.ps[2].pos.x};
        float[] ys = new float[]{t.ps[0].pos.y, t.ps[1].pos.y, t.ps[2].pos.y};
        res[0] = (int) Math.floor(minVal(xs));
        res[1] = (int) Math.floor(minVal(ys));
        res[2] = (int) Math.ceil(maxVal(xs));
        res[3] = (int) Math.ceil(maxVal(ys));
        return res;
    }

    //returns the result of the edge function for given point and two triangle vertices
    private static float edgeFunction(Vector p, Vector v0, Vector v1){
        return (p.x - v0.x) * (v1.y - v0.y) - (p.y - v0.y) * (v1.x - v0.x);
    }

    //returns max value of float array
    private static float maxVal(float[] arr){
        float maxVal = -Float.MAX_VALUE;
        for(float val : arr){
            if(val > maxVal){
                maxVal = val;
            }
        }
        return maxVal;
    }

    //returns min value of float array
    private static float minVal(float[] arr){
        float minVal = Float.MAX_VALUE;
        for(float val : arr){
            if(val < minVal){
                minVal = val;
            }
        }
        return minVal;
    }
}