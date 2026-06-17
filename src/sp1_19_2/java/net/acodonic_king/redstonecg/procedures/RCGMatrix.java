package net.acodonic_king.redstonecg.procedures;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.acodonic_king.redstonecg.RedstonecgMod;

import java.nio.FloatBuffer;

//If you are asking why are you having performance issues,
// it's because 1.19.2 and earlier lacks JOML,
// mojang.math.Matrix4f is the most limited thing I have seen,
// and I had to make this.
public class RCGMatrix {
    public static float[] ANGLES = new float[]{
            (float) (Math.PI * 0.0),
            (float) (Math.PI * 0.5),
            (float) (Math.PI * 1.0),
            (float) (Math.PI * 1.5),
    };
    public static class M3F{
        public float m00, m01, m02;
        public float m10, m11, m12;
        public float m20, m21, m22;
        public M3F(){
            identity();
        }
        public M3F(Matrix3f mat){
            set(mat);
        }
        public M3F identity(){
            m00 = 1.0f;
            m01 = 0.0f;
            m02 = 0.0f;
            m10 = 0.0f;
            m11 = 1.0f;
            m12 = 0.0f;
            m20 = 0.0f;
            m21 = 0.0f;
            m22 = 1.0f;
            return this;
        }
        public M3F set(Matrix3f pose){
            FloatBuffer buf = FloatBuffer.allocate(9);
            pose.store(buf);
            m00 = buf.get(0);
            m10 = buf.get(1);
            m20 = buf.get(2);
            m01 = buf.get(3);
            m11 = buf.get(4);
            m21 = buf.get(5);
            m02 = buf.get(6);
            m12 = buf.get(7);
            m22 = buf.get(8);
            return this;
        }
        public Matrix3f getMatrix(){
            Matrix3f mat = new Matrix3f();
            mat.load(getBuffer(FloatBuffer.allocate(9)));
            return mat;
        }

        public FloatBuffer getBuffer(FloatBuffer arr){
            arr.put(0, m00);
            arr.put(1, m10);
            arr.put(2, m20);
            arr.put(3, m01);
            arr.put(4, m11);
            arr.put(5, m21);
            arr.put(6, m02);
            arr.put(7, m12);
            arr.put(8, m22);
            return arr;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("Matrix3f:\n");
            stringbuilder.append(this.m00);
            stringbuilder.append(" ");
            stringbuilder.append(this.m01);
            stringbuilder.append(" ");
            stringbuilder.append(this.m02);
            stringbuilder.append("\n");
            stringbuilder.append(this.m10);
            stringbuilder.append(" ");
            stringbuilder.append(this.m11);
            stringbuilder.append(" ");
            stringbuilder.append(this.m12);
            stringbuilder.append("\n");
            stringbuilder.append(this.m20);
            stringbuilder.append(" ");
            stringbuilder.append(this.m21);
            stringbuilder.append(" ");
            stringbuilder.append(this.m22);
            stringbuilder.append("\n");
            return stringbuilder.toString();
        }
    }
    public static class M4F{
        public float m00, m01, m02, m03;
        public float m10, m11, m12, m13;
        public float m20, m21, m22, m23;
        public float m30, m31, m32, m33;
        public M4F(){
            identity();
        }
        public M4F(Matrix4f mat){
            set(mat);
        }
        public M4F identity(){
            m00 = 1.0f;
            m01 = 0.0f;
            m02 = 0.0f;
            m03 = 0.0f;
            m10 = 0.0f;
            m11 = 1.0f;
            m12 = 0.0f;
            m13 = 0.0f;
            m20 = 0.0f;
            m21 = 0.0f;
            m22 = 1.0f;
            m23 = 0.0f;
            m30 = 0.0f;
            m31 = 0.0f;
            m32 = 0.0f;
            m33 = 1.0f;
            return this;
        }

        public M4F set(Matrix4f pose){
            FloatBuffer buf = FloatBuffer.allocate(16);
            pose.store(buf);
            m00 = buf.get(0);
            m10 = buf.get(1);
            m20 = buf.get(2);
            m30 = buf.get(3);
            m01 = buf.get(4);
            m11 = buf.get(5);
            m21 = buf.get(6);
            m31 = buf.get(7);
            m02 = buf.get(8);
            m12 = buf.get(9);
            m22 = buf.get(10);
            m32 = buf.get(11);
            m03 = buf.get(12);
            m13 = buf.get(13);
            m23 = buf.get(14);
            m33 = buf.get(15);
            return this;
        }

