package net.acodonic_king.redstonecg.block.entity;

import net.acodonic_king.redstonecg.block.gui.redcu_wire_transition.RedCuWireTransitionGUIMenu;
import io.netty.buffer.Unpooled;
import net.acodonic_king.redstonecg.block.normal.wire.RedCuWireTransitionBlock;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.acodonic_king.redstonecg.procedures.RedCuWireTransitionPathFinder;
import net.acodonic_king.redstonecg.procedures.RedCuWireTransitionRenderEncoding;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.Consumer;

public class RedCuWireTransitionBlockEntity extends DefaultContainerBlockEntity {
    public int POWER = 0;
    public byte DOWN = 5;
    public byte NORTH = 5;
    public byte EAST = 5;
    public byte SOUTH = 5;
    public byte WEST = 5;
    public byte UP = 5;
    public byte SHAPE = 0;
    public byte WALLS = 0;
    public float ticks = 0;
    public List<Integer> RENDER_OBJECTS = new ArrayList<>();
    public RedCuWireTransitionBlockEntity(BlockPos position, BlockState state) {
        super(RedstonecgModBlockEntities.REDCU_WIRE_TRANSITION.get(), position, state, 0);
    }
    public byte getSideCharacter(String side_char){
        return getSideCharacter(side_char.charAt(0));
    }
    public byte getSideCharacter(char side_char){
        byte value = switch (side_char){
            case 'D' -> DOWN;
            case 'N' -> NORTH;
            case 'E' -> EAST;
            case 'S' -> SOUTH;
            case 'W' -> WEST;
            case 'U' -> UP;
            default -> DOWN;
        };
        return value;
    }
    public static byte getShapeIndexCharacter(char side_char){
        byte value = switch (side_char){
            case 'D' -> 0;
            case 'N' -> 1;
            case 'E' -> 2;
            case 'S' -> 3;
            case 'W' -> 4;
            case 'U' -> 5;
            default -> 0;
        };
        return value;
    }
    public static Direction getDirectionCharacter(char side_char){
        return switch (side_char){
            case 'D' -> Direction.DOWN;
            case 'N' -> Direction.NORTH;
            case 'E' -> Direction.EAST;
            case 'S' -> Direction.SOUTH;
            case 'W' -> Direction.WEST;
            case 'U' -> Direction.UP;
            default -> Direction.DOWN;
        };
    }
    public static char getCharacterDirection(Direction direction){
        return switch (direction){
            case DOWN -> 'D';
            case NORTH -> 'N';
            case EAST -> 'E';
            case SOUTH -> 'S';
            case WEST -> 'W';
            case UP -> 'U';
        };
    }
    public void setSideCharacter(String side_char, byte value){
        setSideCharacter(side_char.charAt(0), value);
    }
    public void setSideCharacter(char side_char, byte value){
        //RedstonecgMod.LOGGER.debug("seting {} {}", side_char, (int) value);
        switch (side_char){
            case 'D' -> DOWN = value;
            case 'N' -> NORTH = value;
            case 'E' -> EAST = value;
            case 'S' -> SOUTH = value;
            case 'W' -> WEST = value;
            case 'U' -> UP = value;
            default ->  DOWN = value;
        };
    }
    @Override
    public Component getDefaultName() {
        return Component.literal("redcu_wire_transition");
    }
    @Override
    public Component getDisplayName() {
        return Component.literal("RedCu Wire Transition");
    }

    @Override
    public boolean canTakeItemThroughFace(int p_19239_, ItemStack p_19240_, Direction p_19241_) {
        return false;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return true;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new RedCuWireTransitionGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
    }

