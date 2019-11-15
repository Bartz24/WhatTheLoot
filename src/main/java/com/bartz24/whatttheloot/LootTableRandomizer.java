package com.bartz24.whatttheloot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LootTableRandomizer {
    private HashMap<ResourceLocation, LootTable> remappedTables;
    private HashMap<ResourceLocation, LootTable> oldTables;

    public LootTableRandomizer() {
        this.remappedTables = new HashMap<>();
        this.oldTables = new HashMap<>();
    }

    public void randomize(World world, LootTableManager manager) {
        Random rand = new Random(world.getSeed());
        List<ResourceLocation> keys = new ArrayList<>(manager.getLootTableKeys());
        List<ResourceLocation> keysLeft = new ArrayList<>(manager.getLootTableKeys());
        if (this.oldTables.size() == 0) {
            keys.forEach(key -> this.oldTables.put(key, manager.getLootTableFromLocation(key)));
        }
        keys.forEach(key -> this.remappedTables.put(key, this.oldTables.get(keysLeft.remove(rand.nextInt(keysLeft.size())))));
        manager.registeredLootTables = remappedTables;
    }

    public LootTable getNewLootTable(ResourceLocation resourceLocation) {
        return this.remappedTables.get(resourceLocation);
    }

    public LootTable getOriginalLootTable(ResourceLocation resourceLocation) {
        return this.oldTables.get(resourceLocation);
    }
}