        public M4F set(M4F pose){
            this.m00 = pose.m00;
            this.m10 = pose.m10;
            this.m20 = pose.m20;
            this.m30 = pose.m30;
            this.m01 = pose.m01;
            this.m11 = pose.m11;
            this.m21 = pose.m21;
            this.m31 = pose.m31;
            this.m02 = pose.m02;
            this.m12 = pose.m12;
            this.m22 = pose.m22;
            this.m32 = pose.m32;
            this.m03 = pose.m03;
            this.m13 = pose.m13;
            this.m23 = pose.m23;
            this.m33 = pose.m33;
            return this;
        }

        public M4F translate(float x, float y, float z){
            m30 += m00 * x + m10 * y + m20 * z;
            m31 += m01 * x + m11 * y + m21 * z;
            m32 += m02 * x + m12 * y + m22 * z;
            m33 += m03 * x + m13 * y + m23 * z;
            return this;
        }
        public M4F scale(float x, float y, float z){
            m00 *= x;
            m01 *= x;
            m02 *= x;
            m03 *= x;
            m10 *= y;
            m11 *= y;
            m12 *= y;
            m13 *= y;
            m20 *= z;
            m21 *= z;
            m22 *= z;
            m23 *= z;
            return this;
        }
        public M4F rotateX(float ang){
            float sin = (float) Math.sin(ang), cos = (float) Math.cos(ang);
            float lm10 = m10, lm11 = m11, lm12 = m12, lm13 = m13, lm20 = m20, lm21 = m21, lm22 = m22, lm23 = m23;
            m20 = lm20 * cos - lm10 * sin;
            m21 = lm21 * cos - lm11 * sin;
            m22 = lm22 * cos - lm12 * sin;
            m23 = lm23 * cos - lm13 * sin;
            m10 = lm10 * cos + lm20 * sin;
            m11 = lm11 * cos + lm21 * sin;
            m12 = lm12 * cos + lm22 * sin;
            m13 = lm13 * cos + lm23 * sin;
            return this;
        }
        public M4F rotateY(float ang){
            float sin = (float) Math.sin(ang), cos = (float) Math.cos(ang);
            float lm00 = m00, lm01 = m01, lm02 = m02, lm03 = m03, lm20 = m20, lm21 = m21, lm22 = m22, lm23 = m23;
            m00 = lm00 * cos - lm20 * sin;
            m01 = lm01 * cos - lm21 * sin;
            m02 = lm02 * cos - lm22 * sin;
            m03 = lm03 * cos - lm23 * sin;
            m20 = lm20 * cos + lm00 * sin;
            m21 = lm21 * cos + lm01 * sin;
            m22 = lm22 * cos + lm02 * sin;
            m23 = lm23 * cos + lm03 * sin;
            return this;
        }
        public M4F rotateZ(float ang){
            float sin = (float) Math.sin(ang), cos = (float) Math.cos(ang);
            float lm00 = m00, lm01 = m01, lm02 = m02, lm03 = m03, lm10 = m10, lm11 = m11, lm12 = m12, lm13 = m13;
            m10 = lm10 * cos - lm00 * sin;
            m11 = lm11 * cos - lm01 * sin;
            m12 = lm12 * cos - lm02 * sin;
            m13 = lm13 * cos - lm03 * sin;
            m00 = lm00 * cos + lm10 * sin;
            m01 = lm01 * cos + lm11 * sin;
            m02 = lm02 * cos + lm12 * sin;
            m03 = lm03 * cos + lm13 * sin;
            return this;
        }
        public M4F mul(M4F right){ // one mistake, and I made a mistake
            float fm00 = this.m00 * right.m00 + this.m10 * right.m01 + this.m20 * right.m02 + this.m30 * right.m03;
            float fm01 = this.m01 * right.m00 + this.m11 * right.m01 + this.m21 * right.m02 + this.m31 * right.m03;
            float fm02 = this.m02 * right.m00 + this.m12 * right.m01 + this.m22 * right.m02 + this.m32 * right.m03;
            float fm03 = this.m03 * right.m00 + this.m13 * right.m01 + this.m23 * right.m02 + this.m33 * right.m03;

            float fm10 = this.m00 * right.m10 + this.m10 * right.m11 + this.m20 * right.m12 + this.m30 * right.m13;
            float fm11 = this.m01 * right.m10 + this.m11 * right.m11 + this.m21 * right.m12 + this.m31 * right.m13;
            float fm12 = this.m02 * right.m10 + this.m12 * right.m11 + this.m22 * right.m12 + this.m32 * right.m13;
            float fm13 = this.m03 * right.m10 + this.m13 * right.m11 + this.m23 * right.m12 + this.m33 * right.m13;

            float fm20 = this.m00 * right.m20 + this.m10 * right.m21 + this.m20 * right.m22 + this.m30 * right.m23;
            float fm21 = this.m01 * right.m20 + this.m11 * right.m21 + this.m21 * right.m22 + this.m31 * right.m23;
            float fm22 = this.m02 * right.m20 + this.m12 * right.m21 + this.m22 * right.m22 + this.m32 * right.m23;
            float fm23 = this.m03 * right.m20 + this.m13 * right.m21 + this.m23 * right.m22 + this.m33 * right.m23;

            float fm30 = this.m00 * right.m30 + this.m10 * right.m31 + this.m20 * right.m32 + this.m30 * right.m33;
            float fm31 = this.m01 * right.m30 + this.m11 * right.m31 + this.m21 * right.m32 + this.m31 * right.m33;
            float fm32 = this.m02 * right.m30 + this.m12 * right.m31 + this.m22 * right.m32 + this.m32 * right.m33;
            float fm33 = this.m03 * right.m30 + this.m13 * right.m31 + this.m23 * right.m32 + this.m33 * right.m33;

            this.m00 = fm00;
            this.m01 = fm01;
            this.m02 = fm02;
            this.m03 = fm03;

            this.m10 = fm10;
            this.m11 = fm11;
            this.m12 = fm12;
            this.m13 = fm13;

            this.m20 = fm20;
            this.m21 = fm21;
            this.m22 = fm22;
            this.m23 = fm23;

            this.m30 = fm30;
            this.m31 = fm31;
            this.m32 = fm32;
            this.m33 = fm33;

            return this;
        }

