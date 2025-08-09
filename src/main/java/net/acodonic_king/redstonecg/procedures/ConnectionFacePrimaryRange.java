package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public class ConnectionFacePrimaryRange {
    public Direction[] FACES = {Direction.UP,Direction.EAST};
    public int[] CHANNELS = {4,4};
    public ConnectionFacePrimaryRange(Direction secondary){
        if(secondary == Direction.UP){
            FACES[0] = Direction.SOUTH;
        } else if (secondary == Direction.DOWN){
            FACES[0] = Direction.NORTH;
        } else {
            FACES[1] = secondary.getClockWise(Direction.Axis.Y);
        }
        CHANNELS = switch(secondary){
            case UP -> new int[]{0,0};
            case NORTH -> new int[]{0,1};
            case EAST -> new int[]{1,1};
            case SOUTH -> new int[]{2,3};
            case WEST -> new int[]{3,3};
            case DOWN -> new int[]{2,2};
        };
    }
    public boolean canConnect(ConnectionFace connectionFace){
        if(connectionFace.CHANNEL == 5){return false;}
        for(int i = 0; i < 2; i++){
            Direction direction = this.FACES[i];
            int channel = this.CHANNELS[i];
            if(connectionFace.FACE == direction || connectionFace.FACE == direction.getOpposite()){
                if(connectionFace.CHANNEL == 4){return true;}
                if(connectionFace.CHANNEL == channel){return true;}
            }
        }
        return false;
    }
    public boolean canConnectAvoid(ConnectionFace connectionFace, int allowFace){
        if(connectionFace.CHANNEL == 5){return false;}
        for(int i = 0; i < 2; i++){
            Direction direction = this.FACES[i];
            int channel = this.CHANNELS[i];
            int allowing = allowFace & 5;
            if(
                    ((connectionFace.FACE == direction) && ((allowing & 4) > 0))
                    ||
                    ((connectionFace.FACE == direction.getOpposite()) && ((allowing & 1) > 0))
            ){
                if(connectionFace.CHANNEL == 4){return true;}
                if(connectionFace.CHANNEL == channel){return true;}
            }
            allowFace >>= 1;
        }
        return false;
    }
    public boolean inRange(ConnectionFace connectionFace){
        if(connectionFace.CHANNEL == 5){return false;}
        for(int i = 0; i < 2; i++){
            Direction direction = this.FACES[i];
            int channel = this.CHANNELS[i];
            if(connectionFace.FACE == direction || connectionFace.FACE == direction.getOpposite()){
                if(connectionFace.CHANNEL == 4){return true;}
                if(connectionFace.CHANNEL == channel){return true;}
            }
        }
        return false;
    }

    /**
     * Checks if connection face is within range and if corresponding bit is true
     * @param connectionFace
     * @param allowFace
     * @return
     */
    public boolean inRangeAvoid(ConnectionFace connectionFace, int allowFace){
        if(connectionFace.CHANNEL == 5){return false;}
        for(int i = 0; i < 2; i++){
            Direction direction = this.FACES[i];
            int channel = this.CHANNELS[i];
            int allowing = allowFace & 5;
            if(
                    ((connectionFace.FACE == direction) && ((allowing & 1) > 0))
                    ||
                    ((connectionFace.FACE == direction.getOpposite()) && ((allowing & 4) > 0))
            ){
                if(connectionFace.CHANNEL == 4){return true;}
                if(connectionFace.CHANNEL == channel){return true;}
            }
            allowFace >>= 1;
        }
        return false;
    }
    public List<ConnectionFace> getList(){
        List<ConnectionFace> lst = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            lst.add(new ConnectionFace(this.FACES[i],this.CHANNELS[i]));
        }
        for(int i = 0; i < 2; i++){
            lst.add(new ConnectionFace(this.FACES[i].getOpposite(),this.CHANNELS[i]));
        }
        return lst;
    }
    public List<ConnectionFace> getList(int allowFace){
        List<ConnectionFace> lst = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            if((allowFace & 1) > 0){
                lst.add(new ConnectionFace(this.FACES[i],this.CHANNELS[i]));
            }
            allowFace >>= 1;
        }
        for(int i = 0; i < 2; i++){
            if((allowFace & 1) > 0){
                lst.add(new ConnectionFace(this.FACES[i].getOpposite(),this.CHANNELS[i]));
            }
            allowFace >>= 1;
        }
        return lst;
    }
    public static int rotateFilter(int filter, Direction primary){
        filter = switch (primary){
            case NORTH -> filter;
            case EAST -> filter << 1;
            case SOUTH -> filter << 2;
            case WEST -> filter << 3;
            default -> filter;
        };
        filter |= filter >> 4;
        return filter & 15;
    }
}
