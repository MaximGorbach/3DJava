package renderer;

import linAlg.Matrix;
import linAlg.Vector;
import linAlg.Plane;


public class Point {
    public Vector pos;
    
    public Point(Vector p){
        this.pos = p;
    }

    public Point(float x, float y, float z){
        this.pos = new Vector(x,y,z);
    }

    public Point(int x, int y, int z){
        this.pos = new Vector(x,y,z);
    }

    public Point copy(){
        return new Point(pos.x,pos.y,pos.z);
    }

    public boolean inBound(Plane bound){
        float d = pos.sub(bound.p1).dot(bound.norm);
        return(d >= 0);
    }

    //calculates the screen position of a given point on a camera
    public Point toProjection(Camera cam, Point textureCoord){
        //calculate fov and depth multipliers
        Float f = 1.0f/(float) Math.tan(cam.fov/2);
        Float q = cam.zFar/(cam.zFar-cam.zNear);
        Matrix pM = new Matrix(new Float[][]{{this.pos.x,this.pos.y,this.pos.z,1.0f}});
        //calculate screen position using projection matrix
        Matrix projM = new Matrix(new Float[][]{
            {f, 0.0f, 0.0f, 0.0f},
            {0.0f, f/cam.ar, 0.0f, 0.0f},
            {0.0f, 0.0f, q, 1.0f},
            {0.0f, 0.0f, -cam.zNear*q, 0.0f}
        });
        pM = pM.mul(projM);
        //divide coords by z coordinate
        if(pM.data[0][3] != 0){
            for(int i = 0; i < 3; i++){
                pM.data[0][i] /= pM.data[0][3];
            }
            textureCoord.pos.mulBy(1/pM.data[0][3]);
            textureCoord.pos.z = 1.0f/pM.data[0][3];
        }
        return new Point(new Vector(pM.data[0][0], pM.data[0][1]));
    }

    public Point toView(Camera cam){
        Matrix pM = new Matrix(new Float[][]{{this.pos.x,this.pos.y,this.pos.z,1.0f}});
        Vector x = cam.right;
        Vector z = cam.forward;
        Vector y = cam.up;
        //rotate and translate the point based on the cameras position
        Matrix viewM = new Matrix(new Float[][]{
            {x.x ,y.x, z.x, 0.0f},
            {x.y, y.y, z.y, 0.0f},
            {x.z, y.z, z.z, 0.0f},
            {-cam.pos.dot(x),-cam.pos.dot(y),-cam.pos.dot(z),1.0f}
        });
        pM = pM.mul(viewM);
        return new Point(new Vector(pM.data[0][0], pM.data[0][1],pM.data[0][2]));
    }

    //rotates the point in radians about an axis
    public void rotate(double radians, Vector axis){
        Float cos = (float) Math.cos(radians);
        Float sin = (float) Math.sin(radians);
        Float x = axis.x;
        Float y = axis.y;
        Float z = axis.z;
        //generic rotation matrix
        Matrix rM = new Matrix(new Float[][]{
            {cos + x*x*(1-cos), x*y*(1-cos)-z*sin, x*z*(1-cos) + y*sin},
            {y*x*(1-cos) + z*sin, cos + y*y*(1-cos), y*z*(1-cos) - x*sin},
            {z*x*(1-cos) - y*sin, y*z*(1-cos) + x*sin, cos + z*z*(1-cos)}
        });
        Matrix pM = new Matrix(new Float[][]{{this.pos.x},{this.pos.y},{this.pos.z}});
        pM = rM.mul(pM);
        this.pos = new Vector(pM.data[0][0], pM.data[1][0], pM.data[2][0]);
    }

    public String toString(){
        return this.pos.toString();
    }
}

