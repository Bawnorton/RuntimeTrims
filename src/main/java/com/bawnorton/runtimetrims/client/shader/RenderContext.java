package com.bawnorton.runtimetrims.client.shader;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

public record RenderContext(Entity entity, Item trimmed) {
}