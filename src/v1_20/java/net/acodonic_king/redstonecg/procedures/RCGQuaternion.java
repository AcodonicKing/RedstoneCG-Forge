package net.acodonic_king.redstonecg.procedures;

import org.joml.Quaternionf;

public class RCGQuaternion {
    public Quaternionf quaternion;
    public RCGQuaternion(){
        this.quaternion = new Quaternionf();
    }
    public RCGQuaternion(Quaternionf q){
        this.quaternion = q;
    }
    public RCGQuaternion zRotationTo(float x, float y, float z){
        this.quaternion.rotationTo(0, 0, 1, x, y, z);
        return this;
    }
    @Override
    public RCGQuaternion clone(){
        RCGQuaternion inst = new RCGQuaternion();
        inst.quaternion.set(this.quaternion);
        return inst;
    }
    public RCGQuaternion set(RCGQuaternion quat){
        quaternion.set(quat.quaternion);
        return this;
    }
    public RCGQuaternion rotateX(float angle){
        quaternion.rotateX(angle);
        return this;
    }
    public RCGQuaternion rotateY(float angle){
        quaternion.rotateY(angle);
        return this;
    }
    public RCGQuaternion rotateZ(float angle){
        quaternion.rotateZ(angle);
        return this;
    }
    public RCGQuaternion identity(){
        quaternion.identity();
        return this;
    }
    public Quaternionf getQuaternion(){
        return quaternion;
    }
    /*public RCGQuaternion rotationTo(float fromDirX, float fromDirY, float fromDirZ, float toDirX, float toDirY, float toDirZ){
        float fn = Math.invsqrt(Math.fma(fromDirX, fromDirX, Math.fma(fromDirY, fromDirY, fromDirZ * fromDirZ)));
        float tn = Math.invsqrt(Math.fma(toDirX, toDirX, Math.fma(toDirY, toDirY, toDirZ * toDirZ)));
        float fx = fromDirX * fn, fy = fromDirY * fn, fz = fromDirZ * fn;
        float tx = toDirX * tn, ty = toDirY * tn, tz = toDirZ * tn;
        float dot = fx * tx + fy * ty + fz * tz;
        float x, y, z, w;
        if (dot < -1.0f + 1E-6f) {
            x = fy;
            y = -fx;
            z = 0.0f;
            w = 0.0f;
            if (x * x + y * y == 0.0f) {
                x = 0.0f;
                y = fz;
                z = -fy;
                w = 0.0f;
            }
            this.quaternion.x = x;
            this.quaternion.y = y;
            this.quaternion.z = z;
            this.quaternion.w = 0;
        } else {
            float sd2 = Math.sqrt((1.0f + dot) * 2.0f);
            float isd2 = 1.0f / sd2;
            float cx = fy * tz - fz * ty;
            float cy = fz * tx - fx * tz;
            float cz = fx * ty - fy * tx;
            x = cx * isd2;
            y = cy * isd2;
            z = cz * isd2;
            w = sd2 * 0.5f;
            float n2 = Math.invsqrt(Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w))));
            this.quaternion.x = x * n2;
            this.quaternion.y = y * n2;
            this.quaternion.z = z * n2;
            this.quaternion.w = w * n2;
        }
        return this;
    }*/
}
