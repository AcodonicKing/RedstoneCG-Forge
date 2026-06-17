package net.acodonic_king.redstonecg.block.entity;

import io.netty.buffer.Unpooled;
import net.acodonic_king.redstonecg.block.normal.interaction.ControlPanelBlock;
import net.acodonic_king.redstonecg.block.control_panel.PanelLogicRegistry;
import net.acodonic_king.redstonecg.block.control_panel.logic.DefaultPanelLogic;
import net.acodonic_king.redstonecg.block.gui.control_panel.ControlPanelGUIMenu;
import net.acodonic_king.redstonecg.block.gui.pinmark_configurator.PinmarkConfiguratorLogic;
import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;
import net.acodonic_king.redstonecg.network.MessengerBlockEntityPigeon;
import net.acodonic_king.redstonecg.procedures.BlockFrameTransformUtils;
import net.acodonic_king.redstonecg.procedures.ConnectionFace;
import net.acodonic_king.redstonecg.procedures.LittleTools;
import net.acodonic_king.redstonecg.procedures.RCGMatrix;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static net.acodonic_king.redstonecg.block.gui.pinmark_configurator.PinmarkConfiguratorLogic.DISABLED_TYPE;
import static net.acodonic_king.redstonecg.block.gui.pinmark_configurator.PinmarkConfiguratorLogic.PINMARK_A;


public class ControlPanelBlockEntity extends DefaultContainerBlockEntity {
    public ControlPanelBlockEntity(BlockEntityType<?> type, BlockPos position, BlockState state, int container_size){
        super(type, position, state, container_size);
        PANELS = NonNullList.withSize(container_size, new DefaultPanelLogic(ItemStack.EMPTY, 0));
        SLOT_ANGLES = new float[container_size];
    }

    public ControlPanelBlockEntity(BlockPos position, BlockState state, int container_size) {
        super(RedstonecgModBlockEntities.CONTROL_PANEL.get(), position, state, container_size);
        PANELS = NonNullList.withSize(container_size, new DefaultPanelLogic(ItemStack.EMPTY, 0));
        SLOT_ANGLES = new float[container_size];
    }

    @Override
    public Component getDefaultName() {
        return Component.literal("control_panel");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new ControlPanelGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Control Panel");
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    public NonNullList<DefaultPanelLogic> PANELS;
    public float[] SLOT_ANGLES;
    public byte CONNECTION = 0; //WSEN WSEN

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        //RedstonecgMod.LOGGER.debug(compound);
        if(compound.contains("schedule"))
            loadSchedule(compound.getCompound("schedule"));
        if(compound.contains("connection"))
            CONNECTION = compound.getByte("connection");
        if(compound.contains("slot_angles")){
            ListTag list = compound.getList("slot_angles", CompoundTag.TAG_FLOAT);
            for(int i = 0; i < Math.min(list.size(), SLOT_ANGLES.length); i++)
                SLOT_ANGLES[i] = list.getFloat(i);
        }
        initPanelLogic();
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("schedule", saveSchedule());
        compound.putByte("connection", CONNECTION);
        ListTag list = new ListTag();
        for(float v: SLOT_ANGLES)
            list.add(FloatTag.valueOf(v));
        compound.put("slot_angles", list);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = this.saveWithFullMetadata();
        if(tag.contains("schedule"))
            tag.remove("schedule");
        return tag;
    }

    public CompoundTag getParameterSet(){
        CompoundTag tag = new CompoundTag();
        tag.putByte("connection", CONNECTION);
        ListTag list = new ListTag();
        for(float v: SLOT_ANGLES)
            list.add(FloatTag.valueOf(v));
        tag.put("slot_angles", list);
        ContainerHelper.saveAllItems(tag, this.stacks);
        return tag;
    }