        public Matrix4f getMatrix(){
            return new Matrix4f(getArray(new float[16]));
        }

        public float[] getArray(float[] arr){
            arr[0] = m00;
            arr[1] = m10;
            arr[2] = m20;
            arr[3] = m30;

            arr[4] = m01;
            arr[5] = m11;
            arr[6] = m21;
            arr[7] = m31;

            arr[8] = m02;
            arr[9] = m12;
            arr[10] = m22;
            arr[11] = m32;

            arr[12] = m03;
            arr[13] = m13;
            arr[14] = m23;
            arr[15] = m33;

            return arr;
        }

        public String toString() {
            StringBuilder stringbuilder = new StringBuilder();
            stringbuilder.append("Matrix4f:\n");
            stringbuilder.append(this.m00);
            stringbuilder.append(" ");
            stringbuilder.append(this.m01);
            stringbuilder.append(" ");
            stringbuilder.append(this.m02);
            stringbuilder.append(" ");
            stringbuilder.append(this.m03);
            stringbuilder.append("\n");
            stringbuilder.append(this.m10);
            stringbuilder.append(" ");
            stringbuilder.append(this.m11);
            stringbuilder.append(" ");
            stringbuilder.append(this.m12);
            stringbuilder.append(" ");
            stringbuilder.append(this.m13);
            stringbuilder.append("\n");
            stringbuilder.append(this.m20);
            stringbuilder.append(" ");
            stringbuilder.append(this.m21);
            stringbuilder.append(" ");
            stringbuilder.append(this.m22);
            stringbuilder.append(" ");
            stringbuilder.append(this.m23);
            stringbuilder.append("\n");
            stringbuilder.append(this.m30);
            stringbuilder.append(" ");
            stringbuilder.append(this.m31);
            stringbuilder.append(" ");
            stringbuilder.append(this.m32);
            stringbuilder.append(" ");
            stringbuilder.append(this.m33);
            stringbuilder.append("\n");
            return stringbuilder.toString();
        }
    }

    /*public static Matrix4f COMMON_MATRIX = new Matrix4f();
    public static FloatBuffer COMMON_4x4 = BufferUtils.createFloatBuffer(16);
    public static Matrix4f convMatrix(org.joml.Matrix4f mat){
        mat.get(COMMON_4x4);
        COMMON_MATRIX.load(COMMON_4x4);
        return COMMON_MATRIX;
    }*/
}
