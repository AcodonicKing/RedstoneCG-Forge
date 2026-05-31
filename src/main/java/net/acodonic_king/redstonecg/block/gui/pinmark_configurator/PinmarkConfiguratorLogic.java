package net.acodonic_king.redstonecg.block.gui.pinmark_configurator;

import net.minecraft.core.Direction;

public class PinmarkConfiguratorLogic {
    public static final int DISABLED_TYPE = 0;
    public static final int PINMARK_A = 1;
    public static final int PINMARK_B = 2;
    public static final int PINMARK_C = 3;
    
    /* 0dwsenDD WWSSEENN
    dwsen - pin is enabled or disabled
    DDWWSSEENN - pin type is none, A, B or C.
     */
    public short SETTING = 0;
    
    public PinmarkConfiguratorLogic(){}

    public PinmarkConfiguratorLogic setState(int a, boolean s){
        if(s)
            SETTING |= (short) (1 << a);
        else
            SETTING &= (short) ~(1 << a);
        return this;
    }

    public boolean getState(int a){
        return ((SETTING >> a) & 1) != 0;
    }

    public PinmarkConfiguratorLogic setStates(int v){
        SETTING &= (short) 0b1000001111111111;
        SETTING |= (short) ((v & 0b00011111) << 10);
        return this;
    }

    public int getStates(){
        return (SETTING >> 10) & 0b00011111;
    }

    public PinmarkConfiguratorLogic setType(int a, int v){
        v &= 3;
        SETTING &= (short) ~(3 << a);
        SETTING |= (short) (v << a);
        return this;
    }

    public int getType(int a){
        return (SETTING >> a) & 3;
    }

    public PinmarkConfiguratorLogic setPins(int v){
        SETTING &= (short) 0b1111110000000000;
        SETTING |= (short) (v & 0b0000001111111111);
        return this;
    }

    public int getPins(){
        return SETTING & 0b0000001111111111;
    }

    public PinmarkConfiguratorLogic pinNorth(int type){ return setType(0, type); }
    public PinmarkConfiguratorLogic pinEast(int type){ return setType(2, type); }
    public PinmarkConfiguratorLogic pinSouth(int type){ return setType(4, type); }
    public PinmarkConfiguratorLogic pinWest(int type){ return setType(6, type); }
    public PinmarkConfiguratorLogic pinDown(int type){ return setType(8, type); }
    public PinmarkConfiguratorLogic pinDirection(Direction direction, int type){
        if(direction == Direction.UP || direction == null)
            return this;
        return setType(switch (direction){
            case NORTH -> 0;
            case EAST -> 2;
            case SOUTH -> 4;
            case WEST -> 6;
            case DOWN -> 8;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        }, type);
    }
    public PinmarkConfiguratorLogic pinAll(int type){
        SETTING &= (short) 0b1111110000000000;
        for(int i = 0; i < 5; i++) {
            SETTING |= (short) type;
            type <<= 2;
        }
        return this;
    }

    public int pinNorth(){ return getType(0); }
    public int pinEast(){ return getType(2); }
    public int pinSouth(){ return getType(4); }
    public int pinWest(){ return getType(6); }
    public int pinDown(){ return getType(8); }
    public int pinDirection(Direction direction){
        if(direction == Direction.UP || direction == null)
            return 0;
        return getType(switch (direction){
            case NORTH -> 0;
            case EAST -> 2;
            case SOUTH -> 4;
            case WEST -> 6;
            case DOWN -> 8;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        });
    }

    public PinmarkConfiguratorLogic stateNorth(boolean state){ return setState(10, state); }
    public PinmarkConfiguratorLogic stateEast(boolean state){ return setState(11, state); }
    public PinmarkConfiguratorLogic stateSouth(boolean state){ return setState(12, state); }
    public PinmarkConfiguratorLogic stateWest(boolean state){ return setState(13, state); }
    public PinmarkConfiguratorLogic stateDown(boolean state){ return setState(14, state); }
    public PinmarkConfiguratorLogic stateDirection(Direction direction, boolean state){
        if(direction == Direction.UP || direction == null)
            return this;
        return setState(switch (direction){
            case NORTH -> 10;
            case EAST -> 11;
            case SOUTH -> 12;
            case WEST -> 13;
            case DOWN -> 14;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        }, state);
    }

    public boolean stateNorth(){ return getState(10); }
    public boolean stateEast(){ return getState(11); }
    public boolean stateSouth(){ return getState(12); }
    public boolean stateWest(){ return getState(13); }
    public boolean stateDown(){ return getState(14); }
    public boolean stateDirection(Direction direction){
        if(direction == Direction.UP || direction == null)
            return false;
        return getState(switch (direction){
            case NORTH -> 10;
            case EAST -> 11;
            case SOUTH -> 12;
            case WEST -> 13;
            case DOWN -> 14;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        });
    }
}
