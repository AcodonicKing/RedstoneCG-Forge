package net.acodonic_king.redstonecg.procedures;

import org.joml.Quaternionf;

public class RCGQuaternion {
    public Quaternionf quaternion;
    public RCGQuaternion(Quaternionf q){
        this.quaternion = q;
    }
    public static class Vector3F{
        public static RCGQuaternion rotateXP(float angle){
            return new RCGQuaternion(new Quaternionf().rotateX(angle));
        }
        public static RCGQuaternion rotateYP(float angle){
            return new RCGQuaternion(new Quaternionf().rotateY(angle));
        }
        public static RCGQuaternion rotateZP(float angle){
            return new RCGQuaternion(new Quaternionf().rotateZ(angle));
        }
    }

}
