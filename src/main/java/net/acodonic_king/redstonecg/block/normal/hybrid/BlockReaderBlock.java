package net.acodonic_king.redstonecg.block.normal.hybrid;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.defaults.PinMarkConnectionInterface;
import net.acodonic_king.redstonecg.init.RedstonecgModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComparatorBlock;
import net.acodonic_king.redstonecg.block.defaults.DefaultAnalogInteractibleGate;
import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class BlockReaderBlock extends DefaultAnalogInteractibleGate implements PinMarkConnectionInterface {
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,6);
    public static final BooleanProperty BASE_READ = BooleanProperty.create("base_read");
    public BlockReaderBlock(){
        super();
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(BASE_READ, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION,BASE_READ);
    }
    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        ConnectionFace connectionFaceB = BlockFrameTransformUtils.canConnectRedstoneTargetConnectionFace(world, pos, side);
        return CanConnectWallGateProcedure.To1_3Gate(state, connectionFaceB);
    }
    @Override
    public ConnectionFace getOutputConnectionFace(LevelAccessor world, BlockPos pos, ConnectionFace requesterFace) {
        BlockState blockState = world.getBlockState(pos);
        ConnectionFace connectionFaceA = requesterFace.getConnectable();
        if(!CanConnectWallGateProcedure.To1_3Gate(blockState, requesterFace))
            connectionFaceA.CHANNEL = 5;
        return connectionFaceA;
    }
    @Override
    public boolean isOutput(LevelAccessor world, BlockState blockState, BlockPos pos, Direction direction){
        ConnectionFace connectionFaceB = new ConnectionFace(direction.getOpposite());
        return CanConnectWallGateProcedure.To1_3Gate(blockState, connectionFaceB);
    }
    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState blockState, BlockPos pos){
        Direction side = Direction.UP;
        if(blockState.getValue(BASE_READ))
            side = BlockFrameTransformUtils.getWorldDirectionFromLocal(blockState, Direction.DOWN);
        else
            side = BlockFrameTransformUtils.getWorldDirectionFromLocalForward(blockState);
        int power = getInputSignal((Level) world, pos, side);
        if (power == 0) {
            ConnectionFace thisFace = BlockFrameTransformUtils.getConnectionFace(blockState, side);
            power = GetRedstoneSignalProcedure.execute(world, pos, thisFace);
        }
        if(world.getBlockEntity(pos) instanceof DefaultAnalogGateBlockEntity be){
            if(be.POWER == power){ return power; }
            be.POWER = power;
            be.setChanged();
            LittleTools.setBooleanProperty(world, pos, power > 0, "visible_state", 2);
            int connection = blockState.getValue(CONNECTION);
            connection = (connection + 1) << 1;
            connection = ConnectionFacePrimaryRange.rotateFilter(connection, blockState.getValue(ROTATION));
            ConnectionFacePrimaryRange connectionFaceRange = new ConnectionFacePrimaryRange(blockState.getValue(FACING));
            for(ConnectionFace connectionFaceA: connectionFaceRange.getList(connection)){
                sendRedstoneUpdateInDirection(world, blockState.getBlock(), pos, connectionFaceA.FACE);
            }
        }
        return power;
    }

    //From Comparator Code with some modifications
    private ItemFrame getItemFrame(Level var1, Direction var2, BlockPos var3) {
        List var4 = var1.getEntitiesOfClass(ItemFrame.class, new AABB((double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), (double)(var3.getX() + 1), (double)(var3.getY() + 1), (double)(var3.getZ() + 1)), (var1x) -> {
            return var1x != null && var1x.getDirection() == var2;
        });
        return var4.size() == 1 ? (ItemFrame)var4.get(0) : null;
    }

    protected int getInputSignal(Level var1, BlockPos var2, Direction var5) {
        int var4 = 0;
        BlockPos var6 = var2.relative(var5);
        BlockState var7 = var1.getBlockState(var6);
        if (var7.hasAnalogOutputSignal()) {
            return var7.getAnalogOutputSignal(var1, var6);
        }
        if (var7.isRedstoneConductor(var1, var6)) {
            var6 = var6.relative(var5);
            var7 = var1.getBlockState(var6);
            ItemFrame var8 = this.getItemFrame(var1, var5, var6);
            if(var8 != null){
                var1.scheduleTick(var2, var1.getBlockState(var2).getBlock(), 2);
                return var8.getAnalogOutput();
            }
            if(var7.hasAnalogOutputSignal()) {
                var1.scheduleTick(var2, var1.getBlockState(var2).getBlock(), 2);
                return var7.getAnalogOutputSignal(var1, var6);
            }
        }
        return var4;
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        if(AdventureProcedure.pinConfig(world, entity)){
            ItemStack itemStack = entity.getItemInHand(hand);
            if(!itemStack.isEmpty()){
                if(itemStack.is(RedstonecgModItems.ROTATION_BRACKET.get())){return super.use(blockstate, world, pos, entity, hand, hit);}
                if(itemStack.is(blockstate.getBlock().asItem()) && AdventureProcedure.pinConfig(world, entity)){
                    blockstate = blockstate.setValue(BASE_READ, !blockstate.getValue(BASE_READ));
                    world.scheduleTick(pos, blockstate.getBlock(), 1);
                    world.setBlock(pos, blockstate, 3);
                    return InteractionResult.SUCCESS;
                }
            }
            OnBlockRightClickedProcedure.execute(world, pos, blockstate);
            return InteractionResult.SUCCESS;
        }
        return super.use(blockstate, world, pos, entity, hand, hit);
    }

    @Override
    public int getConnection(BlockState bs) {
        return bs.getValue(CONNECTION);
    }

    @Override
    public int connectionFilter(int connection) {
        return CanConnectWallGateProcedure.To1_3GateConnectionFilter(connection);
    }
}
