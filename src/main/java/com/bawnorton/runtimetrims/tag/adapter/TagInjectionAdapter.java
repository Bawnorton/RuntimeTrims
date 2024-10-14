package com.bawnorton.runtimetrims.tag.adapter;

import net.minecraft.item.Item;
import java.util.Set;

public abstract class TagInjectionAdapter {
    public abstract Set<Item> getTrimmableArmour();
    public abstract Set<Item> getTrimMaterials();
}
