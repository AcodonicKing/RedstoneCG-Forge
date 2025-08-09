package net.acodonic_king.redstonecg.init;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.acodonic_king.redstonecg.RedstonecgMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = RedstonecgMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RedstonecgModMissingMappingsHandler {
    private static final Map<String, ResourceLocation> BLOCK_REMAP_TABLE = load_mapping("data/redstonecg/remap/block_remap.json");
    private static final Map<String, ResourceLocation> ITEM_REMAP_TABLE = load_mapping("data/redstonecg/remap/item_remap.json");

    @SubscribeEvent
    public static void onMissingMappings(MissingMappingsEvent event) {
        remap(event, ForgeRegistries.BLOCKS, "redstonecg", BLOCK_REMAP_TABLE);
        remap(event, ForgeRegistries.ITEMS, "redstonecg", ITEM_REMAP_TABLE);
    }

    private static Map<String, ResourceLocation> load_mapping(String directory){
        Map<String, ResourceLocation> map = new HashMap<>();
        try (InputStream stream = RedstonecgModMissingMappingsHandler.class.getClassLoader().getResourceAsStream(directory)) {
            if (stream != null) {
                JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    map.put(entry.getKey(), new ResourceLocation(entry.getValue().getAsString()));
                }
                RedstonecgMod.LOGGER.info("[Remap] Loaded {} containing {} remaps.", directory, map.size());
            } else {
                RedstonecgMod.LOGGER.error("[Remap] {} not found in resources.", directory);
            }
        } catch (Exception e) {
            RedstonecgMod.LOGGER.error("[Remap] Failed to load {}: {}", directory, e.getMessage());
        }
        return map;
    }
    private static <T> void remap(MissingMappingsEvent event, IForgeRegistry<T> registry, String modId, Map<String, ResourceLocation> map) {
        for (MissingMappingsEvent.Mapping<T> mapping : event.getMappings(registry.getRegistryKey(), modId)) {
            String oldPath = mapping.getKey().getPath();
            ResourceLocation newId = map.get(oldPath);
            if (newId != null) {
                T newObject = registry.getValue(newId);
                if (newObject != null) {
                    mapping.remap(newObject);
                    RedstonecgMod.LOGGER.info("[Remap] Remapped {} to {}", mapping.getKey(), newId);
                } else {
                    RedstonecgMod.LOGGER.error("[Remap] Could not find: {}", newId);
                }
            }
        }
    }

    /*private static final Map<String, ResourceLocation> REMAP_TABLE = new HashMap<>();
    static {
        try (InputStream stream = RedstonecgModMissingMappingsHandler.class.getClassLoader().getResourceAsStream("data/redstonecg/remap/block_remap.json")) {
            if (stream != null) {
                JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    REMAP_TABLE.put(entry.getKey(), new ResourceLocation(RedstonecgMod.MODID, entry.getValue().getAsString()));
                }
                RedstonecgMod.LOGGER.info("[Remap] Loaded " + REMAP_TABLE.size() + " block remaps.");
            } else {
                RedstonecgMod.LOGGER.info("[Remap] block_remap.json not found in resources.");
            }
        } catch (Exception e) {
            RedstonecgMod.LOGGER.info("[Remap] Failed to load block_remap.json: " + e.getMessage());
        }
    }
    @SubscribeEvent
    public static void onMissingMappings(MissingMappingsEvent event) {
        event.getMappings(ForgeRegistries.BLOCKS.getRegistryKey(), RedstonecgMod.MODID)
                .forEach(mapping -> {
                    String oldPath = mapping.getKey().getPath();
                    ResourceLocation newId = REMAP_TABLE.get(oldPath);

                    if (newId != null) {
                        Block newBlock = ForgeRegistries.BLOCKS.getValue(newId);
                        if (newBlock != null) {
                            mapping.remap(newBlock);
                            RedstonecgMod.LOGGER.info("[Remap] Remapped block: " + oldPath + " -> " + newId);
                        } else {
                            RedstonecgMod.LOGGER.info("[Remap] Target block not found: " + newId);
                        }
                    }
                }
        );
    }*/
}
