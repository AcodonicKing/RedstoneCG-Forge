package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public class ConnectionFacePrimaryRange {
    public short MASKS = (byte) 0xFF;
    public ConnectionFacePrimaryRange(Direction secondary){
        MASKS = primitive(secondary);
    }
    public byte part(int i){
        return part(MASKS, i);
    }
    public boolean canConnect(ConnectionFace connectionFace){
        if(connectionFace.connectsNone()){return false;}
        for(int i = 0; i < 2; i++){
            byte mask = part(i);
            if(connectionFace.canConnect(mask) || connectionFace.equals(mask))
                return true;
        }
        return false;
    }
    public boolean canConnectAllow(ConnectionFace connectionFace, int allowFace){
        if(connectionFace.connectsNone()){return false;}
        for(int i = 0; i < 2; i++){
            int allowing = allowFace & 5;
            byte mask = part(i);
            if(
                (connectionFace.canConnect(mask) && ((allowing & 1) > 0))
                ||
                (connectionFace.equals(mask) && ((allowing & 4) > 0))
            )
                return true;
            allowFace >>= 1;
        }
        return false;
    }
    public boolean inRange(ConnectionFace connectionFace){
        return canConnect(connectionFace);
    }

    /**
     * Checks if connection face is within range and if corresponding bit is true
     * @param connectionFace
     * @param allowFace
     * @return
     */
    public boolean inRangeAllow(ConnectionFace connectionFace, int allowFace){
        return canConnectAllow(connectionFace, (~allowFace) & 0x0F);
    }
    public List<ConnectionFace> getList(){
        List<ConnectionFace> lst = new ArrayList<>();
        for(int i = 0; i < 2; i++)
            lst.add(new ConnectionFace(part(i)));
        for(int i = 0; i < 2; i++)
            lst.add(new ConnectionFace(ConnectionFace.getConnectable(part(i))));
        return lst;
    }
    public List<ConnectionFace> getList(int allowFace){
        List<ConnectionFace> lst = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            if((allowFace & 1) > 0)
                lst.add(new ConnectionFace(part(i)));
            allowFace >>= 1;
        }
        for(int i = 0; i < 2; i++){
            if((allowFace & 1) > 0)
                lst.add(new ConnectionFace(ConnectionFace.getConnectable(part(i))));
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

    public static short primitive(Direction secondary){
        return (short) switch(secondary){
            case UP -> 0x0101 | 0x4020;
            case NORTH -> 0x0201 | 0x4010;
            case EAST -> 0x0202 | 0x2010;
            case SOUTH -> 0x0804 | 0xC010;
            case WEST -> 0x0808 | 0xA010;
            case DOWN -> 0x0404 | 0x40A0;
        };
        /*
        if (secondary == Direction.UP)
            masks |= 0x4020;
        else if (secondary == Direction.DOWN)
            masks |= 0x40A0;
        else {
            masks |= 0x0010;
            masks |= (short) (ConnectionFace.encodeFace(secondary.getClockWise(Direction.Axis.Y)) << 8);
        }
        */
    }
    public static byte part(short masks, int i){
        return (byte)((masks >> (i << 3)) & 0xFF);
    }
    public static boolean canConnect(short masks, byte mask){
        if(ConnectionFace.connectsNone(mask)){return false;}
        for(int i = 0; i < 2; i++){
            byte maskPart = part(masks, i);
            if(ConnectionFace.canConnectOrSame(mask, maskPart))
                return true;
        }
        return false;
    }
    public static boolean canConnectAllow(short masks, byte mask, int allowFace){
        if(ConnectionFace.connectsNone(mask)){return false;}
        for(int i = 0; i < 2; i++){
            int allowing = allowFace & 5;
            byte maskPart = part(masks, i);
            if(
                    (ConnectionFace.canConnect(mask, maskPart) && ((allowing & 1) > 0))
                    ||
                    (ConnectionFace.sameFaceConnect(mask, maskPart) && ((allowing & 4) > 0))
            )
                return true;
            allowFace >>= 1;
        }
        return false;
    }
    public static boolean inRange(short masks, byte mask){
        return canConnect(masks, mask);
    }
    public static boolean inRangeAllow(short masks, byte mask, int allowFace){
        return canConnectAllow(masks, mask, (~allowFace) & 0x0F);
    }
    public static byte[] getArray(short masks){
        byte[] array = new byte[4];
        for(int i = 0; i < 2; i++)
            array[i] = part(masks, i);
        for(int i = 0; i < 2; i++)
            array[i + 2] = ConnectionFace.getConnectable(part(masks, i));
        return array;
    }
    public static byte[] getArray(short masks, int allowFace){
        int l = 0;
        for(int i = 0; i < 4; i++){
            if((allowFace & 8) > 0)
                l++;
            allowFace <<= 1;
        }
        byte[] array = new byte[l];
        for(int i = 0; i < 2; i++){
            allowFace >>= 1;
            if((allowFace & 8) > 0)
                array[--l] = part(masks, i);
        }
        for(int i = 0; i < 2; i++){
            allowFace >>= 1;
            if((allowFace & 8) > 0)
                array[--l] = ConnectionFace.getConnectable(part(masks, i));
        }
        return array;
    }

}
