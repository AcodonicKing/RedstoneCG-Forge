package net.acodonic_king.redstonecg.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acodonic_king.redstonecg.block.defaults.DefaultGate;
import net.acodonic_king.redstonecg.procedures.RCGQuaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.acodonic_king.redstonecg.init.RedstonecgModBlockEntities;

public class AnalogSourceBlockEntity extends SuperBlockEntity {
	public int POWER = 0;
	public float ANGLE = 0;
	//public boolean BASE_OUT = true;
	public int[] POWER_RANGE = new int[]{0, 15};

	public AnalogSourceBlockEntity(BlockPos position, BlockState state) {
		super(RedstonecgModBlockEntities.ANALOG_SOURCE.get(), position, state);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putByte("power", (byte)POWER);
		tag.putInt("power_min", POWER_RANGE[0]);
		tag.putInt("power_max", POWER_RANGE[1]);
		//tag.putBoolean("base_read", BASE_OUT);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if(tag.contains("power")) POWER = tag.getByte("power");
		if(tag.contains("power_min")) POWER_RANGE[0] = tag.getInt("power_min");
		if(tag.contains("power_max")) POWER_RANGE[1] = tag.getInt("power_max");
		//if (tag.contains("base_read")) BASE_OUT = tag.getBoolean("base_read");
		setAngle();
    }

	public void setAngle(){
		ANGLE = (float) (POWER - POWER_RANGE[0]) / (POWER_RANGE[1] - POWER_RANGE[0] + 1);
		ANGLE = 0.5f - ANGLE;
		ANGLE *= (float) (2 * Math.PI);
	}

	public void setPower(int v){
		POWER = v;
		setAngle();
	}

	public static class AnalogSourceBlockEntityRenderer implements BlockEntityRenderer<AnalogSourceBlockEntity>{
		//public static final ModelResourceLocation BASE_OUT_MODEL = ArrowIndicatorBlockEntity.PINMARK_MODELS[4];
		public static final ModelResourceLocation KNOB_MODEL = new ModelResourceLocation(new ResourceLocation("redstonecg", "render_object_block"), "model=0");

		BlockEntityRendererProvider.Context context;
		public AnalogSourceBlockEntityRenderer(BlockEntityRendererProvider.Context context){
			super();
			this.context = context;
		}

		RCGQuaternion rotation = RCGQuaternion.Vector3F.rotateYP(0);
		byte facingMode = 0;
		RCGQuaternion facing = RCGQuaternion.Vector3F.rotateYP(0);

		@Override
		public void render(AnalogSourceBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
			ModelManager modelManager = Minecraft.getInstance().getModelManager();
			BakedModel model = modelManager.getModel(KNOB_MODEL);
			ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
			VertexConsumer vc = bufferSource.getBuffer(RenderType.cutoutMipped());

			BlockState blockState = blockEntity.getBlockState();
			if(blockState.getBlock() instanceof DefaultGate dg){
				rotation = OrientationHolderBlockEntity.getRotation(blockState.getValue(DefaultGate.ROTATION), blockEntity.ANGLE);
				Direction faced = blockState.getValue(DefaultGate.FACING);
				facing = OrientationHolderBlockEntity.getFacing(faced);
				facingMode = OrientationHolderBlockEntity.getFacingMode(faced);
			}
			poseStack.pushPose();
			poseStack.translate(0.5, 0.5, 0.5);
			OrientationHolderBlockEntity.setPoseStack(poseStack, facingMode, facing, rotation);

			modelRenderer.renderModel(
					poseStack.last(),
					vc,
					blockState,
					model,
					1.0f, 1.0f, 1.0f,
					packedLight,
					packedOverlay
			);

			/*if(blockEntity.BASE_OUT){
				model = Minecraft.getInstance().getModelManager().getModel(BASE_OUT_MODEL);
				modelRenderer.renderModel(
						poseStack.last(),
						vc,
						blockState,
						model,
						1.0f, 1.0f, 1.0f,
						packedLight,
						packedOverlay
				);
			}*/

			poseStack.popPose();
		}
	}
}
