package renderer;

import java.awt.Graphics;
import java.util.ArrayList;

import linAlg.Vector;
import linAlg.Plane;
import linAlg.Line;
import linAlg.VectorFloatPair;

public class Triangle {
    public Point[] ps;
    public Point[] ts;
    public Vector normal;

    public Triangle(Point[] ps, Point[] ts){
        if(ps.length == 3){
            this.ps = ps;
            this.ts = ts;
            //updateNormal();
        }
    }


    //calculate the normal of the triangle
    public void updateNormal(){
        Vector v1 = ps[0].pos.sub(ps[1].pos);
        Vector v2 = ps[0].pos.sub(ps[2].pos);
        this.normal = v1.cross(v2).unit();
    }


    public Vector getCenter(){
        Vector center = new Vector(0,0,0);
        for(Point p : ps){
            center = center.add(p.pos);
        }
        return center.mul(1/3);
    }


    //check if the triangle is facing away from the camera
    public boolean isVis(Camera cam){
        Vector toTriangle = ps[0].pos.sub(cam.pos);
        return toTriangle.dot(this.normal) <= 0;
    }


    //display the triangle on screen
    public void draw(Graphics g, Camera cam, Object parent){
        //backface culling
        if(!isVis(cam)){
            return;
        } 
        //move objects into view
        Triangle viewTriangle = new Triangle(new Point[]{ps[0].toView(cam), ps[1].toView(cam), ps[2].toView(cam)}, this.ts);
        //clip against near plane and far plane
        Triangle[] tris = viewTriangle.clip(cam.nearBound);

        //project clipped triangles
        for(Triangle t : tris){
            Point[] newTs = new Point[]{t.ts[0].copy(),t.ts[1].copy(),t.ts[2].copy()};
            Triangle projTriangle = new Triangle(new Point[]{t.ps[0].toProjection(cam, newTs[0]),t.ps[1].toProjection(cam, newTs[1]),t.ps[2].toProjection(cam, newTs[2])}, newTs);
            
            //clip against screen boundaries
            Queue<Triangle> clipQ = new Queue<>();
            Queue<Triangle> tempQ;
            clipQ.enqueue(projTriangle);
            for(Plane bound: cam.screenBounds){
                tempQ = new Queue<>();
                while(!clipQ.isEmpty()){
                    Triangle[] trisToClip = clipQ.dequeue().clip(bound);
                    for(Triangle tri : trisToClip){ tempQ.enqueue(tri);}
                }
                clipQ = tempQ;
            }

            //draw each of the triangles
            while(!clipQ.isEmpty()){
                Triangle triToDraw = clipQ.dequeue();
                triToDraw.normal = this.normal;
                //triToDraw.textureTriangle(g, cam, parent);
                TriRasterizer.rasterize(triToDraw, cam, parent);
            }
        }
    }


    //clips the triangle against a given boundary and returns the list of triangles to draw
    public Triangle[] clip(Plane bound){
        ArrayList<Integer> inPoints = new ArrayList<>();

        //classify each point
        for(int i = 0; i < ps.length; i++){
            Point p = ps[i];
            if(p.inBound(bound)){
                inPoints.add(i);
            }
        }
        int inCount = inPoints.size();
        //clip triangle based on classification
        if(inCount == 0){
            return new Triangle[]{}; 
        }
        else if(inCount == 3){
            return new Triangle[]{this};
        }
        else if(inCount == 1){
            //make one new triangle using intersect points
            int inIndex = inPoints.get(0);
            int outIndex = inIndex + 1;
            if(outIndex > 2){ outIndex -= 3;}
            Line l1 = new Line(ps[inIndex].pos, ps[outIndex].pos);
            VectorFloatPair pair1 = l1.intersectDetailed(bound);
            Vector intersect1 = pair1.vector;
            Float t1 = pair1.num;
            Point texA = new Point(ts[outIndex].pos.add((ts[inIndex].pos.sub(ts[outIndex].pos)).mul(1-t1)));

            outIndex++;
            if(outIndex > 2){ outIndex -= 3;}
            Line l2 = new Line(ps[inIndex].pos, ps[outIndex].pos);
            VectorFloatPair pair2 = l2.intersectDetailed(bound);
            Vector intersect2 = pair2.vector;
            Float t2 = pair2.num;
            Point texB = new Point(ts[outIndex].pos.add((ts[inIndex].pos.sub(ts[outIndex].pos)).mul(1-t2)));

            Triangle res =  new Triangle(new Point[]{
                ps[inIndex].copy(),
                new Point(intersect1.copy()),
                new Point(intersect2.copy()),
            }, new Point[]{
                ts[inIndex].copy(),
                texA,
                texB
            });
    
            res.normal = this.normal;
            return new Triangle[]{res};
        }
        else if(inCount == 2){
            int inIndex1 = inPoints.get(0);
            int inIndex2 = inPoints.get(1);
            if(inIndex2 - inIndex1 > 1){
                inIndex1 = inPoints.get(1);
                inIndex2 = inPoints.get(0);
            }
            int outIndex = 3 - inIndex1 - inIndex2;
            
            //make 2 new triangles using intersect points
            Line l1 = new Line(ps[inIndex1].pos, ps[outIndex].pos);
            VectorFloatPair pair1 = l1.intersectDetailed(bound);
            Vector intersect1 = pair1.vector;
            Float t1 = pair1.num;
            Point texA = new Point(ts[outIndex].pos.add((ts[inIndex1].pos.sub(ts[outIndex].pos)).mul(1-t1)));

            Line l2 = new Line(ps[inIndex2].pos, ps[outIndex].pos);
            VectorFloatPair pair2 = l2.intersectDetailed(bound);
            Vector intersect2 = pair2.vector;
            Float t2 = pair2.num;
            Point texB = new Point(ts[outIndex].pos.add((ts[inIndex2].pos.sub(ts[outIndex].pos)).mul(1-t2)));

            Triangle res1 =  new Triangle(new Point[]{
                ps[inIndex1].copy(),
                ps[inIndex2].copy(),
                new Point(intersect1.copy())
            }, new Point[]{
                ts[inIndex1].copy(),
                ts[inIndex2].copy(),
                texA.copy()
            });
            Triangle res2 =  new Triangle(new Point[]{
                ps[inIndex2].copy(),
                new Point(intersect2.copy()),
                new Point(intersect1.copy())
            }, new Point[]{
                ts[inIndex2].copy(),
                texB.copy(),
                texA.copy()
            });
            res1.normal = this.normal;
            res2.normal = this.normal;
            return new Triangle[]{res1, res2};
        }
        return new Triangle[]{this};
    }

    public String toString(){
        String res = "Triangle at: ";
        for(Point p : ps){
            res += p.toString();
        }
        return res;
    }
}
