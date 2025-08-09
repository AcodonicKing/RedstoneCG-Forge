package net.acodonic_king.redstonecg.procedures;

import net.minecraft.world.item.context.BlockPlaceContext;

public class LocalThreadValueHolders {
    public static class BlockPlaceContextHolder {
        private static final ThreadLocal<BlockPlaceContext> CONTEXT = new ThreadLocal<>();
        public static void set(BlockPlaceContext context) {CONTEXT.set(context);}
        public static BlockPlaceContext get() {return CONTEXT.get();}
        public static void clear() {CONTEXT.remove();}
    }
}
