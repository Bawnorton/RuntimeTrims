package com.bawnorton.runtimetrims.client.model.item.adapter;

import com.bawnorton.runtimetrims.client.model.item.JsonParser;
import com.bawnorton.runtimetrims.client.model.item.TrimmableResource;
import com.bawnorton.runtimetrims.client.model.item.json.TrimmableItemModel;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class TrimModelLoaderAdapter {
    /**
     * Whether dynamic trims should be generated for the item
     */
    public abstract boolean canTrim(Item item);

    /**
     * How many colours does the blank trim texture for the item use.
     */
    public abstract Integer getLayerCount(Item item);

    /**
     * Where to find the <b>dynamic</b> trim texture for a given item and index.
     */
    public abstract String getLayerName(Item item, int layerIndex);

    /**
     * Add, modify or remove existing overrides
     */
    public abstract Map<Identifier, TrimmableItemModel> supplyOverrides(JsonParser jsonParser, TrimmableItemModel itemModel, TrimmableResource resource, BiFunction<TrimmableItemModel, TrimmableResource, TrimmableItemModel> overrideCreator);
}
