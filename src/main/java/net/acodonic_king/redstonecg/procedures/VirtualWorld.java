package net.acodonic_king.redstonecg.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class VirtualWorld implements LevelAccessor, BlockGetter {
    public Level level;
    public BlockPos source;

    public Map<BlockPos, BlockState> blockStates = new HashMap<>();
    public Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();

    public static VirtualWorld INSTANCE = new VirtualWorld();

    @Override
    public long nextSubTickCount() {
        return 0;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return null;
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return null;
    }

    @Override
    public LevelData getLevelData() {
        return null;
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos p_46800_) {
        return level.getCurrentDifficultyAt(source);
    }

    @Override
    public @Nullable MinecraftServer getServer() {
        return level.getServer();
    }

    @Override
    public ChunkSource getChunkSource() {
        return null;
    }

    @Override
    public RandomSource getRandom() {
        return level.getRandom();
    }

    @Override
    public void playSound(@Nullable Player player, BlockPos pos, SoundEvent soundEvent, SoundSource soundSource, float p_46779_, float p_46780_) {
        level.playSound(player, source, soundEvent, soundSource, p_46779_, p_46780_);
    }

    @Override
    public void addParticle(ParticleOptions p_46783_, double p_46784_, double p_46785_, double p_46786_, double p_46787_, double p_46788_, double p_46789_) {
        level.addParticle(p_46783_, p_46784_, p_46785_, p_46786_, p_46787_, p_46788_, p_46789_);
    }

    @Override
    public void levelEvent(@Nullable Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {

    }

    @Override
    public void gameEvent(GameEvent p_220404_, Vec3 p_220405_, GameEvent.Context p_220406_) {

    }

    @Override
    public float getShade(Direction p_45522_, boolean p_45523_) {
        return 0;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }

    @Override
    public WorldBorder getWorldBorder() {
        return null;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return blockEntities.get(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return blockStates.get(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return null;
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, AABB aabb, Predicate<? super Entity> predicate) {
        return level.getEntities(entity, aabb, predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entity, AABB aabb, Predicate<? super T> predicate) {
        return level.getEntities(entity, aabb, predicate);
    }

    @Override
    public List<? extends Player> players() {
        return level.players();
    }

    @Override
    public @Nullable ChunkAccess getChunk(int p_46823_, int p_46824_, ChunkStatus p_46825_, boolean p_46826_) {
        return null;
    }

    @Override
    public int getHeight(Heightmap.Types p_46827_, int p_46828_, int p_46829_) {
        return 0;
    }

    @Override
    public int getSkyDarken() {
        return 0;
    }

    @Override
    public BiomeManager getBiomeManager() {
        return null;
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int p_204159_, int p_204160_, int p_204161_) {
        return null;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public DimensionType dimensionType() {
        return null;
    }

    @Override
    public RegistryAccess registryAccess() {
        return null;
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return null;
    }

    @Override
    public boolean isStateAtPosition(BlockPos p_46938_, Predicate<BlockState> p_46939_) {
        return false;
    }

    @Override
    public boolean isFluidAtPosition(BlockPos p_151584_, Predicate<FluidState> p_151585_) {
        return false;
    }

    @Override
    public boolean setBlock(BlockPos p_46947_, BlockState p_46948_, int p_46949_, int p_46950_) {
        return false;
    }

    @Override
    public boolean removeBlock(BlockPos p_46951_, boolean p_46952_) {
        return false;
    }

    @Override
    public boolean destroyBlock(BlockPos p_46957_, boolean p_46958_, @Nullable Entity p_46959_, int p_46960_) {
        return false;
    }
}
