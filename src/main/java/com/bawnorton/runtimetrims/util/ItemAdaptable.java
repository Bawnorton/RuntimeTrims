package com.bawnorton.runtimetrims.util;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ItemAdaptable<T> extends DefaultedAdaptable<Identifier, T> {
    public void registerAdapter(T adapter, List<Item> items) {
        registerAdapter(adapter, items.stream().map(Registries.ITEM::getId).collect(Collectors.toSet()));
    }

    protected boolean hasAdapter(Item item) {
        return hasAdapter(Registries.ITEM.getId(item));
    }

    protected T getAdapter(Item item) {
        return getAdapter(Registries.ITEM.getId(item));
    }
}
