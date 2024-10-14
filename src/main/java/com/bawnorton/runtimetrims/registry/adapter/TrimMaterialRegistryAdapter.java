package com.bawnorton.runtimetrims.registry.adapter;

import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import java.util.Map;

public abstract class TrimMaterialRegistryAdapter {
    public abstract Map<Identifier, RegistryEntry<Item>> getNewMaterials(SimpleRegistry<ArmorTrimMaterial> trimMaterialRegistry);
}
