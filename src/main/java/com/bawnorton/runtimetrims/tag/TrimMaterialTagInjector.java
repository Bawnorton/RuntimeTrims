package com.bawnorton.runtimetrims.tag;

import com.bawnorton.runtimetrims.tag.adapter.TrimMaterialTagAdapter;
import com.bawnorton.runtimetrims.util.KeylessAdaptable;
import net.minecraft.item.Item;
import java.util.HashSet;
import java.util.Set;

public class TrimMaterialTagInjector extends KeylessAdaptable<TrimMaterialTagAdapter> {
    public Set<Item> getTrimMaterials() {
        Set<Item> newMaterials = new HashSet<>();
        getAdapters().forEach(adapter -> newMaterials.addAll(adapter.getTrimMaterials()));
        return newMaterials;
    }
}
