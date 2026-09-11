package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.Direction;

public class ConnectionFace {
    public static final int CHANNEL_A = 0;
    public static final int CHANNEL_B = 1;
    public static final int CHANNEL_C = 2;
    public static final int CHANNEL_D = 3;
    public static final int CHANNEL_ALL = 4;
    public static final int CHANNEL_NONE = 5;
    public static final byte MASK_CHANNEL_A = 1;
    public static final byte MASK_CHANNEL_B = 2;
    public static final byte MASK_CHANNEL_C = 4;
    public static final byte MASK_CHANNEL_D = 8;
    public static final byte MASK_ALL = 0x0F;
    public static final byte MASK_NONE = 0;
    public static final byte FACE_DOWN = (byte) 0x90;
    public static final byte FACE_NORTH = (byte) 0xA0;
    public static final byte FACE_EAST = (byte) 0x40;
    public static final byte FACE_SOUTH = (byte) 0x20;
    public static final byte FACE_WEST = (byte) 0xC0;
    public static final byte FACE_UP = (byte) 0x10;
    public static final byte FACE_NULL = (byte) 0x00;

    /*public static final Direction[][] FACEMAP = { // secondary primary
            {Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP},
            {Direction.NORTH, Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST, Direction.SOUTH},
            {Direction.EAST, Direction.UP, Direction.SOUTH, Direction.DOWN, Direction.NORTH, Direction.WEST},
            {Direction.SOUTH, Direction.UP, Direction.WEST, Direction.DOWN, Direction.EAST, Direction.NORTH},
            {Direction.WEST, Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH, Direction.EAST},
            {Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.DOWN},
    };*/

    public byte MASK = 0x0F;
    public ConnectionFace(){}
    public ConnectionFace(Direction primary, Direction secondary){
        MASK = primitive(primary, secondary);
    }
    public ConnectionFace(Direction face){
        MASK |= encodeFace(face);
    }
    public ConnectionFace(Direction face, int channel){
        channel(channel);
        MASK |= encodeFace(face);
    }
    public ConnectionFace(Direction face, byte channel_mask){
        MASK = (byte) (channel_mask | encodeFace(face));
    }
    public ConnectionFace(byte fullMask){
        MASK = fullMask;
    }
    public ConnectionFace combine(ConnectionFace connectionFace){
        return combine(this, connectionFace);
    }
    public ConnectionFace combine(Direction primary, Direction secondary){
        return combine(this, primary, secondary);
    }

    public byte mask(){
        return MASK;
    }
    public ConnectionFace mask(byte mask){
        MASK = mask;
        return this;
    }
    public ConnectionFace mask(int mask){
        MASK = (byte)(mask & 0xFF);
        return this;
    }
    public int channel(){
        return toChannel(MASK);
    }
    public ConnectionFace channel(int channel){
        MASK = (byte) (MASK & 0xF0 | toChannelMask(channel));
        return this;
    }
    public Direction face(){
        return decodeFace(MASK);
    }
    public ConnectionFace face(Direction face){
        MASK = (byte) (MASK & 0x0F | encodeFace(face));
        return this;
    }
    public boolean canConnect(ConnectionFace connectionFace){
        return canConnect(this.MASK, connectionFace.MASK);
    }
    public boolean canConnect(byte mask){
        return canConnect(this.MASK, mask);
    }
    public boolean equals(byte mask){
        return mask == MASK;
    }
    public boolean equals(ConnectionFace connectionFace){
        return equals(connectionFace.MASK);
    }
    public boolean connectsNone(){
        return connectsNone(MASK);
    }
    public boolean connectsAll(){
        return connectsAll(MASK);
    }
    public boolean canConnectChannels(byte mask){
        return channelsConnect(this.MASK,mask);
    }
    public boolean canConnectChannels(ConnectionFace connectionFace){
        return canConnectChannels(connectionFace.MASK);
    }
    public ConnectionFace copy(){
        return new ConnectionFace(MASK);
    }
    public ConnectionFace getConnectable(){
        return new ConnectionFace(getConnectable(MASK));
    }
    @Override
    public String toString() {
        return "ConnectionFace("+ MASK +")";
    }