    public void setParameterSet(CompoundTag tag, LivingEntity entity){
        if(tag.contains("connection"))
            CONNECTION = tag.getByte("connection");
        if(tag.contains("slot_angles")){
            ListTag list = tag.getList("slot_angles", CompoundTag.TAG_FLOAT);
            for(int i = 0; i < Math.min(list.size(), SLOT_ANGLES.length); i++)
                SLOT_ANGLES[i] = list.getFloat(i);
        }
        NonNullList<ItemStack> inv = NonNullList.withSize(this.stacks.size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, inv);
        if(entity instanceof Player player) {
            if (player.getAbilities().instabuild)
                for (int i = 0; i < this.stacks.size(); i++)
                    this.stacks.set(i, inv.get(i));
            else {
                for (int i = 0; i < this.stacks.size(); i++) {
                    ItemStack loadStack = inv.get(i);
                    if (loadStack.isEmpty())
                        continue;
                    if (LittleTools.hasItem(player, loadStack.getItem()) == 0)
                        continue;
                    this.stacks.set(i, LittleTools.popItem(player, loadStack.getItem()));
                }
            }
        }
        initPanelLogic();
    }

    public void setPinConfigLogicA(PinmarkConfiguratorLogic pcl){
        int o = this.getBlockState().getValue(ControlPanelBlock.ORIENTATION);
        if(o < 30) {
            pcl.pinAll(PINMARK_A);
            pcl.setStates(CONNECTION & 0b1111);
            pcl.stateDown(((CONNECTION >> 6) & 1) != 0);
        } else {
            pcl.pinAll(PINMARK_A).pinEast(DISABLED_TYPE);
            pcl.setStates(CONNECTION & 0b1101);
            pcl.stateDown(((CONNECTION >> 7) & 1) != 0);
        }
    }

    public void setPinConfigLogicB(PinmarkConfiguratorLogic pcl){
        int o = this.getBlockState().getValue(ControlPanelBlock.ORIENTATION);
        if(o > 29) {
            pcl.pinAll(PINMARK_A).pinWest(DISABLED_TYPE);
            pcl.setStates((CONNECTION >> 4) & 0b0111);
            pcl.stateDown((CONNECTION & 2) != 0);
        }
    }

    public void loadPinConfigLogic(PinmarkConfiguratorLogic pclA, PinmarkConfiguratorLogic pclB){
        int o = this.getBlockState().getValue(ControlPanelBlock.ORIENTATION);
        CONNECTION = 0;
        if(o < 30) {
            CONNECTION |= (byte) (pclA.getStates() & 0b1111);
            if(pclA.stateDown())
                CONNECTION |= (byte) 0b01000000;
        } else {
            CONNECTION |= (byte) (pclA.getStates() & 0b1101);
            if(pclA.stateDown())
                CONNECTION |= (byte) 0b10000000;
            CONNECTION |= (byte) ((pclB.getStates() & 0b0111) << 4);
            if(pclB.stateDown())
                CONNECTION |= (byte) 0b00000010;
        }
    }

    public void initPanelLogicPredict(ItemStack itemStack, int slot){
        for(int i = 0; i < PANELS.size(); i++){
            ItemStack stack = stacks.get(i);
            if (i == slot)
                stack = itemStack;
            if(PANELS.get(i).ITEM_STACK.is(stack.getItem())) {
                try {
                    PANELS.get(i).loadStack(stack);
                } catch (IllegalArgumentException ignored) {
                    PANELS.set(i, PanelLogicRegistry.get(stack, i));
                }
                continue;
            }
            PANELS.set(i, PanelLogicRegistry.get(stack, i));
        }
    }

    public void initPanelLogic(){
        for(int i = 0; i < PANELS.size(); i++){
            ItemStack stack = stacks.get(i);
            if(PANELS.get(i).ITEM_STACK.is(stack.getItem())) {
                try {
                    PANELS.get(i).loadStack(stack);
                } catch (IllegalArgumentException ignored) {
                    //RedstonecgMod.LOGGER.debug(ignored);
                    PANELS.set(i, PanelLogicRegistry.get(stack, i));
                }
                continue;
            }
            PANELS.set(i, PanelLogicRegistry.get(stack, i));
        }
    }

