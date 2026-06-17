package net.acodonic_king.redstonecg.procedures;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class VoxelShapeBuilder {
    public List<BoxOperation> OPERATIONS = new ArrayList<>();

    public VoxelShapeBuilder(){}

    public VoxelShape build(){
        if(OPERATIONS.isEmpty())
            return null;
        VoxelShape shape = OPERATIONS.get(0).box();
        for (int i = 1; i < OPERATIONS.size(); i++) {
            BoxOperation operation = OPERATIONS.get(i);
            shape = Shapes.join(shape, operation.box(), operation.OPERATION);
        }
        return shape;
    }

    public VoxelShapeBuilder clear(){
        OPERATIONS.clear();
        return this;
    }

    public boolean isEmpty(){
        return OPERATIONS.isEmpty();
    }

    public int length(){
        return OPERATIONS.size();
    }

    public VoxelShapeBuilder add(BoxOperation operation){
        OPERATIONS.add(operation);
        return this;
    }

    public VoxelShapeBuilder add(VoxelShapeBuilder shapes){
        OPERATIONS.addAll(shapes.OPERATIONS);
        return this;
    }

    public VoxelShapeBuilder first(double xs, double ys, double zs, double xe, double ye, double ze){
        return OR(xs, ys, zs, xe, ye, ze);
    }

    public VoxelShapeBuilder first(BoxOperation operation){
        return OR(operation);
    }

    public VoxelShapeBuilder OR(double xs, double ys, double zs, double xe, double ye, double ze){
        OPERATIONS.add(new BoxOperation(BooleanOp.OR, xs, ys, zs, xe, ye, ze));
        return this;
    }

    public VoxelShapeBuilder OR(BoxOperation operation){
        OPERATIONS.add(operation.operation(BooleanOp.OR));
        return this;
    }

    public VoxelShapeBuilder AND(double xs, double ys, double zs, double xe, double ye, double ze){
        OPERATIONS.add(new BoxOperation(BooleanOp.AND, xs, ys, zs, xe, ye, ze));
        return this;
    }

    public VoxelShapeBuilder AND(BoxOperation operation){
        OPERATIONS.add(operation.operation(BooleanOp.AND));
        return this;
    }

    public VoxelShapeBuilder XOR(double xs, double ys, double zs, double xe, double ye, double ze){
        OPERATIONS.add(new BoxOperation(BooleanOp.NOT_SAME, xs, ys, zs, xe, ye, ze));
        return this;
    }

    public VoxelShapeBuilder XOR(BoxOperation operation){
        OPERATIONS.add(operation.operation(BooleanOp.NOT_SAME));
        return this;
    }

    public VoxelShapeBuilder translate(double x, double y, double z){
        for (BoxOperation operation: OPERATIONS)
            operation.translate(x, y, z);
        return this;
    }

    public VoxelShapeBuilder scale(double x, double y, double z){
        for (BoxOperation operation: OPERATIONS)
            operation.scale(x, y, z);
        return this;
    }

    public VoxelShapeBuilder scale(double x, double y, double z, double xc, double yc, double zc){
        for (BoxOperation operation: OPERATIONS)
            operation.scale(x, y, z, xc, yc, zc);
        return this;
    }

    public VoxelShapeBuilder rotateX(RightAngleRotation rotation){
        for (BoxOperation operation: OPERATIONS)
            operation.rotateX(rotation);
        return this;
    }

    public VoxelShapeBuilder rotateY(RightAngleRotation rotation){
        for (BoxOperation operation: OPERATIONS)
            operation.rotateY(rotation);
        return this;
    }

    public VoxelShapeBuilder rotateZ(RightAngleRotation rotation){
        for (BoxOperation operation: OPERATIONS)
            operation.rotateZ(rotation);
        return this;
    }

    public VoxelShapeBuilder rotateX(RightAngleRotation rotation, double xc, double yc, double zc){
        for (BoxOperation operation: OPERATIONS)
            operation.rotateX(rotation, xc, yc, zc);
        return this;
    }

    public VoxelShapeBuilder rotateY(RightAngleRotation rotation, double xc, double yc, double zc){
        for (BoxOperation operation: OPERATIONS)
            operation.rotateY(rotation, xc, yc, zc);
        return this;
    }

    public VoxelShapeBuilder rotateZ(RightAngleRotation rotation, double xc, double yc, double zc){
        for (BoxOperation operation: OPERATIONS)
            operation.rotateZ(rotation, xc, yc, zc);
        return this;
    }

    public static class BoxOperation{
        public BooleanOp OPERATION = BooleanOp.OR;

        public double XS, YS, ZS, XE, YE, ZE;

        public BoxOperation(){}
        public BoxOperation(double xs, double ys, double zs, double xe, double ye, double ze){
            XS = xs;
            YS = ys;
            ZS = zs;
            XE = xe;
            YE = ye;
            ZE = ze;
        }
        public BoxOperation(BooleanOp operation, double xs, double ys, double zs, double xe, double ye, double ze){
            OPERATION = operation;
            XS = xs;
            YS = ys;
            ZS = zs;
            XE = xe;
            YE = ye;
            ZE = ze;
        }

        @Override
        public String toString() {
            return "BoxOperation{" +
                    "OPERATION=" + OPERATION +
                    ", XS=" + XS +
                    ", YS=" + YS +
                    ", ZS=" + ZS +
                    ", XE=" + XE +
                    ", YE=" + YE +
                    ", ZE=" + ZE +
                    '}';
        }

        public VoxelShape box(){
            return Block.box(
                    Math.min(XS, XE),
                    Math.min(YS, YE),
                    Math.min(ZS, ZE),
                    Math.max(XS, XE),
                    Math.max(YS, YE),
                    Math.max(ZS, ZE)
            );
        }

        public BoxOperation operation(BooleanOp operation){
            OPERATION = operation;
            return this;
        }
        public BoxOperation start(double xs, double ys, double zs){
            XS = xs;
            YS = ys;
            ZS = zs;
            return this;
        }
        public BoxOperation end(double xe, double ye, double ze){
            XE = xe;
            YE = ye;
            ZE = ze;
            return this;
        }
        public BoxOperation size(double xs, double ys, double zs){
            XE = XS + xs;
            YE = YS + ys;
            ZE = ZS + zs;
            return this;
        }
        public BoxOperation centerSize(double xc, double yc, double zc, double xs, double ys, double zs){
            xs /= 2;
            ys /= 2;
            zs /= 2;
            XS = xc - xs;
            YS = yc - ys;
            ZS = zc - zs;
            XE = xc + xs;
            YE = yc + ys;
            ZE = zc + zs;
            return this;
        }

        public BoxOperation translate(double x, double y, double z){
            XS += x;
            YS += y;
            ZS += z;
            XE += x;
            YE += y;
            ZE += z;
            return this;
        }

        public BoxOperation scale(double x, double y, double z){
            XS *= x;
            YS *= y;
            ZS *= z;
            XE *= x;
            YE *= y;
            ZE *= z;
            return this;
        }

        public BoxOperation scale(double x, double y, double z, double xc, double yc, double zc){
            return translate(-xc, -yc, -zc).scale(x, y, z).translate(xc, yc, zc);
        }

        public BoxOperation rotateX(RightAngleRotation rotation, double xc, double yc, double zc){
            return translate(-xc, -yc, -zc).rotateX(rotation).translate(xc, yc, zc);
        }

        public BoxOperation rotateY(RightAngleRotation rotation, double xc, double yc, double zc){
            return translate(-xc, -yc, -zc).rotateY(rotation).translate(xc, yc, zc);
        }

        public BoxOperation rotateZ(RightAngleRotation rotation, double xc, double yc, double zc){
            return translate(-xc, -yc, -zc).rotateZ(rotation).translate(xc, yc, zc);
        }

        public BoxOperation rotateX(RightAngleRotation rotation){
            double xs = XS;
            double ys = YS;
            double zs = ZS;
            double xe = XE;
            double ye = YE;
            double ze = ZE;
            XS = rotation.rotateXAxisX(xs, ys, zs);
            YS = rotation.rotateYAxisX(xs, ys, zs);
            ZS = rotation.rotateZAxisX(xs, ys, zs);
            XE = rotation.rotateXAxisX(xe, ye, ze);
            YE = rotation.rotateYAxisX(xe, ye, ze);
            ZE = rotation.rotateZAxisX(xe, ye, ze);
            return this;
        }

        public BoxOperation rotateY(RightAngleRotation rotation){
            double xs = XS;
            double ys = YS;
            double zs = ZS;
            double xe = XE;
            double ye = YE;
            double ze = ZE;
            XS = rotation.rotateXAxisY(xs, ys, zs);
            YS = rotation.rotateYAxisY(xs, ys, zs);
            ZS = rotation.rotateZAxisY(xs, ys, zs);
            XE = rotation.rotateXAxisY(xe, ye, ze);
            YE = rotation.rotateYAxisY(xe, ye, ze);
            ZE = rotation.rotateZAxisY(xe, ye, ze);
            return this;
        }

        public BoxOperation rotateZ(RightAngleRotation rotation){
            double xs = XS;
            double ys = YS;
            double zs = ZS;
            double xe = XE;
            double ye = YE;
            double ze = ZE;
            XS = rotation.rotateXAxisZ(xs, ys, zs);
            YS = rotation.rotateYAxisZ(xs, ys, zs);
            ZS = rotation.rotateZAxisZ(xs, ys, zs);
            XE = rotation.rotateXAxisZ(xe, ye, ze);
            YE = rotation.rotateYAxisZ(xe, ye, ze);
            ZE = rotation.rotateZAxisZ(xe, ye, ze);
            return this;
        }
    }
}
