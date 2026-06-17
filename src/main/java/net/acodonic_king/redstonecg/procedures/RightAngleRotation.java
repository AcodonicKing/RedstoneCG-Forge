package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.Direction;

public class RightAngleRotation {
    public static RightAngleRotation CW0 = new RightAngleRotation(Direction.NORTH, 0f);
    public static RightAngleRotation CW1 = new RightAngleRotation(Direction.EAST, (float)(Math.PI * 0.5)){
        @Override
        public Direction rotateX(Direction direction){return direction.getClockWise(Direction.Axis.X);}
        @Override
        public Direction rotateY(Direction direction){return direction.getClockWise(Direction.Axis.Y);}
        @Override
        public Direction rotateZ(Direction direction){return direction.getClockWise(Direction.Axis.Z);}
        @Override
        public double rotateYAxisX(double x, double y, double z){return -z;}
        @Override
        public double rotateZAxisX(double x, double y, double z){return y;}
        @Override
        public double rotateXAxisY(double x, double y, double z){return z;}
        @Override
        public double rotateZAxisY(double x, double y, double z){return -x;}
        @Override
        public double rotateXAxisZ(double x, double y, double z){return -y;}
        @Override
        public double rotateYAxisZ(double x, double y, double z){return x;}
    };
    public static RightAngleRotation CW2 = new RightAngleRotation(Direction.SOUTH, (float)(Math.PI * 1.0)){
        @Override
        public Direction rotateX(Direction direction){
            return switch (direction){
                case EAST, WEST -> direction;
                default -> direction.getOpposite();
            };
        }
        @Override
        public Direction rotateY(Direction direction){
            return switch (direction){
                case UP, DOWN -> direction;
                default -> direction.getOpposite();
            };
        }
        @Override
        public Direction rotateZ(Direction direction){
            return switch (direction){
                case NORTH, SOUTH -> direction;
                default -> direction.getOpposite();
            };
        }
        @Override
        public double rotateYAxisX(double x, double y, double z){return -y;}
        @Override
        public double rotateZAxisX(double x, double y, double z){return -z;}
        @Override
        public double rotateXAxisY(double x, double y, double z){return -x;}
        @Override
        public double rotateZAxisY(double x, double y, double z){return -z;}
        @Override
        public double rotateXAxisZ(double x, double y, double z){return -x;}
        @Override
        public double rotateYAxisZ(double x, double y, double z){return -y;}
    };
    public static RightAngleRotation CW3 = new RightAngleRotation(Direction.WEST, (float)(Math.PI * 1.5)){
        @Override
        public Direction rotateX(Direction direction){return direction.getCounterClockWise(Direction.Axis.X);}
        @Override
        public Direction rotateY(Direction direction){return direction.getCounterClockWise(Direction.Axis.Y);}
        @Override
        public Direction rotateZ(Direction direction){return direction.getCounterClockWise(Direction.Axis.Z);}
        @Override
        public double rotateYAxisX(double x, double y, double z){return z;}
        @Override
        public double rotateZAxisX(double x, double y, double z){return -y;}
        @Override
        public double rotateXAxisY(double x, double y, double z){return -z;}
        @Override
        public double rotateZAxisY(double x, double y, double z){return x;}
        @Override
        public double rotateXAxisZ(double x, double y, double z){return y;}
        @Override
        public double rotateYAxisZ(double x, double y, double z){return -x;}
    };
    public static RightAngleRotation CCW0 = CW0;
    public static RightAngleRotation CCW1 = CW3;
    public static RightAngleRotation CCW2 = CW2;
    public static RightAngleRotation CCW3 = CW1;

    public static RightAngleRotation[] CW_MAP = new RightAngleRotation[]{CW0, CW1, CW2, CW3};
    public static RightAngleRotation[] CCW_MAP = new RightAngleRotation[]{CCW0, CCW1, CCW2, CCW3};

    public final Direction DIRECTION;
    public final float RADIANS;
    public RightAngleRotation(Direction direction, float radians){
        DIRECTION = direction;
        RADIANS = radians;
    }
    public Direction getDirection(){
        return DIRECTION;
    }
    public float getRadians(){
        return RADIANS;
    }
    public Direction rotateX(Direction direction){
        return direction;
    }
    public Direction rotateY(Direction direction){
        return direction;
    }
    public Direction rotateZ(Direction direction){
        return direction;
    }
    public double rotateXAxisX(double x, double y, double z){return x;}
    public double rotateYAxisX(double x, double y, double z){return y;}
    public double rotateZAxisX(double x, double y, double z){return z;}
    public double rotateXAxisY(double x, double y, double z){return x;}
    public double rotateYAxisY(double x, double y, double z){return y;}
    public double rotateZAxisY(double x, double y, double z){return z;}
    public double rotateXAxisZ(double x, double y, double z){return x;}
    public double rotateYAxisZ(double x, double y, double z){return y;}
    public double rotateZAxisZ(double x, double y, double z){return z;}

}