    public DefaultPanelLogic get(int i) {
        return PANELS.get(i);
    }

    public int range() {
        return PANELS.size();
    }
    
    public static final float[] ANGLES = new float[]{(float) (Math.PI * 0.0), (float) (Math.PI * 0.5), (float) (Math.PI * 1.0), (float) (Math.PI * 1.5)};

    public static RCGMatrix.M4F sideTransform(RCGMatrix.M4F matrix, int orientation){
        matrix.identity().translate(0.5f, 0.5f, 0.5f);
        if (orientation < 6){
            matrix = switch (orientation){
                case 0 -> matrix;
                case 5 -> matrix.rotateX(ANGLES[2]);
                default -> matrix.rotateY(-ANGLES[orientation - 1]).rotateX(ANGLES[1]);
            };
        } else if (orientation < 10) {
            matrix.rotateY(-ANGLES[orientation - 6]);
        } else if (orientation < 26) {
            int o = orientation - 10;
            matrix
                    .rotateY(-ANGLES[o >> 2])
                    .rotateZ(-ANGLES[o & 3])
                    .rotateX(ANGLES[1]);
        } else if (orientation < 30) {
            matrix.rotateY(-ANGLES[orientation - 26]).rotateX(ANGLES[2]);
        } else if (orientation < 34) {
            matrix.rotateY(-ANGLES[(orientation - 31) & 3]);
        } else if (orientation < 38) {
            matrix.rotateY(-ANGLES[(orientation - 35) & 3]).rotateX(ANGLES[1]);
        } else if (orientation < 42) {
            matrix.rotateY(-ANGLES[(orientation - 39) & 3]).rotateX(ANGLES[2]);
        }
        return matrix.translate(-0.5f, -0.5f, -0.5f);
    }
    public static RCGMatrix.M4F sideTransformB(RCGMatrix.M4F matrix, int orientation){
        matrix.identity().translate(0.5f, 0.5f, 0.5f);
        if (orientation < 30) {
            return matrix.translate(-0.5f, -0.5f, -0.5f);
        } else if (orientation < 34) {
            matrix.rotateY(-ANGLES[orientation - 30]).rotateZ(ANGLES[1]);
        } else if (orientation < 38) {
            matrix.rotateY(-ANGLES[orientation - 34]);
        } else if (orientation < 42) {
            matrix.rotateY(-ANGLES[orientation - 38]).rotateZ(ANGLES[3]);
        }
        return matrix.rotateX(ANGLES[1]).translate(-0.5f, -0.5f, -0.5f);
    }

