package net.acodonic_king.redstonecg.procedures;

import net.acodonic_king.redstonecg.init.RedstonecgModVersionRides;
import net.acodonic_king.redstonecg.network.RedstonecgModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

public class AdventureProcedure {
    /**
     * Returns true if player can configure pins.
     * If in adventure mode and pinConfig is false, then player cannot configure pins.
     * If pinConfig is true OR not in adventure mode, then player can configure pins.
     * @param world
     * @param player
     * @return
     */
    public static boolean pinConfig(Level world, Player player){
        if(!RedstonecgModVariables.MapVariables.get(world).adventurePinConfig){
            return !onAdventure(world, player);
        }
        return true;
    }
    /**
     * Returns true if player can configure values.
     * If in adventure mode and valueConfig is false, then player cannot configure values.
     * If valueConfig is true OR not in adventure mode, then player can configure values.
     * @param world
     * @param player
     * @return
     */
    public static boolean valueConfig(Level world, Player player){
        if(!RedstonecgModVariables.MapVariables.get(world).adventureValueConfig){
            return !onAdventure(world, player);
        }
        return true;
    }

    /**
     * Returns true if player can open gate GUI.
     * If in adventure mode and valueConfig is false, then player cannot open gate GUI.
     * If gateGUI is true OR not in adventure mode, then player can open gate GUI.
     * @param world
     * @param player
     * @return
     */
    public static boolean gateGUI(Level world, Player player){
        if(!RedstonecgModVariables.MapVariables.get(world).adventureGateGUI){
            return !onAdventure(world, player);
        }
        return true;
    }
    public static boolean onAdventure(Level world, Player player){
        GameType gameMode = getGameMode(player);
        boolean isAdventure = gameMode == GameType.ADVENTURE;
        if(RedstonecgModVariables.MapVariables.get(world).adventureSurvival)
            isAdventure |= gameMode == GameType.SURVIVAL;
        return isAdventure;
    }
    public static GameType getGameMode(Entity entity) {
        if (entity instanceof ServerPlayer _serverPlayer) {
            return _serverPlayer.gameMode.getGameModeForPlayer();
        } else if (RedstonecgModVersionRides.getPlayerLevel(entity).isClientSide() && entity instanceof Player _player) {
            PlayerInfo playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId());
            if (playerInfo != null) {
                return playerInfo.getGameMode();
            }
        }
        return null;
    }
}
