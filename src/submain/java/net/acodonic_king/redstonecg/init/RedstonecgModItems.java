package net.acodonic_king.redstonecg.init;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.item.RedCuIngotItem;
import net.acodonic_king.redstonecg.item.RedCuMeterItem;
import net.acodonic_king.redstonecg.item.RedCuMixtureItem;
import net.acodonic_king.redstonecg.item.RotationBracket;
import net.acodonic_king.redstonecg.procedures.RedCuMeterMeasuringProcedure;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RedstonecgModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, RedstonecgMod.MODID);

	public static final RegistryObject<Item> RED_CU_METER = item("redcu_meter", RedCuMeterItem::new);
	public static final RegistryObject<Item> RED_CU_MIXTURE = item("redcu_mixture", RedCuMixtureItem::new);
	public static final RegistryObject<Item> RED_CU_INGOT = item("redcu_ingot", RedCuIngotItem::new);
	public static final RegistryObject<Item> ROTATION_BRACKET = item("rotation_bracket", RotationBracket::new);
	public static final RegistryObject<Item> RED_CU_CRAFTER = block(RedstonecgModBlocks.REDCU_CRAFTER);
	public static final RegistryObject<Item> SMOOTH_STONE_PLATE = block(RedstonecgModBlocks.SMOOTH_STONE_PLATE);

	//==== Floor ====
	//Digital
	public static final RegistryObject<Item> NORMAL_AND = block(RedstonecgModBlocks.NORMAL_AND);
	public static final RegistryObject<Item> NORMAL_OR = block(RedstonecgModBlocks.NORMAL_OR);
	public static final RegistryObject<Item> NORMAL_XOR = block(RedstonecgModBlocks.NORMAL_XOR);
	public static final RegistryObject<Item> NORMAL_NAND = block(RedstonecgModBlocks.NORMAL_NAND);
	public static final RegistryObject<Item> NORMAL_NOR = block(RedstonecgModBlocks.NORMAL_NOR);
	public static final RegistryObject<Item> NORMAL_NXOR = block(RedstonecgModBlocks.NORMAL_NXOR);
	public static final RegistryObject<Item> NORMAL_BUF = block(RedstonecgModBlocks.NORMAL_BUF);
	public static final RegistryObject<Item> NORMAL_NOT = block(RedstonecgModBlocks.NORMAL_NOT);
	public static final RegistryObject<Item> NORMAL_SR_LATCH = block(RedstonecgModBlocks.NORMAL_SR_LATCH);
	public static final RegistryObject<Item> NORMAL_D_LATCH = block(RedstonecgModBlocks.NORMAL_D_LATCH);
	public static final RegistryObject<Item> NORMAL_T_TRIGGER = block(RedstonecgModBlocks.NORMAL_T_TRIGGER);
	public static final RegistryObject<Item> NORMAL_JK_TRIGGER = block(RedstonecgModBlocks.NORMAL_JK_TRIGGER);
	//Analog
	public static final RegistryObject<Item> NORMAL_ANALOG_SOURCE = block(RedstonecgModBlocks.NORMAL_ANALOG_SOURCE);
	public static final RegistryObject<Item> NORMAL_ANALOG_BIAS = block(RedstonecgModBlocks.NORMAL_ANALOG_BIAS);
	public static final RegistryObject<Item> NORMAL_ANALOG_GAIN = block(RedstonecgModBlocks.NORMAL_ANALOG_GAIN);
	public static final RegistryObject<Item> NORMAL_ANALOG_PASS = block(RedstonecgModBlocks.NORMAL_ANALOG_PASS);
	public static final RegistryObject<Item> NORMAL_ANALOG_MEMORY = block(RedstonecgModBlocks.NORMAL_ANALOG_MEMORY);
	public static final RegistryObject<Item> NORMAL_ANALOG_SUBTRACTOR = block(RedstonecgModBlocks.NORMAL_ANALOG_SUBTRACTOR);
	public static final RegistryObject<Item> NORMAL_ANALOG_DIFFERENTIAL = block(RedstonecgModBlocks.NORMAL_ANALOG_DIFFERENTIAL);
	public static final RegistryObject<Item> NORMAL_ANALOG_MAX = block(RedstonecgModBlocks.NORMAL_ANALOG_MAX);
	public static final RegistryObject<Item> NORMAL_ANALOG_MIN = block(RedstonecgModBlocks.NORMAL_ANALOG_MIN);
	//Hybrid
	public static final RegistryObject<Item> NORMAL_COMPARATOR = block(RedstonecgModBlocks.NORMAL_COMPARATOR);
	public static final RegistryObject<Item> NORMAL_N_COMPARATOR = block(RedstonecgModBlocks.NORMAL_N_COMPARATOR);
	public static final RegistryObject<Item> NORMAL_FORWARD_PATH_SELECTOR = block(RedstonecgModBlocks.NORMAL_FORWARD_PATH_SELECTOR);
	public static final RegistryObject<Item> NORMAL_REVERSED_PATH_SELECTOR = block(RedstonecgModBlocks.NORMAL_REVERSED_PATH_SELECTOR);
	public static final RegistryObject<Item> NORMAL_ONE_WAY_THROUGH_GATE = block(RedstonecgModBlocks.NORMAL_ONE_WAY_THROUGH_GATE);
	public static final RegistryObject<Item> NORMAL_ONE_WAY_THROUGH_NOT_GATE = block(RedstonecgModBlocks.NORMAL_ONE_WAY_THROUGH_NOT_GATE);
	public static final RegistryObject<Item> NORMAL_DELAYER = block(RedstonecgModBlocks.NORMAL_DELAYER);
	public static final RegistryObject<Item> NORMAL_BLOCK_READER = block(RedstonecgModBlocks.NORMAL_BLOCK_READER);
	//Indicator
	public static final RegistryObject<Item> UNIVERSAL_INDICATOR = block(RedstonecgModBlocks.UNIVERSAL_INDICATOR);
	public static final RegistryObject<Item> SEVEN_SEGMENT_INDICATOR = block(RedstonecgModBlocks.SEVEN_SEGMENT_INDICATOR);
	public static final RegistryObject<Item> CLOCK_FILLING_INDICATOR = block(RedstonecgModBlocks.CLOCK_FILLING_INDICATOR);
	public static final RegistryObject<Item> ORB_INDICATOR = block(RedstonecgModBlocks.ORB_INDICATOR);
	//Parallel Digital
	public static final RegistryObject<Item> PARALLEL_AND = block(RedstonecgModBlocks.PARALLEL_AND);
	public static final RegistryObject<Item> PARALLEL_OR = block(RedstonecgModBlocks.PARALLEL_OR);
	public static final RegistryObject<Item> PARALLEL_XOR = block(RedstonecgModBlocks.PARALLEL_XOR);
	public static final RegistryObject<Item> PARALLEL_NAND = block(RedstonecgModBlocks.PARALLEL_NAND);
	public static final RegistryObject<Item> PARALLEL_NOR = block(RedstonecgModBlocks.PARALLEL_NOR);
	public static final RegistryObject<Item> PARALLEL_NXOR = block(RedstonecgModBlocks.PARALLEL_NXOR);
	public static final RegistryObject<Item> PARALLEL_SR_LATCH = block(RedstonecgModBlocks.PARALLEL_SR_LATCH);
	public static final RegistryObject<Item> PARALLEL_D_LATCH = block(RedstonecgModBlocks.PARALLEL_D_LATCH);
	//Parallel Analog
	public static final RegistryObject<Item> PARALLEL_ANALOG_BIAS = block(RedstonecgModBlocks.PARALLEL_ANALOG_BIAS);
	public static final RegistryObject<Item> PARALLEL_ANALOG_GAIN = block(RedstonecgModBlocks.PARALLEL_ANALOG_GAIN);
	public static final RegistryObject<Item> PARALLEL_ANALOG_PASS = block(RedstonecgModBlocks.PARALLEL_ANALOG_PASS);
	public static final RegistryObject<Item> PARALLEL_ANALOG_DIFFERENTIAL = block(RedstonecgModBlocks.PARALLEL_ANALOG_DIFFERENTIAL);
	public static final RegistryObject<Item> PARALLEL_ANALOG_MEMORY = block(RedstonecgModBlocks.PARALLEL_ANALOG_MEMORY);
	public static final RegistryObject<Item> PARALLEL_ANALOG_SUBTRACTOR = block(RedstonecgModBlocks.PARALLEL_ANALOG_SUBTRACTOR);
	public static final RegistryObject<Item> PARALLEL_ANALOG_MAX = block(RedstonecgModBlocks.PARALLEL_ANALOG_MAX);
	public static final RegistryObject<Item> PARALLEL_ANALOG_MIN = block(RedstonecgModBlocks.PARALLEL_ANALOG_MIN);
	//Parallel Hybrid
	public static final RegistryObject<Item> PARALLEL_COMPARATOR = block(RedstonecgModBlocks.PARALLEL_COMPARATOR);
	public static final RegistryObject<Item> PARALLEL_N_COMPARATOR = block(RedstonecgModBlocks.PARALLEL_N_COMPARATOR);
	public static final RegistryObject<Item> PARALLEL_ONE_WAY_PATH_SELECTOR = block(RedstonecgModBlocks.PARALLEL_ONE_WAY_PATH_SELECTOR);
	public static final RegistryObject<Item> PARALLEL_ONE_WAY_THROUGH_GATE = block(RedstonecgModBlocks.PARALLEL_ONE_WAY_THROUGH_GATE);
	public static final RegistryObject<Item> PARALLEL_ONE_WAY_THROUGH_NOT_GATE = block(RedstonecgModBlocks.PARALLEL_ONE_WAY_THROUGH_NOT_GATE);
	public static final RegistryObject<Item> PARALLEL_DELAYER = block(RedstonecgModBlocks.PARALLEL_DELAYER);
	//Wire
	public static final RegistryObject<Item> REDCU_WIRE = block(RedstonecgModBlocks.REDCU_WIRE);
	public static final RegistryObject<Item> REDSTONE_TO_REDCU_CONVERTER = block(RedstonecgModBlocks.REDSTONE_TO_REDCU_CONVERTER);
	public static final RegistryObject<Item> REDCU_WIRE_INTERSECTION = block(RedstonecgModBlocks.REDCU_WIRE_INTERSECTION);
	public static final RegistryObject<Item> PARALLEL_LINE_OUTPUT = block(RedstonecgModBlocks.PARALLEL_LINE_OUTPUT);
	public static final RegistryObject<Item> REDCU_WIRE_TRANSITION = block(RedstonecgModBlocks.REDCU_WIRE_TRANSITION);



	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> RedstonecgModVersionRides.createBlockItem(block));
	}

	private static RegistryObject<Item> item(String reg, Supplier<? extends Item> sup) {
		return REGISTRY.register(reg, sup);
	}

	@SubscribeEvent
	public static void clientLoad(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemProperties.register(RED_CU_METER.get(), new ResourceLocation("redstonecg:red_cu_meter_measuring"), (itemStackToRender, clientWorld, entity, itemEntityId) -> RedCuMeterMeasuringProcedure.GetTextureID(itemStackToRender));
		});
	}
}