    public List<ConnectionFace> getConnectionFaces(int orientation){
        List<ConnectionFace> conn = new ArrayList<>();
        if (orientation < 30) {
            int connection = CONNECTION & 0xFF;
            connection &= 0b1111;
            connection |= (CONNECTION >> 2) & 0b00010000;
            //RedstonecgMod.LOGGER.debug(connection);
            for (int i = 0; i < 5; i++) {
                if ((connection & 1) == 0) {
                    connection >>= 1;
                    continue;
                }
                connection >>= 1;
                if (orientation < 6) {
                    Direction f = BlockFrameTransformUtils.decodeIntToDirection(orientation);
                    if(i == 4)
                        conn.add(new ConnectionFace(f, 4));
                    else {
                        Direction d = BlockFrameTransformUtils.decodeIntToDirection(i + 1);
                        conn.add(new ConnectionFace(d, f));
                    }
                } else {
                    Direction f = BlockFrameTransformUtils.decodeIntToDirection((orientation - 6) >> 2);
                    if(i == 4)
                        conn.add(new ConnectionFace(f,4));
                    else {
                        Direction d = BlockFrameTransformUtils.decodeIntToDirection(i + 1);
                        Direction r = BlockFrameTransformUtils.decodeIntToDirection(((orientation - 6) & 3) + 1);
                        d = BlockFrameTransformUtils.rotateDirectionClockwiseY(d, r);
                        conn.add(new ConnectionFace(d, f));
                    }
                }
            }
        } else {
            Direction A = Direction.DOWN;
            Direction B = Direction.NORTH;
            if (orientation < 34) {
                B = BlockFrameTransformUtils.decodeIntToDirection(orientation - 29);
            } else if (orientation < 38) {
                A = BlockFrameTransformUtils.decodeIntToDirection(((orientation - 35) & 3) + 1);
                B = BlockFrameTransformUtils.decodeIntToDirection(orientation - 33);
            } else {
                A = Direction.UP;
                B = BlockFrameTransformUtils.decodeIntToDirection(orientation - 37);
            }
            for (int[] im : O2SMA) {
                if ((im[1] & CONNECTION) == 0)
                    continue;
                if (im[0] == 4)
                    conn.add(new ConnectionFace(A, 4));
                else {
                    Direction d = BlockFrameTransformUtils.decodeIntToDirection(im[0] + 1);
                    if (orientation < 34) {
                        d = BlockFrameTransformUtils.rotateDirectionClockwiseY(d, Direction.WEST);
                        d = BlockFrameTransformUtils.rotateDirectionClockwiseY(B, d);
                    } else if (orientation > 37) {
                        d = BlockFrameTransformUtils.rotateDirectionClockwiseY(d, Direction.EAST);
                        d = BlockFrameTransformUtils.rotateDirectionClockwiseY(B, d);
                    }
                    conn.add(new ConnectionFace(d, A));
                }
            }
            for (int[] im : O2SMB) {
                if ((im[1] & CONNECTION) == 0)
                    continue;
                if (im[0] == 4)
                    conn.add(new ConnectionFace(B, 4));
                else {
                    Direction d = BlockFrameTransformUtils.decodeIntToDirection(im[0] + 1);
                    if (orientation < 34)
                        d = BlockFrameTransformUtils.rotateDirectionClockwiseY(d, Direction.WEST);
                    else if (orientation > 37)
                        d = BlockFrameTransformUtils.rotateDirectionClockwiseY(d, Direction.EAST);
                    conn.add(new ConnectionFace(d, B));
                }
            }
        }
        return conn;
    }

