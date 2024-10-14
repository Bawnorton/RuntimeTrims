package com.bawnorton.runtimetrims.tag;

import com.bawnorton.runtimetrims.tag.adapter.TrimmableArmourTagAdapater;
import com.bawnorton.runtimetrims.util.KeylessAdaptable;
import net.minecraft.item.Equipment;
import java.util.HashSet;
import java.util.Set;

public class TrimmableArmourTagInjector extends KeylessAdaptable<TrimmableArmourTagAdapater> {
    public Set<Equipment> getTrimmableArmour() {
        Set<Equipment> newEquipment = new HashSet<>();
        getAdapters().forEach(adapter -> newEquipment.addAll(adapter.getTrimmableArmour()));
        return newEquipment;
    }
}