    // Save NBT
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        //tag.putIntArray("render_objects", RENDER_OBJECTS);
        tag.putByte("power", (byte) (POWER & 0xFF));
        tag.putByte("ax", (byte) (((EAST & 0x0F) << 4) | (WEST & 0x0F)));
        tag.putByte("ay", (byte) (((UP & 0x0F) << 4) | (DOWN & 0x0F)));
        tag.putByte("az", (byte) (((NORTH & 0x0F) << 4) | (SOUTH & 0x0F)));
        tag.putByte("walls", WALLS);
        //RedstonecgMod.LOGGER.debug("saving additional {} {} {} {} {} {}", (int)DOWN, (int)NORTH, (int)EAST, (int)SOUTH, (int)WEST, (int)UP);
        //RedstonecgMod.LOGGER.debug(tag);
    }

    // Load NBT
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        //RedstonecgMod.LOGGER.debug(tag);
        //RENDER_OBJECTS = Arrays.stream(tag.getIntArray("render_objects")).boxed().collect(Collectors.toList());
        POWER = tag.getByte("power") & 0xFF;

        byte packedX = tag.getByte("ax");
        WEST = (byte) (packedX & 0x0F);
        EAST = (byte) ((packedX >> 4) & 0x0F);

        byte packedY = tag.getByte("ay");
        DOWN = (byte) (packedY & 0x0F);
        UP = (byte) ((packedY >> 4) & 0x0F);

        byte packedZ = tag.getByte("az");
        SOUTH = (byte) (packedZ & 0x0F);
        NORTH = (byte) ((packedZ >> 4) & 0x0F);

        WALLS = tag.getByte("walls");
        //RedstonecgMod.LOGGER.debug("loading additional {} {} {} {} {} {}", (int)DOWN, (int)NORTH, (int)EAST, (int)SOUTH, (int)WEST, (int)UP);
        pathFind();
        /*Level level = this.getLevel();
        if(level != null) {
            BlockState blockState = this.getBlockState();
            int m = blockState.getValue(RedCuWireTransitionBlock.MODEL);
            m++;
            m &= 1;
            this.getLevel().sendBlockUpdated(this.getBlockPos(), blockState, blockState.setValue(RedCuWireTransitionBlock.MODEL, m), 3);
        }*/
    }

    public void pathFind(){
        Map<String, Integer> NodesMap = new HashMap<>();
        RENDER_OBJECTS.clear();
        SHAPE = 0;
        List<String> cables = getCables((node, knot, c) -> {
            int h = (node.indexOf(knot) == 0) ? 1 : 0;
            h = 1 << h;
            int g = 1 << c;
            if (NodesMap.containsKey(knot)) {
                NodesMap.replace(knot, NodesMap.get(knot) | g);
            } else {
                NodesMap.put(knot, g);
            }
            if (NodesMap.containsKey(node)) {
                NodesMap.replace(node, NodesMap.get(node) | h);
            } else {
                NodesMap.put(node, h);
            }
        }, (node) -> {
            NodesMap.put(node,0);
            for(byte c: node.getBytes()){
                SHAPE |= (byte) (1 << getShapeIndexCharacter((char) c));
            }
            SHAPE |= (byte) (1 << 6);
        });
        if(!cables.isEmpty()){
            for(String cable: cables){
                RENDER_OBJECTS.add(
                        RedCuWireTransitionRenderEncoding.packRenderEntry(
                                0,
                                cableRotation(cable)
                        )
                );
            }
            nodeConnection(NodesMap, "D", DOWN);
            nodeConnection(NodesMap, "N", NORTH);
            nodeConnection(NodesMap, "E", EAST);
            nodeConnection(NodesMap, "S", SOUTH);
            nodeConnection(NodesMap, "W", WEST);
            nodeConnection(NodesMap, "U", UP);
        }
        NodesMap.forEach((String node, Integer IValue) -> {
            int value = IValue;
            if(node.length() == 1){
                RENDER_OBJECTS.add(
                        RedCuWireTransitionRenderEncoding.packRenderEntry(
                                shortNodeModel(value),
                                shortNodeRotation(node, value)
                        )
                );
                SHAPE |= (byte) (1 << getShapeIndexCharacter(node.charAt(0)));
            } else {
                RENDER_OBJECTS.add(
                        RedCuWireTransitionRenderEncoding.packRenderEntry(
                                longNodeModel(value),
                                longNodeRotation(node, value)
                        )
                );
            }
        });
        for(byte chr: "DNESWU".getBytes()){
            int i = getShapeIndexCharacter((char) chr);
            if(((this.WALLS >> i) & 1) > 0){
                RENDER_OBJECTS.add(
                        RedCuWireTransitionRenderEncoding.packRenderEntry(
                                15,
                                plateRotation((byte) i)
                        )
                );
            }
        }
        //RedstonecgMod.LOGGER.debug(RENDER_OBJECTS);
    }

    public List<String> getCables(){
        return getCables((A, B, C) -> {},(N) -> {});
    }
    public List<String> getCables(TriConsumer<String, String, Integer> action, Consumer<String> one_node){
        List<String> nodes = new ArrayList<>();
        addNode(nodes, "D", DOWN);
        addNode(nodes, "N", NORTH);
        addNode(nodes, "E", EAST);
        addNode(nodes, "S", SOUTH);
        addNode(nodes, "W", WEST);
        addNode(nodes, "U", UP);

        if(nodes.isEmpty()){return new ArrayList<>();}
        if(nodes.size() == 1){
            one_node.accept(nodes.get(0));
            return new ArrayList<>();
        }
        return Cableing(nodes,action);
    }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }

    public static List<String> Cableing(List<String> nodes){
        return Cableing(nodes, (A, B, C) -> {});
    }
    public static List<String> Cableing(List<String> nodes, TriConsumer<String, String, Integer> action){
        String start_node = nodes.remove(0);
        List<String> cables = new ArrayList<>();
        for (String end_node : nodes) {
            List<String> path = RedCuWireTransitionPathFinder.execute(start_node, end_node);
            String s_node = path.remove(0);
            String nodeA = s_node;
            for (String nodeB : path) {
                String knot, node;
                if (nodeA.length() == 1) {
                    knot = nodeA;
                    node = nodeB;
                } else {
                    knot = nodeB;
                    node = nodeA;
                }
                int c = RedCuWireTransitionPathFinder.nodemap.get(knot).indexOf(node);
                action.accept(node, knot, c);

                knot += c;
                if (!cables.contains(knot))
                    cables.add(knot);
                nodeA = nodeB;
            }
            //path.add(0, s_node);
        }
        return cables;
    }

    private void addNode(List<String> nodes, String g, byte c){
        //RedstonecgMod.LOGGER.debug("nodeA {} {}", g, (int) c);
        String node = RedCuWireTransitionPathFinder.getNodeBasedOnConnectionFace(g,c);
        if(node.isEmpty()) {return;}
        if(!nodes.contains(node)){nodes.add(node);}
    }

    private void nodeConnection(Map<String, Integer> NodesMap, String g, byte c){
        //RedstonecgMod.LOGGER.debug("nodeC {} {}", g, (int) c);
        if(c < 0){return;}
        if(c > 3){return;}
        String node = RedCuWireTransitionPathFinder.nodemap.get(g).get(c);
        if(!NodesMap.containsKey(node)){return;}
        int h = (node.indexOf(g) == 0) ? 3 : 2;
        h = 1 << h;
        NodesMap.replace(node, NodesMap.get(node) | h);
    }

    private int longNodeModel(int case_number){
        return switch (case_number){
            case 3 -> 6; //node_isu_o
            case 6,9 -> 1; //node_is_on
            case 5,10 -> 2; //node_is_od
            case 7,11 -> 3; //node_isu_on
            case 13,14 -> 4; //node_is_ond
            case 15 -> 5; //node_isu_ond
            default -> 11; //illegal
        };
    }

    private String longNodeRotation(String node, int case_number){
        boolean table = switch (case_number){
            case 6,7,10,14 -> false; //A
            case 3,5,9,11,13,15 -> true; //B
            default -> false; //illegal
        };
        return switch (node){
            case "NU" -> table ? "ZS>0N": "XE>0N";
            case "NE" -> table ? "ZE>0N": "XE>ZW";
            case "ND" -> table ? "0N>0N": "XE>ZS";
            case "NW" -> table ? "ZW>0N": "XE>ZE";

            case "SU" -> table ? "XS>0N": "XW>ZS";
            case "SE" -> table ? "XS>ZW": "XW>ZE";
            case "SD" -> table ? "YS>0N": "XW>0N";
            case "SW" -> table ? "YS>ZW": "XW>ZW";

            case "EU" -> table ? "YW>XS": "XE>YW";
            case "ED" -> table ? "YW>0N": "XW>YE";
            case "WU" -> table ? "YE>XS": "XE>YE";
            case "WD" -> table ? "YE>0N": "XW>YW";
            default -> "0N>0N";
        };
    }

    private String plateRotation(byte i){
        return switch (i){
            case 0 -> "0N";
            case 1 -> "XE";
            case 2 -> "XE>YW";
            case 3 -> "XE>YS";
            case 4 -> "XE>YE";
            case 5 -> "XS";
            default -> "0N";
        };
    }

    private int shortNodeModel(int case_number){
        return switch (case_number){
            case 5,10 -> 7; //node_knot_i
            case 3,6,9,12 -> 8; //node_knot_l
            case 7,11,13,14 -> 9; //node_knot_t
            case 15 -> 10; //node_knot_x
            default -> 11; //illegal
        };
    }

    private String shortNodeRotation(String node, int case_number){
        String rotation = switch (node){
            case "D", "N" -> switch (case_number){
                case 3, 5, 11, 15 -> "0N";
                case 6, 7, 10 -> "YW";
                case 9, 13 -> "YE";
                case 12, 14 -> "YS";
                default -> "0N"; //illegal
            };
            case "S", "U" -> switch (case_number){
                case 5, 6, 14, 15 -> "0N";
                case 3, 7, 10 -> "YW";
                case 9, 11 -> "YS";
                case 12, 13 -> "YE";
                default -> "0N"; //illegal
            };
            case "E" -> switch (case_number) {
                case 3, 7, 10, 15 -> "0N";
                case 5, 9, 11 -> "YW";
                case 6, 14 -> "YE";
                case 12, 13 -> "YS";
                default -> "0N"; //illegal
            };
            case "W" -> switch (case_number) {
                case 7, 10, 6, 15 -> "0N";
                case 5, 12, 14 -> "YW";
                case 3, 11 -> "YE";
                case 9, 13 -> "YS";
                default -> "0N"; //illegal
            };
            default -> "0N"; //illegal
        };
        rotation += ">";
        rotation += switch (node){
            case "D" -> "0N";
            case "N" -> "XE";
            case "E" -> "ZE";
            case "S" -> "XW";
            case "W" -> "ZW";
            case "U" -> "XS";
            default -> "0N"; //illegal
        };
        return rotation;
    }

    private String cableRotation(String cable){
        char dir = cable.charAt(0);
        char con = cable.charAt(1);
        String rotation = switch (dir){
            case 'D' -> "0N";
            case 'N' -> "XE";
            case 'E' -> "ZE";
            case 'S' -> "XW";
            case 'W' -> "ZW";
            case 'U' -> "ZS";
            default -> "0N"; //illegal
        };
        rotation += ">";
        rotation += switch (dir){
            case 'D', 'U' -> switch (con){
                case '0' -> "0N";
                case '1' -> "YW";
                case '2' -> "YS";
                case '3' -> "YE";
                default -> "0N"; //illegal
            };
            case 'E', 'W' -> switch (con){
                case '0' -> "XE";
                case '1' -> "0N";
                case '2' -> "XW";
                case '3' -> "XS";
                default -> "0N"; //illegal
            };
            case 'N' -> switch (con){
                case '0' -> "0N";
                case '1' -> "ZW";
                case '2' -> "ZS";
                case '3' -> "ZE";
                default -> "0N"; //illegal
            };
            case 'S' -> switch (con){
                case '0' -> "ZS";
                case '1' -> "ZE";
                case '2' -> "0N";
                case '3' -> "ZW";
                default -> "0N"; //illegal
            };
            default -> "0N"; //illegal
        };
        return rotation;
    }
}