    public ConnectionFace getConnectionFace(int orientation, ConnectionFace requesterFace){
        Direction direction = requesterFace.FACE.getOpposite();
        if (orientation < 30) {
            int connection = CONNECTION & 0xFF;
            connection &= 0b1111;
            connection |= (CONNECTION >> 2) & 0b00010000;
            int o = orientation;
            Direction primary = Direction.NORTH;
            if (orientation > 5) {
                primary = BlockFrameTransformUtils.decodeIntToDirection(((orientation - 6) & 3) + 1);
                o = (orientation - 6) >> 2;
            }
            Direction secondary = BlockFrameTransformUtils.decodeIntToDirection(o);
            Direction d = BlockFrameTransformUtils.getLocalDirectionFromWorld(primary, secondary, direction);
            if(d == Direction.UP)
                return new ConnectionFace(direction, 5);
            if(d == Direction.DOWN){
                if((connection & 0b00010000) > 0)
                    return new ConnectionFace(direction, 4);
                return new ConnectionFace(direction, 5);
            }
            int s = BlockFrameTransformUtils.encodeDirectionToInt(d) - 1;
            d = BlockFrameTransformUtils.rotateDirectionClockwiseY(d, primary);
            if((connection & (1 << s)) > 0)
                return new ConnectionFace(d, secondary);
        }
        Direction A = Direction.DOWN;
        Direction B = Direction.NORTH;
        Direction Ar = Direction.NORTH;
        Direction Br = Direction.NORTH;
        if (orientation < 34) {
            B = BlockFrameTransformUtils.decodeIntToDirection(orientation - 29);
            //Ar = BlockFrameTransformUtils.rotateDirectionClockwiseY(B, Direction.WEST);
            //Br = Direction.WEST;
        } else if (orientation < 38) {
            A = BlockFrameTransformUtils.decodeIntToDirection(((orientation - 35) & 3) + 1);
            B = BlockFrameTransformUtils.decodeIntToDirection(orientation - 33);
        } else {
            A = Direction.UP;
            B = BlockFrameTransformUtils.decodeIntToDirection(orientation - 37);
            //Ar = BlockFrameTransformUtils.rotateDirectionClockwiseY(B, Direction.EAST);
            //Br = Direction.EAST;
        }
        Direction Al = BlockFrameTransformUtils.getLocalDirectionFromWorld(Ar, A, direction);
        Direction Bl = BlockFrameTransformUtils.getLocalDirectionFromWorld(Br, B, direction);
        if (Al == Direction.DOWN) {
            if ((CONNECTION & O2SMA[3][1]) > 0)
                return new ConnectionFace(A, 4);
            return new ConnectionFace(A, 5);
        }
        if (Bl == Direction.DOWN) {
            if ((CONNECTION & O2SMB[3][1]) > 0)
                return new ConnectionFace(B, 4);
            return new ConnectionFace(B, 5);
        }
        for (int[] im : O2SMA) {
            if ((im[1] & CONNECTION) == 0)
                continue;
            if (im[0] == 4)
                continue;
            else {
                Direction d = BlockFrameTransformUtils.decodeIntToDirection(im[0] + 1);
                if (orientation < 34) {
                    d = BlockFrameTransformUtils.rotateDirectionClockwiseY(d, Direction.WEST);
                    d = BlockFrameTransformUtils.rotateDirectionClockwiseY(B, d);
                } else if (orientation > 37) {
                    d = BlockFrameTransformUtils.rotateDirectionClockwiseY(d, Direction.EAST);
                    d = BlockFrameTransformUtils.rotateDirectionClockwiseY(B, d);
                }
                ConnectionFace out = new ConnectionFace(d, A);
                if(out.canConnect(requesterFace))
                    return out;
            }
        }
        for (int[] im : O2SMB) {
            if ((im[1] & CONNECTION) == 0)
                continue;
            if (im[0] == 4)
                continue;
            else {
                Direction d = BlockFrameTransformUtils.decodeIntToDirection(im[0] + 1);
                if (orientation < 34)
                    d = BlockFrameTransformUtils.rotateDirectionClockwiseY(d, Direction.WEST);
                else if (orientation > 37)
                    d = BlockFrameTransformUtils.rotateDirectionClockwiseY(d, Direction.EAST);
                ConnectionFace out = new ConnectionFace(d, B);
                if(out.canConnect(requesterFace))
                    return out;
            }
        }
        return new ConnectionFace(direction, 5);
    }

    public static final int[][] O2SMA = {{0, 0b00000001}, {2, 0b00000100}, {3, 0b00001000}, {4, 0b10000000}};
    public static final int[][] O2SMB = {{0, 0b00010000}, {1, 0b00100000}, {2, 0b01000000}, {4, 0b00000010}};

