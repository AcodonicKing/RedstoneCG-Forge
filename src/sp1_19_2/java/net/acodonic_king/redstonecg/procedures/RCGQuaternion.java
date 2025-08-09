package net.acodonic_king.redstonecg.procedures;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

public class RCGQuaternion {
    public Quaternion quaternion;
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

}
