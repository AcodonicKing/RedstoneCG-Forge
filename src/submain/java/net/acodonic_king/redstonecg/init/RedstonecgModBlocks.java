package net.acodonic_king.redstonecg.init;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.block.*;
import net.acodonic_king.redstonecg.block.normal.interaction.CodedControlPanelBlock;
import net.acodonic_king.redstonecg.block.normal.interaction.ControlPanelBlock;
import net.acodonic_king.redstonecg.block.normal.hybrid.*;
import net.acodonic_king.redstonecg.block.normal.analog.*;
import net.acodonic_king.redstonecg.block.normal.digital.*;
import net.acodonic_king.redstonecg.block.normal.indicator.*;
import net.acodonic_king.redstonecg.block.normal.interaction.RedButtonBlock;
import net.acodonic_king.redstonecg.block.normal.interaction.RedSwitchBlock;
import net.acodonic_king.redstonecg.block.normal.wire.*;
import net.acodonic_king.redstonecg.block.parallel.analog.*;
import net.acodonic_king.redstonecg.block.parallel.digital.*;
import net.acodonic_king.redstonecg.block.parallel.hybrid.*;
import net.acodonic_king.redstonecg.block.parallel.wire.ParallelLineOutputBlock;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

public class RedstonecgModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, RedstonecgMod.MODID);

	public static final RegistryObject<Block> SMOOTH_STONE_PLATE = REGISTRY.register("smooth_stone_plate", SmoothStonePlateBlock::new);
	public static final RegistryObject<Block> REDCU_CRAFTER = REGISTRY.register("redcu_crafter", RedCuCrafterBlock::new);

	//==== Normal ====
	//Digital
	public static final RegistryObject<Block> NORMAL_AND = REGISTRY.register("normal_and", AndBlock::new);
	public static final RegistryObject<Block> NORMAL_OR = REGISTRY.register("normal_or", OrBlock::new);
	public static final RegistryObject<Block> NORMAL_XOR = REGISTRY.register("normal_xor", XorBlock::new);
	public static final RegistryObject<Block> NORMAL_BUF = REGISTRY.register("normal_buf", BufBlock::new);
	public static final RegistryObject<Block> NORMAL_NAND = REGISTRY.register("normal_nand", NandBlock::new);
	public static final RegistryObject<Block> NORMAL_NOR = REGISTRY.register("normal_nor", NorBlock::new);
	public static final RegistryObject<Block> NORMAL_NXOR = REGISTRY.register("normal_nxor", NxorBlock::new);
	public static final RegistryObject<Block> NORMAL_NOT = REGISTRY.register("normal_not", NotBlock::new);
	public static final RegistryObject<Block> NORMAL_SR_LATCH = REGISTRY.register("normal_sr_latch", SRLatchBlock::new);
	public static final RegistryObject<Block> NORMAL_D_LATCH = REGISTRY.register("normal_d_latch", DLatchBlock::new);
	public static final RegistryObject<Block> NORMAL_T_TRIGGER = REGISTRY.register("normal_t_trigger", TTriggerBlock::new);
	public static final RegistryObject<Block> NORMAL_JK_TRIGGER = REGISTRY.register("normal_jk_trigger", JKTriggerBlock::new);
	//Analog
	public static final RegistryObject<Block> NORMAL_ANALOG_SOURCE = REGISTRY.register("normal_analog_source", AnalogSourceBlock::new);
	public static final RegistryObject<Block> NORMAL_ANALOG_BIAS = REGISTRY.register("normal_analog_bias", AnalogBiasBlock::new);
	public static final RegistryObject<Block> NORMAL_ANALOG_GAIN = REGISTRY.register("normal_analog_gain", AnalogGainBlock::new);
	public static final RegistryObject<Block> NORMAL_ANALOG_PASS = REGISTRY.register("normal_analog_pass", AnalogPassBlock::new);
	public static final RegistryObject<Block> NORMAL_ANALOG_MEMORY = REGISTRY.register("normal_analog_memory", AnalogMemoryBlock::new);
	public static final RegistryObject<Block> NORMAL_ANALOG_SUBTRACTOR = REGISTRY.register("normal_analog_subtractor", AnalogSubtractorBlock::new);
	public static final RegistryObject<Block> NORMAL_ANALOG_DIFFERENTIAL = REGISTRY.register("normal_analog_differential", AnalogDifferentialBlock::new);
	public static final RegistryObject<Block> NORMAL_ANALOG_MAX = REGISTRY.register("normal_analog_max", AnalogMaxBlock::new);
	public static final RegistryObject<Block> NORMAL_ANALOG_MIN = REGISTRY.register("normal_analog_min", AnalogMinBlock::new);
	//Hybrid
	public static final RegistryObject<Block> NORMAL_COMPARATOR = REGISTRY.register("normal_comparator", DComparatorBlock::new);
	public static final RegistryObject<Block> NORMAL_N_COMPARATOR = REGISTRY.register("normal_n_comparator", NComparatorBlock::new);
	public static final RegistryObject<Block> NORMAL_FORWARD_PATH_SELECTOR = REGISTRY.register("normal_forward_path_selector", ForwardPathSelectorBlock::new);
	public static final RegistryObject<Block> NORMAL_REVERSED_PATH_SELECTOR = REGISTRY.register("normal_reversed_path_selector", ReversedPathSelectorBlock::new);
	public static final RegistryObject<Block> NORMAL_ONE_WAY_THROUGH_GATE = REGISTRY.register("normal_one_way_through_gate", OneWayThroughGateBlock::new);
	public static final RegistryObject<Block> NORMAL_ONE_WAY_THROUGH_NOT_GATE = REGISTRY.register("normal_one_way_through_not_gate", OneWayThroughNotGateBlock::new);
	public static final RegistryObject<Block> NORMAL_DELAYER = REGISTRY.register("normal_delayer", DelayerBlock::new);
	public static final RegistryObject<Block> NORMAL_BLOCK_READER = REGISTRY.register("normal_block_reader", BlockReaderBlock::new);
	public static final RegistryObject<Block> NORMAL_BINARY_ENCODER = REGISTRY.register("normal_binary_encoder", BinaryEncoderBlock::new);
	public static final RegistryObject<Block> NORMAL_BINARY_DECODER = REGISTRY.register("normal_binary_decoder", BinaryDecoderBlock::new);
	//Indicator
	public static final RegistryObject<Block> UNIVERSAL_INDICATOR = REGISTRY.register("universal_indicator", UniversalIndicatorBlock::new);
	public static final RegistryObject<Block> SEVEN_SEGMENT_INDICATOR = REGISTRY.register("seven_segment_indicator", SevenSegmentIndicatorBlock::new);
	public static final RegistryObject<Block> CLOCK_FILLING_INDICATOR = REGISTRY.register("clock_filling_indicator", ClockFillingIndicatorBlock::new);
	public static final RegistryObject<Block> ORB_INDICATOR = REGISTRY.register("orb_indicator", OrbIndicatorBlock::new);
	public static final RegistryObject<Block> HEXADECIMAL_INDICATOR = REGISTRY.register("hexadecimal_indicator", HexadecimalIndicatorBlock::new);
	public static final RegistryObject<Block> FLAT_LAMP_INDICATOR = REGISTRY.register("flat_lamp_indicator", FlatLampIndicatorBlock::new);
	public static final RegistryObject<Block> COLORED_FLAT_LAMP_INDICATOR = REGISTRY.register("colored_flat_lamp_indicator", ColoredFlatLampIndicatorBlock::new);
	public static final RegistryObject<Block> COLORFUL_FLAT_LAMP_INDICATOR = REGISTRY.register("colorful_flat_lamp_indicator", ColorfulFlatLampIndicatorBlock::new);
	public static final RegistryObject<Block> ARROW_INDICATOR = REGISTRY.register("arrow_indicator", ArrowIndicatorBlock::new);
	public static final RegistryObject<Block> BAR_INDICATOR = REGISTRY.register("bar_indicator", BarIndicatorBlock::new);
	public static final RegistryObject<Block> COLORED_LAMP_BLOCK = REGISTRY.register("colored_lamp_indicator", ColoredLampBlock::new);
	public static final RegistryObject<Block> COLORFUL_LAMP_BLOCK = REGISTRY.register("colorful_lamp_indicator", ColorfulLampBlock::new);
	//Interaction
	public static final RegistryObject<Block> RED_SWITCH = REGISTRY.register("red_switch", RedSwitchBlock::new);
	public static final RegistryObject<Block> RED_BUTTON = REGISTRY.register("red_button", RedButtonBlock::new);
	public static final RegistryObject<Block> CONTROL_PANEL = REGISTRY.register("control_panel", ControlPanelBlock::new);
	public static final RegistryObject<Block> CODED_CONTROL_PANEL = REGISTRY.register("coded_control_panel", CodedControlPanelBlock::new);
	//Parallel Digital
	public static final RegistryObject<Block> PARALLEL_AND = REGISTRY.register("parallel_and", ParallelAndBlock::new);
	public static final RegistryObject<Block> PARALLEL_OR = REGISTRY.register("parallel_or", ParallelOrBlock::new);
	public static final RegistryObject<Block> PARALLEL_XOR = REGISTRY.register("parallel_xor", ParallelXorBlock::new);
	public static final RegistryObject<Block> PARALLEL_NAND = REGISTRY.register("parallel_nand", ParallelNandBlock::new);
	public static final RegistryObject<Block> PARALLEL_NOR = REGISTRY.register("parallel_nor", ParallelNorBlock::new);
	public static final RegistryObject<Block> PARALLEL_NXOR = REGISTRY.register("parallel_nxor", ParallelNxorBlock::new);
	public static final RegistryObject<Block> PARALLEL_SR_LATCH = REGISTRY.register("parallel_sr_latch", ParallelSRLatchBlock::new);
	public static final RegistryObject<Block> PARALLEL_D_LATCH = REGISTRY.register("parallel_d_latch", ParallelDLatchBlock::new);
	//Parallel Analog
	public static final RegistryObject<Block> PARALLEL_ANALOG_BIAS = REGISTRY.register("parallel_analog_bias", ParallelAnalogBiasBlock::new);
	public static final RegistryObject<Block> PARALLEL_ANALOG_GAIN = REGISTRY.register("parallel_analog_gain", ParallelAnalogGainBlock::new);
	public static final RegistryObject<Block> PARALLEL_ANALOG_PASS = REGISTRY.register("parallel_analog_pass", ParallelAnalogPassBlock::new);
	public static final RegistryObject<Block> PARALLEL_ANALOG_DIFFERENTIAL = REGISTRY.register("parallel_analog_differential", ParallelAnalogDifferentialBlock::new);
	public static final RegistryObject<Block> PARALLEL_ANALOG_MEMORY = REGISTRY.register("parallel_analog_memory", ParallelAnalogMemoryBlock::new);
	public static final RegistryObject<Block> PARALLEL_ANALOG_SUBTRACTOR = REGISTRY.register("parallel_analog_subtractor", ParallelAnalogSubtractorBlock::new);
	public static final RegistryObject<Block> PARALLEL_ANALOG_MAX = REGISTRY.register("parallel_analog_max", ParallelAnalogMaxBlock::new);
	public static final RegistryObject<Block> PARALLEL_ANALOG_MIN = REGISTRY.register("parallel_analog_min", ParallelAnalogMinBlock::new);
	//Parallel Hybrid
	public static final RegistryObject<Block> PARALLEL_COMPARATOR = REGISTRY.register("parallel_comparator", ParallelComparatorBlock::new);
	public static final RegistryObject<Block> PARALLEL_N_COMPARATOR = REGISTRY.register("parallel_n_comparator", ParallelNComparatorBlock::new);
	public static final RegistryObject<Block> PARALLEL_ONE_WAY_PATH_SELECTOR = REGISTRY.register("parallel_one_way_path_selector", ParallelPathSelectorBlock::new);
	public static final RegistryObject<Block> PARALLEL_ONE_WAY_THROUGH_GATE = REGISTRY.register("parallel_one_way_through_gate", ParallelOneWayThroughGateBlock::new);
	public static final RegistryObject<Block> PARALLEL_ONE_WAY_THROUGH_NOT_GATE = REGISTRY.register("parallel_one_way_through_not_gate", ParallelOneWayThroughNotGateBlock::new);
	public static final RegistryObject<Block> PARALLEL_DELAYER = REGISTRY.register("parallel_delayer", ParallelDelayerBlock::new);
	//Wire
	public static final RegistryObject<Block> REDCU_WIRE = REGISTRY.register("redcu_wire", RedCuWireBlock::new);
	public static final RegistryObject<Block> REDSTONE_TO_REDCU_CONVERTER = REGISTRY.register("redstone_to_redcu_converter", RedstoneToRedCuConverterBlock::new);
	public static final RegistryObject<Block> REDCU_WIRE_INTERSECTION = REGISTRY.register("redcu_wire_intersection", RedCuWireIntersectionBlock::new);
	public static final RegistryObject<Block> PARALLEL_LINE_OUTPUT = REGISTRY.register("parallel_line_output", ParallelLineOutputBlock::new);
	public static final RegistryObject<Block> REDCU_WIRE_TRANSITION = REGISTRY.register("redcu_wire_transition", RedCuWireTransitionBlock::new);
	public static final RegistryObject<Block> HANGING_REDCU_WIRE_CONNECTOR = REGISTRY.register("hanging_redcu_wire_connector", HangingRedCuWireConnectorBlock::new);

	public static final RegistryObject<Block> RENDER_OBJECT_BLOCK = REGISTRY.register("render_object_block", RenderObjectBlock::new);
}
