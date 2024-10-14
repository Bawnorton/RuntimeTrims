package com.bawnorton.runtimetrims.registry;

import com.bawnorton.runtimetrims.registry.adapter.TrimMaterialRegistryAdapter;
import com.bawnorton.runtimetrims.util.KeylessAdaptable;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;

public class TrimMaterialRegistryInjector extends KeylessAdaptable<TrimMaterialRegistryAdapter> {
    public Map<Identifier, RegistryEntry<Item>> getNewMaterials(SimpleRegistry<ArmorTrimMaterial> trimMaterialRegistry) {
        Map<Identifier, RegistryEntry<Item>> newMaterials = new HashMap<>();
        getAdapters().forEach(adapter -> newMaterials.putAll(adapter.getNewMaterials(trimMaterialRegistry)));
        return newMaterials;
    }
}
