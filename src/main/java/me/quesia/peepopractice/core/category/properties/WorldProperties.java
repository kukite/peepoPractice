package me.quesia.peepopractice.core.category.properties;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public class WorldProperties extends BaseProperties {
    private RegistryKey<World> worldRegistryKey;
    private boolean spawnChunksEnabled = true;
    private final Map<Biome, @Nullable Integer> antiBiomeRangeMap = new HashMap<>();

    public RegistryKey<World> getWorldRegistryKey() {
        return this.worldRegistryKey;
    }

    public boolean hasWorldRegistryKey() {
        return this.worldRegistryKey != null;
    }

    public WorldProperties setWorldRegistryKey(RegistryKey<World> worldRegistryKey) {
        this.worldRegistryKey = worldRegistryKey;
        return this;
    }

    public boolean isSpawnChunksEnabled() {
        return this.spawnChunksEnabled;
    }

    public WorldProperties setSpawnChunksEnabled(boolean spawnChunksEnabled) {
        this.spawnChunksEnabled = spawnChunksEnabled;
        return this;
    }

    public Map<Biome, Integer> getAntiBiomeRangeMap() {
        return this.antiBiomeRangeMap;
    }

    public WorldProperties addAntiBiomeRange(Biome biome, @Nullable Integer range) {
        this.antiBiomeRangeMap.put(biome, range);
        return this;
    }
}
