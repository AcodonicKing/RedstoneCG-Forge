package net.acodonic_king.redstonecg.init;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class RedstonecgModTags {
    public static class Blocks {
        public static final TagKey<Block> ANALOG_OUTPUT = tag("analog_output");
        public static final TagKey<Block> BOOLEAN_OUTPUT = tag("boolean_output");
        public static final TagKey<Block> NORMAL_NEIGHBOR_REDSTONE_UPDATE = tag("normal_neighbor_redstone_update");
        public static final TagKey<Block> PARALLEL_GATES = tag("parallel_gates");
        public static final TagKey<Block> PARALLEL_PROPAGATION_OPPOSITE = tag("parallel_propagation_opposite");
        public static final TagKey<Block> REDCUWIRES = tag("redcuwires");
        public static final TagKey<Block> AIRS = tag("airs");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(RedstonecgMod.MODID, name));
        }
    }
}
