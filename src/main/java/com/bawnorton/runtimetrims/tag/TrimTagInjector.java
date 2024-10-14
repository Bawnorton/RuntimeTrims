package com.bawnorton.runtimetrims.tag;

import com.bawnorton.runtimetrims.tag.adapter.TagInjectionAdapter;
import com.bawnorton.runtimetrims.util.KeylessAdaptable;
import net.minecraft.item.Item;
import java.util.HashSet;
import java.util.Set;

public class TrimTagInjector extends KeylessAdaptable<TagInjectionAdapter> {
    public Set<Item> getTrimmableArmour() {
        Set<Item> newEquipment = new HashSet<>();
        getAdapters().forEach(adapter -> newEquipment.addAll(adapter.getTrimmableArmour()));
        return newEquipment;
    }

    public Set<Item> getTrimMaterials() {
        Set<Item> newMaterials = new HashSet<>();
        getAdapters().forEach(adapter -> newMaterials.addAll(adapter.getTrimMaterials()));
        return newMaterials;
    }
}
