package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.Direction;

public class ConnectionFace {
    public Direction FACE;
    public int CHANNEL;
    public ConnectionFace(Direction primary, Direction secondary){
        if(primary == Direction.UP || primary == Direction.DOWN){CHANNEL = 5;}
        if(primary == Direction.UP){FACE = secondary.getOpposite();}
        else if(secondary == Direction.DOWN){
            FACE = primary;
            CHANNEL = 2;
        }
        else if(secondary == Direction.UP){
            CHANNEL = 0;
            if(primary == Direction.EAST || primary == Direction.WEST){FACE = primary;}
            else{FACE = primary.getOpposite();}
        }
        else{
            CHANNEL = switch (secondary) {
                case EAST -> 1;
                case WEST -> 3;
                default -> switch (primary){
                    case NORTH, SOUTH -> switch (secondary){
                        case NORTH -> 0;
                        case SOUTH -> 2;
                        default -> 5;
                    };
                    default -> switch (secondary){
                        case NORTH -> 1;
                        case SOUTH -> 3;
                        default -> 5;
                    };
                };
            };
            primary = primary.getCounterClockWise(Direction.Axis.X);
            FACE = BlockFrameTransformUtils.rotateDirectionClockwiseY(primary, secondary);
        }
    }
    public ConnectionFace(Direction face){
        FACE = face;
        CHANNEL = 4;
    }
    public ConnectionFace(Direction face, int channel){
        FACE = face;
        CHANNEL = channel;
    }
    public boolean canConnect(ConnectionFace connectionFace){
        if(connectionFace.FACE.getOpposite() == this.FACE){
            if(connectionFace.CHANNEL == 5 || this.CHANNEL == 5){return false;}
            if(connectionFace.CHANNEL == 4 || this.CHANNEL == 4){return true;}
            return connectionFace.CHANNEL == this.CHANNEL;
        }
        return false;
    }
    public ConnectionFace copy(){
        return new ConnectionFace(this.FACE, this.CHANNEL);
    }
    public ConnectionFace getConnectable(){
        return new ConnectionFace(this.FACE.getOpposite(), this.CHANNEL);
    }
    @Override
    public String toString() {
        return "ConnectionFace("+FACE+","+CHANNEL+")";
    }
}