    public RCGMatrix.M4F slotOrientationTransform(RCGMatrix.M4F matrix, int i){
        int orientation = this.getBlockState().getValue(ControlPanelBlock.ORIENTATION);
        matrix.identity().translate(0.5f, 0.5f, 0.5f);
        if (orientation < 6){
            (switch (orientation){
                case 0 -> matrix;
                case 5 -> matrix.rotateX(ANGLES[2]);
                default -> matrix.rotateY(-ANGLES[orientation - 1]).rotateX(ANGLES[1]);
            }).translate(-0.5f, 0.125f, -0.5f);
        } else if (orientation < 10) {
            matrix
                    .rotateY(-ANGLES[orientation - 6])
                    .translate(-0.5f, 0.49f, -0.415f)
                    .rotateX(0.3926990817f)
            ;
        } else if (orientation < 26) {
            int o = orientation - 10;
            matrix.rotateY(-ANGLES[o >> 2]);
            switch (o & 3){
                case 0 -> matrix.translate(-0.5f, 0.415f, 0.49f).rotateX(0.3926990817f + ANGLES[1]);
                case 1 -> matrix.translate(-0.51f, 0.5f, 0.115f).rotateY(-0.3926990817f).rotateX(ANGLES[1]);
                case 2 -> matrix.translate(-0.5f, 0.51f, 0.115f).rotateX(ANGLES[1] - 0.3926990817f);
                case 3 -> matrix.translate(-0.415f, 0.5f, 0.49f).rotateY(0.3926990817f).rotateX(ANGLES[1]);
            }
        } else if (orientation < 30) {
            matrix
                    .rotateY(-ANGLES[orientation - 26])
                    .translate(0.5f, -0.11f, -0.51f)
                    .rotateX(0.3926990817f + ANGLES[2])
                    .rotateY(ANGLES[2])
            ;
        } else if (orientation < 34) {
            matrix
                    .rotateY(-ANGLES[orientation - 30])
                    .translate(-0.5f, 0.44f, -0.27f)
                    .rotateX(ANGLES[1] * 0.5f)
            ;
        } else if (orientation < 38) {
            matrix
                    .rotateY(-ANGLES[orientation - 34])
                    .translate(-0.27f, 0.5f, 0.44f)
                    .rotateY(ANGLES[1] * 0.5f)
                    .rotateX(ANGLES[1])
            ;
        } else if (orientation < 42) {
            matrix
                    .rotateY(-ANGLES[orientation - 38])
                    .translate(-0.5f, 0.27f, 0.44f)
                    .rotateX(ANGLES[1] * 1.5f)
            ;
        }
        return matrix;
    }

    public float slotSizeX(int i){
        return 1.0f;
    }

    public float slotSizeZ(int i){
        return 1.0f;
    }

    public RCGMatrix.M4F slotTransform(RCGMatrix.M4F matrix, int i){
        slotOrientationTransform(matrix, i);
        float angle = -SLOT_ANGLES[i];
        return matrix.translate(0.5f, 0.5f, 0.5f).rotateY(angle).translate(-0.5f, -0.5f, -0.5f);
    }

    public static int s0321(int v){
        return (v * 3) & 3;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        //load(tag);
        super.handleUpdateTag(tag);
        //ContainerHelper.loadAllItems(tag, this.stacks);
        //initPanelLogic();
    }

    public static class ScheduledTick{
        public int NEXT_IN = 0;
        public int SLOT = 0;
        public ScheduledTick(int in, int slot){
            NEXT_IN = in;
            SLOT = slot;
        }
        @Override
        public String toString(){
            return "ScheduledTick{in= "+NEXT_IN+", slot= "+SLOT+"}";
        }
    }

    List<ScheduledTick> SCHEDULED_UPDATES = new ArrayList<>();

    public CompoundTag saveSchedule(){
        int[] timeList = new int[SCHEDULED_UPDATES.size()];
        int[] slotList = new int[SCHEDULED_UPDATES.size()];
        int i = 0;
        for(ScheduledTick tick: SCHEDULED_UPDATES){
            timeList[i] = tick.NEXT_IN;
            slotList[i] = tick.SLOT;
        }
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("tick", timeList);
        tag.putIntArray("slot", slotList);
        return tag;
    }

    public void loadSchedule(CompoundTag tag){
        int[] timeList = tag.getIntArray("tick");
        int[] slotList = tag.getIntArray("slot");
        SCHEDULED_UPDATES.clear();
        for(int i = 0; i < Math.min(timeList.length, slotList.length); i++)
            SCHEDULED_UPDATES.add(new ScheduledTick(timeList[i], slotList[i]));
    }

