package net.acodonic_king.redstonecg.network;

import net.acodonic_king.redstonecg.RedstonecgMod;
import net.acodonic_king.redstonecg.init.RedstonecgModNetworking;
import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class RedstonecgModVariables {

	@Mod.EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			Level level = RedstonecgModVersionRides.getPlayerLevel(event.getEntity());
			if (level.isClientSide()) {
				SavedData mapdata = MapVariables.get(level);
				SavedData worlddata = WorldVariables.get(level);
				if (mapdata != null)
					RedstonecgModNetworking.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(0, mapdata));
				if (worlddata != null)
					RedstonecgModNetworking.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			Level level = RedstonecgModVersionRides.getPlayerLevel(event.getEntity());
			if (level.isClientSide()) {
				SavedData worlddata = WorldVariables.get(level);
				if (worlddata != null)
					RedstonecgModNetworking.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "redstonecg_worldvars";

		public static WorldVariables load(CompoundTag tag) {
			WorldVariables data = new WorldVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level level && !level.isClientSide())
				RedstonecgModNetworking.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(level::dimension), new SavedDataSyncMessage(1, this));
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(e -> WorldVariables.load(e), WorldVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "redstonecg_mapvars";
		public boolean enableredcuwireautoconnect = false;
		public boolean canSurviveAnyCase = false;
		public boolean adventurePinConfig = false;
		public boolean adventureValueConfig = false;
		public boolean adventureSurvival = false;
		public boolean adventureGateGUI = false;
		public int hangingRedCuWireMaxDistance = 16;
		public int gateChainLimit = 1024;
		public int wireChainLimit = 1024;

		public static MapVariables load(CompoundTag tag) {
			MapVariables data = new MapVariables();
			data.read(tag);
			return data;
		}

		public boolean readBoolean(CompoundTag nbt, String var, boolean def){
			if(nbt.contains(var))
				return nbt.getBoolean(var);
			return def;
		}

		public int readInt(CompoundTag nbt, String var, int def){
			if(nbt.contains(var))
				return nbt.getInt(var);
			return def;
		}

		public void read(CompoundTag nbt) {
			enableredcuwireautoconnect = readBoolean(nbt, "enableredcuwireautoconnect", false);
			canSurviveAnyCase = readBoolean(nbt,"can_survive", false);
			adventurePinConfig = readBoolean(nbt,"adventure_pin_config", false);
			adventureValueConfig = readBoolean(nbt,"adventure_value_config", false);
			adventureSurvival = readBoolean(nbt,"adventure_survival", false);
			adventureGateGUI = readBoolean(nbt,"adventure_gate_gui", false);
			hangingRedCuWireMaxDistance = readInt(nbt, "hanging_redcu_wire_max_distance", 16);
			gateChainLimit = readInt(nbt, "gate_chain_limit", 1024);
			wireChainLimit = readInt(nbt, "wire_chain_limit", 1024);
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			nbt.putBoolean("enableredcuwireautoconnect", enableredcuwireautoconnect);
			nbt.putBoolean("can_survive", canSurviveAnyCase);
			nbt.putBoolean("adventure_pin_config", adventurePinConfig);
			nbt.putBoolean("adventure_value_config", adventureValueConfig);
			nbt.putBoolean("adventure_survival", adventureSurvival);
			nbt.putBoolean("adventure_gate_gui", adventureGateGUI);
			nbt.putInt("hanging_redcu_wire_max_distance", hangingRedCuWireMaxDistance);
			nbt.putInt("gate_chain_limit", gateChainLimit);
			nbt.putInt("wire_chain_limit", wireChainLimit);
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level && !world.isClientSide())
				RedstonecgModNetworking.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SavedDataSyncMessage(0, this));
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc
						.getLevel()
						.getServer()
						.getLevel(Level.OVERWORLD)
						.getDataStorage()
						.computeIfAbsent(e -> MapVariables.load(e), MapVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class SavedDataSyncMessage {
		private final int type;
		private SavedData data;

		public SavedDataSyncMessage(FriendlyByteBuf buffer) {
			this.type = buffer.readInt();
			CompoundTag nbt = buffer.readNbt();
			if (nbt != null) {
				this.data = this.type == 0 ? new MapVariables() : new WorldVariables();
				if (this.data instanceof MapVariables mapVariables)
					mapVariables.read(nbt);
				else if (this.data instanceof WorldVariables worldVariables)
					worldVariables.read(nbt);
			}
		}

		public SavedDataSyncMessage(int type, SavedData data) {
			this.type = type;
			this.data = data;
		}

		public static void buffer(SavedDataSyncMessage message, FriendlyByteBuf buffer) {
			buffer.writeInt(message.type);
			if (message.data != null)
				buffer.writeNbt(message.data.save(new CompoundTag()));
		}

		public static void handleData(SavedDataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer() && message.data != null) {
					if (message.type == 0)
						MapVariables.clientSide = (MapVariables) message.data;
					else
						WorldVariables.clientSide = (WorldVariables) message.data;
				}
			});
			context.setPacketHandled(true);
		}
	}
}
