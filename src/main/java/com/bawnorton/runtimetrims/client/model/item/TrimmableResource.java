package com.bawnorton.runtimetrims.client.model.item;

import net.minecraft.data.client.ModelIds;
import net.minecraft.item.Item;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public record TrimmableResource(Item item, Identifier resourceId, Resource resource) {
    public Identifier modelId() {
        return ModelIds.getItemModelId(item);
    }
}