    public void schedule(LevelAccessor world, int in, int slot){
        if(world.isClientSide())
            return;
        BlockPos pos = this.getBlockPos();
        if(SCHEDULED_UPDATES.isEmpty()){
            SCHEDULED_UPDATES.add(new ScheduledTick(in, slot));
            world.scheduleTick(pos, this.getBlockState().getBlock(), in);
            return;
        }
        if(!world.getBlockTicks().hasScheduledTick(pos, this.getBlockState().getBlock()))
            world.scheduleTick(pos, this.getBlockState().getBlock(), 1);
        int tick_sum = 0;
        int i = 0;
        for(i = 0; i < SCHEDULED_UPDATES.size(); i++){
            int next_tick = SCHEDULED_UPDATES.get(i).NEXT_IN;
            if(tick_sum + next_tick > in){
                SCHEDULED_UPDATES.add(i, new ScheduledTick(in - tick_sum, slot));
                return;
            }
            tick_sum += next_tick;
        }
        SCHEDULED_UPDATES.add(new ScheduledTick(in - tick_sum, slot));
    }

    public boolean nextTick(LevelAccessor world, BlockPos pos){
        if(SCHEDULED_UPDATES.isEmpty())
            return false;
        int slot = SCHEDULED_UPDATES.get(0).SLOT;
        SCHEDULED_UPDATES.remove(0);
        boolean update = PANELS.get(slot).tick(this, world, pos);
        if(SCHEDULED_UPDATES.isEmpty())
            return update;
        ScheduledTick tick = SCHEDULED_UPDATES.get(0);
        if(tick.NEXT_IN == 0)
            update = update || nextTick(world, pos);
        else
            world.scheduleTick(pos, world.getBlockState(pos).getBlock(), tick.NEXT_IN);
        return update;
    }

    public boolean finishTick(LevelAccessor world, BlockPos pos){
        boolean update = nextTick(world, pos);
        if(SCHEDULED_UPDATES.isEmpty())
            return update;
        if(SCHEDULED_UPDATES.get(0).NEXT_IN == 0)
            world.scheduleTick(pos, world.getBlockState(pos).getBlock(), 1);
        //RedstonecgMod.LOGGER.debug("the "+SCHEDULED_UPDATES);
        return update;
    }

    public InteractionResult usePanelSlot(int slot, LevelAccessor world, BlockPos pos, Player player){
        /*DefaultPanelLogic logic = PANELS.get(slot);
        ItemStack stack = stacks.get(slot);
        InteractionResult result = InteractionResult.PASS;
        if(logic.ITEM_STACK.is(stack.getItem()))
            result = logic.use(this, world, pos, player);
        else {
            logic = PANELS.set(slot, PanelLogicRegistry.get(stack, slot));
            result = logic.use(this, world, pos, player);
        }*/
        InteractionResult result = PANELS.get(slot).use(this, world, pos, player);
        return result;
    }

    public int provideRedstone(LevelAccessor world, BlockPos pos){
        return PANELS.get(0).provideRedCu(this, world, pos) >> 4;
    }

    public boolean receiveRedstone(LevelAccessor world, BlockPos pos, int power){
        return PANELS.get(0).receiveRedCu(this, world, pos, power << 4);
    }

    public void syncInventory(BlockPos pos){
        /*for(int i = 0; i < this.stacks.size(); i++)
            this.stacks.set(i, PANELS.get(i).ITEM_STACK);*/
        CompoundTag tag = ContainerHelper.saveAllItems(new CompoundTag(), this.stacks);
        //RedstonecgMod.LOGGER.debug(tag);
        MessengerBlockEntityPigeon.send(new MessengerBlockEntityPigeon(pos, tag), false);
    }

    public void syncTag(BlockPos pos, CompoundTag tag){
        tag = ContainerHelper.saveAllItems(tag, this.stacks);
        MessengerBlockEntityPigeon.send(new MessengerBlockEntityPigeon(pos, tag));
    }
}