    public static byte primitive(Direction primary, Direction secondary){
        if(primary == Direction.DOWN)
            return encodeFace(secondary);
        if(primary == Direction.UP)
            return encodeFace(secondary.getOpposite());
        if (secondary == Direction.DOWN)
            return (byte) (encodeFace(primary) | MASK_CHANNEL_C);
        if (secondary == Direction.UP) {
            if(primary == Direction.EAST || primary == Direction.WEST)
                return (byte)(encodeFace(primary) | MASK_CHANNEL_A);
            else
                return (byte)(encodeFace(primary.getOpposite()) | MASK_CHANNEL_A);
        }
        /*int mask = switch (secondary) {
            case EAST -> MASK_CHANNEL_B;
            case WEST -> MASK_CHANNEL_D;
            default -> switch (primary){
                case NORTH, SOUTH -> switch (secondary){
                    case NORTH -> MASK_CHANNEL_A;
                    case SOUTH -> MASK_CHANNEL_C;
                    default -> MASK_NONE;
                };
                default -> switch (secondary){
                    case NORTH -> MASK_CHANNEL_B;
                    case SOUTH -> MASK_CHANNEL_D;
                    default -> MASK_NONE;
                };
            };
        };*/
        int mask = switch (secondary) {
            case EAST -> MASK_CHANNEL_B;
            case WEST -> MASK_CHANNEL_D;
            case NORTH -> primary.getAxis() == Direction.Axis.Z ? MASK_CHANNEL_A : MASK_CHANNEL_B;
            case SOUTH -> primary.getAxis() == Direction.Axis.Z ? MASK_CHANNEL_C : MASK_CHANNEL_D;
            default -> MASK_NONE;
        };
        primary = primary.getCounterClockWise(Direction.Axis.X);
        Direction face = BlockFrameTransformUtils.rotateDirectionClockwiseY(primary, secondary);
        return (byte)(mask | encodeFace(face));
    }
    public static byte primitiveNone(Direction face){
        return (byte) (encodeFace(face) | MASK_NONE);
    }
    public static byte primitiveAll(Direction face){
        return (byte) (encodeFace(face) | MASK_ALL);
    }
    public static byte primitiveChannel(Direction face, int channel){
        return (byte) (encodeFace(face) | toChannelMask(channel));
    }
    public static byte primitiveChannelMask(Direction face, byte channelMask){
        return (byte) (encodeFace(face) | channelMask);
    }
    public static byte primitiveChannelMask(Direction primary, Direction secondary, byte channelMask){
        return (byte) (getFaceMask(primary, secondary) | channelMask);
    }
    public static byte combine(byte maskA, byte maskB){
        if(!sameFaces(maskA, maskB))
            throw new RuntimeException("Cannot combine "+maskA+" and "+maskB);
        return (byte) (maskA | (maskB & 0x0F));
    }
    public static byte combine(byte maskA, Direction primary, Direction secondary){
        if(!sameFaces(maskA, getFaceMask(primary, secondary)))
            throw new RuntimeException("Cannot combine "+maskA+" with ConnectionFace("+primary+", "+secondary+")");
        return (byte) (maskA | getChannelMask(primary, secondary));
    }
    public static byte combine(Direction primaryA, Direction secondaryA, Direction primaryB, Direction secondaryB){
        byte face = getFaceMask(primaryA, secondaryA);
        if(!sameFaces(face, getFaceMask(primaryB, secondaryB)))
            throw new RuntimeException("Cannot combine ConnectionFace("+primaryA+", "+secondaryA+") with ConnectionFace("+primaryB+", "+secondaryB+")");
        return (byte) (face | getChannelMask(primaryA, secondaryA) | getChannelMask(primaryB, secondaryB));
    }
    public static ConnectionFace combine(ConnectionFace destination, ConnectionFace connectionFace){
        destination.MASK = combine(destination.MASK, connectionFace.MASK);
        return destination;
    }
    public static ConnectionFace combine(ConnectionFace destination, Direction primary, Direction secondary){
        destination.MASK = combine(destination.MASK, primary, secondary);
        return destination;
    }
    public static byte toChannelMask(int channel){
        if(channel == CHANNEL_NONE)
            return MASK_NONE;
        if(channel == CHANNEL_ALL)
            return MASK_ALL;
        return (byte) (1 << channel);
    }
    public static int toChannel(byte mask){
        if(mask == MASK_NONE)
            return CHANNEL_NONE;
        if(mask == MASK_ALL)
            return CHANNEL_ALL;
        for(int i = 0; i < 4; i++){
            if((mask & 1) > 0)
                return i;
            mask >>= 1;
        }
        return 5;
    }
    public static int toChannel(int mask){
        return toChannel((byte)(mask & 0x0F));
    }
    public static int[] toChannels(byte mask){
        if(mask == MASK_NONE)
            return new int[0];
        if(mask == MASK_ALL)
            return new int[]{CHANNEL_A, CHANNEL_B, CHANNEL_C, CHANNEL_D};
        int l = 0;
        for(int i = 0; i < 4; i++) {
            if ((mask & 0x08) > 0)
                l++;
            mask <<= 1;
        }
        int[] channels = new int[l];
        for(int i = 3; i >= 0; i--){
            if((mask & 0x80) > 0)
                channels[--l] = i;
            mask <<= 1;
        }
        return channels;
    }
    public static Direction getFace(Direction primary, Direction secondary){
        if(secondary == Direction.DOWN)
            return primary;
        if(primary == Direction.UP)
            return secondary.getOpposite();
        if(secondary == Direction.UP) {
            if (primary == Direction.EAST || primary == Direction.WEST)
                return primary;
            return primary.getOpposite();
        }
        primary = primary.getCounterClockWise(Direction.Axis.X);
        return BlockFrameTransformUtils.rotateDirectionClockwiseY(primary, secondary);
    }
    public static byte getFaceMask(Direction primary, Direction secondary){
        return encodeFace(getFace(primary, secondary));
    }
    public static byte getChannelMask(Direction primary, Direction secondary){
        if(primary == Direction.UP || primary == Direction.DOWN)
            return MASK_NONE;
        if(secondary == Direction.DOWN)
            return MASK_CHANNEL_C;
        if(secondary == Direction.UP)
            return MASK_CHANNEL_A;
        return switch (secondary) {
            case EAST -> MASK_CHANNEL_B;
            case WEST -> MASK_CHANNEL_D;
            default -> switch (primary){
                case NORTH, SOUTH -> switch (secondary){
                    case NORTH -> MASK_CHANNEL_A;
                    case SOUTH -> MASK_CHANNEL_C;
                    default -> MASK_NONE;
                };
                default -> switch (secondary){
                    case NORTH -> MASK_CHANNEL_B;
                    case SOUTH -> MASK_CHANNEL_D;
                    default -> MASK_NONE;
                };
            };
        };
    }
    public static byte setChannelMask(byte mask, byte channelMask){
        return (byte) (mask & 0xF0 | channelMask);
    }
    public static byte setFaceMask(byte mask, byte face){
        return (byte) (mask & 0x0F | face);
    }
    public static byte getConnectable(byte mask){
        return (byte)(mask ^ 0x80);
    }
    public static boolean connectsNone(byte mask){
        return (mask & 0x0F) == MASK_NONE;
    }
    public static boolean connectsAll(byte mask){
        return (mask & 0x0F) == MASK_ALL;
    }
    public static boolean canConnect(byte maskA, byte maskB){
        return (((maskA ^ maskB) & 0xF0) == 0x80) && ((maskA & maskB & MASK_ALL) != 0);
    }
    public static boolean sameFaceConnect(byte maskA, byte maskB){
        return (((maskA ^ maskB) & 0xF0) == 0x00) && ((maskA & maskB & MASK_ALL) != 0);
    }
    public static boolean sameMasks(byte maskA, byte maskB){
        return maskA == maskB;
    }
    public static boolean canConnectOrSame(byte maskA, byte maskB){
        return channelsConnect(maskA, maskB) && (facesConnect(maskA, maskB) || sameFaces(maskA, maskB));
    }
    public static boolean channelsConnect(byte maskA, byte maskB){
        return (maskA & maskB & MASK_ALL) != 0;
    }
    public static boolean facesConnect(byte maskA, byte maskB){
        return ((maskA ^ maskB) & 0xF0) == 0x80;
    }
    public static boolean sameFaces(byte maskA, byte maskB){
        return ((maskA ^ maskB) & 0xF0) == 0x00;
    }
    public static byte encodeFace(Direction face){
        if(face == null)
            return FACE_NULL;
        return switch (face){
            case DOWN ->  FACE_DOWN; //0x90
            case NORTH -> FACE_NORTH; //0xA0
            case EAST ->  FACE_EAST; //0x40
            case SOUTH -> FACE_SOUTH; //0x20
            case WEST ->  FACE_WEST; //0xC0
            case UP ->    FACE_UP; //0x10
        };
    }
    public static Direction decodeFace(byte mask){
        return switch ((byte)(mask & 0xF0)){
            case FACE_DOWN -> Direction.DOWN;
            case FACE_NORTH -> Direction.NORTH;
            case FACE_EAST -> Direction.EAST;
            case FACE_SOUTH -> Direction.SOUTH;
            case FACE_WEST -> Direction.WEST;
            case FACE_UP -> Direction.UP;
            default -> null;
        };
    }
    public static byte faceChannelMask(Direction face, Direction local){
        if(local == Direction.NORTH)
            return MASK_CHANNEL_A;
        if(local == Direction.SOUTH)
            return MASK_CHANNEL_C;
        if(face == Direction.NORTH || face == Direction.WEST || face == Direction.DOWN)
            return local == Direction.EAST ? MASK_CHANNEL_B : MASK_CHANNEL_D;
        return local == Direction.EAST ? MASK_CHANNEL_D : MASK_CHANNEL_B;
    }
}
