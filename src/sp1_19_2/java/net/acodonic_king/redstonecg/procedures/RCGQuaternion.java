package net.acodonic_king.redstonecg.procedures;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;

public class RCGQuaternion {
    public Quaternion quaternion;
    public RCGQuaternion(){this.quaternion = Quaternion.ONE.copy();}
    public RCGQuaternion(Quaternion q){
        this.quaternion = q;
    }
    public static class Vector3F{
        public static RCGQuaternion rotateXP(float angle){
            return new RCGQuaternion(Vector3f.XP.rotation(angle));
        }
        public static RCGQuaternion rotateYP(float angle){
            return new RCGQuaternion(Vector3f.YP.rotation(angle));
        }
        public static RCGQuaternion rotateZP(float angle){
            return new RCGQuaternion(Vector3f.ZP.rotation(angle));
        }
    }
    public RCGQuaternion zRotationTo(float x, float y, float z){
        return rotationTo(0, 0, 1, x, y, z);
    }
    @Override
    public RCGQuaternion clone(){
        RCGQuaternion inst = new RCGQuaternion();
        inst.quaternion.set(this.quaternion.i(), this.quaternion.j(), this.quaternion.k(), this.quaternion.r());
        return inst;
    }
    public RCGQuaternion set(RCGQuaternion quat){
        quaternion.set(this.quaternion.i(), this.quaternion.j(), this.quaternion.k(), this.quaternion.r());
        return this;
    }
    public RCGQuaternion rotateX(float angle){
        quaternion.mul(Vector3f.XP.rotation(angle));
        return this;
    }
    public RCGQuaternion rotateY(float angle){
        quaternion.mul(Vector3f.YP.rotation(angle));
        return this;
    }
    public RCGQuaternion rotateZ(float angle){
        quaternion.mul(Vector3f.ZP.rotation(angle));
        return this;
    }
    public RCGQuaternion identity(){
        quaternion.set(0, 0, 0, 1);
        return this;
    }
    public Quaternion getQuaternion(){
        return quaternion;
    }
    public RCGQuaternion rotationTo(float fromDirX, float fromDirY, float fromDirZ, float toDirX, float toDirY, float toDirZ){
        float fn = (float) (1.0 / Math.sqrt(Math.fma(fromDirX, fromDirX, Math.fma(fromDirY, fromDirY, fromDirZ * fromDirZ))));
        float tn = (float) (1.0 / Math.sqrt(Math.fma(toDirX, toDirX, Math.fma(toDirY, toDirY, toDirZ * toDirZ))));
        float fx = fromDirX * fn, fy = fromDirY * fn, fz = fromDirZ * fn;
        float tx = toDirX * tn, ty = toDirY * tn, tz = toDirZ * tn;
        float dot = fx * tx + fy * ty + fz * tz;
        float x, y, z, w;
        if (dot < -1.0f + 1E-6f) {
            x = fy;
            y = -fx;
            z = 0.0f;
            if (x * x + y * y == 0.0f) {
                x = 0.0f;
                y = fz;
                z = -fy;
            }
            this.quaternion.set(x, y, z, 0);
        } else {
            float sd2 = (float) Math.sqrt((1.0f + dot) * 2.0f);
            float isd2 = 1.0f / sd2;
            float cx = fy * tz - fz * ty;
            float cy = fz * tx - fx * tz;
            float cz = fx * ty - fy * tx;
            x = cx * isd2;
            y = cy * isd2;
            z = cz * isd2;
            w = sd2 * 0.5f;
            float n2 = (float) (1.0 / Math.sqrt(Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w)))));
            this.quaternion.set(x * n2, y * n2, z * n2, w * n2);
        }
        return this;
    }
}
