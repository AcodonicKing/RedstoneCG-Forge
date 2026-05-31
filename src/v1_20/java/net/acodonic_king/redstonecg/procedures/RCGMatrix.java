package net.acodonic_king.redstonecg.procedures;

import org.joml.Matrix4f;

public class RCGMatrix {
    public static class M4F extends Matrix4f{
        public M4F(){
            super();
        }

        public M4F(Matrix4f pose) {
            super(pose);
        }

        public Matrix4f getMatrix(){
            return this;
        }

        @Override
        public M4F identity(){
            super.identity();
            return this;
        }
        @Override
        public M4F translate(float x, float y, float z){
            super.translate(x, y, z);
            return this;
        }
        @Override
        public M4F scale(float x, float y, float z){
            super.scale(x, y, z);
            return this;
        }
        @Override
        public M4F rotateX(float ang){
            super.rotateX(ang);
            return this;
        }
        @Override
        public M4F rotateY(float ang){
            super.rotateY(ang);
            return this;
        }
        @Override
        public M4F rotateZ(float ang){
            super.rotateZ(ang);
            return this;
        }

        public M4F mul(M4F right){
            super.mul(right);
            return this;
        }

        public String toString(){
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("Matrix4f:\n");
            stringbuilder.append(this.m00());
            stringbuilder.append(" ");
            stringbuilder.append(this.m01());
            stringbuilder.append(" ");
            stringbuilder.append(this.m02());
            stringbuilder.append(" ");
            stringbuilder.append(this.m03());
            stringbuilder.append("\n");
            stringbuilder.append(this.m10());
            stringbuilder.append(" ");
            stringbuilder.append(this.m11());
            stringbuilder.append(" ");
            stringbuilder.append(this.m12());
            stringbuilder.append(" ");
            stringbuilder.append(this.m13());
            stringbuilder.append("\n");
            stringbuilder.append(this.m20());
            stringbuilder.append(" ");
            stringbuilder.append(this.m21());
            stringbuilder.append(" ");
            stringbuilder.append(this.m22());
            stringbuilder.append(" ");
            stringbuilder.append(this.m23());
            stringbuilder.append("\n");
            stringbuilder.append(this.m30());
            stringbuilder.append(" ");
            stringbuilder.append(this.m31());
            stringbuilder.append(" ");
            stringbuilder.append(this.m32());
            stringbuilder.append(" ");
            stringbuilder.append(this.m33());
            stringbuilder.append("\n");
            return stringbuilder.toString();
        }
    }
}
