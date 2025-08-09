package net.acodonic_king.redstonecg.block.defaults;

import net.acodonic_king.redstonecg.block.entity.DefaultAnalogGateBlockEntity;
import net.acodonic_king.redstonecg.procedures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class DefaultParallelAnalogInteractableABGate extends DefaultParallelAnalogAGate {
    public static final IntegerProperty CONNECTION = IntegerProperty.create("connection",0,1);
    public DefaultParallelAnalogInteractableABGate(){
        super();
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION);
    }

    @Override
    public int onRedstoneUpdate(LevelAccessor world, BlockState thisState, BlockPos thisPos){
        int linePower = GetParallelSignalProcedure.getParallelLinePower(world, thisPos);
        ConnectionFace connectionFaceA = BlockFrameTransformUtils.getConnectionFace(thisState, Direction.SOUTH);
        int backPower = GetRedstoneSignalProcedure.execute(world, thisPos, connectionFaceA);
        int power = 0;
        if(thisState.getValue(CONNECTION) == 0){
            power = this.redstonePowerOperation(backPower, linePower);
        } else {
            power = this.redstonePowerOperation(linePower, backPower);
        }
        if(world.getBlockEntity(thisPos) instanceof DefaultAnalogGateBlockEntity be){
            if(be.POWER == power){ return 0; }
            be.POWER = power;
            be.setChanged();
            updateVisibleState((Level) world, thisPos, thisState, power);
            sendRedstoneUpdateInDirection(world, thisState.getBlock(), thisPos, connectionFaceA.FACE.getOpposite());
        }
        return 0;
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        InteractionResult interactionResult = super.use(blockstate, world, pos, entity, hand, hit);
        if(interactionResult == InteractionResult.FAIL){return interactionResult;}
        if(AdventureProcedure.pinConfig(world, entity)){
            OnBlockRightClickedProcedure.execute(world, pos, blockstate);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
