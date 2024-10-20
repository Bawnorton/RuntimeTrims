package com.bawnorton.runtimetrims.client.render;

import com.bawnorton.runtimetrims.RuntimeTrims;
import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.colour.ARGBColourHelper;
import com.bawnorton.runtimetrims.client.palette.TrimPalette;
import com.bawnorton.runtimetrims.client.palette.TrimPalettes;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.util.collection.IdList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.AnimalArmorItem;
import java.util.Map;


public final class ItemTrimColourProvider implements ItemColorProvider {
    private final TrimPalettes palettes;
    private final LayerData layerData;
    //? if fabric {
    /*private final IdList<ItemColorProvider> existingProviders;

    public ItemTrimColourProvider(TrimPalettes palettes, LayerData layerData, IdList<ItemColorProvider> existingProviders) {
    *///?} elif neoforge {
    private final Map<Item, ItemColorProvider> existingProviders;

    public ItemTrimColourProvider(TrimPalettes palettes, LayerData layerData, Map<Item, ItemColorProvider> existingProviders) {
    //?}
        this.palettes = palettes;
        this.layerData = layerData;
        this.existingProviders = existingProviders;
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        ArmorTrim trim = stack.getComponents().get(DataComponentTypes.TRIM);
        if(trim == null) return getExistingColor(stack, tintIndex);

        ArmorTrimMaterial material = trim.getMaterial().value();
        if(!(material.assetName().equals(RuntimeTrims.DYNAMIC) || RuntimeTrimsClient.overrideExisting)) return -1;

        int startLayer = layerData.getTrimStartLayer(stack.getItem());
        if(tintIndex < startLayer) return getExistingColor(stack, tintIndex);

        Item materialItem = material.ingredient().value();
        TrimPalette palette = palettes.getOrGeneratePalette(materialItem);

        return ARGBColourHelper.fullAlpha(palette.getColours().get(tintIndex - startLayer));
    }

    private int getExistingColor(ItemStack stack, int tintIndex) {
        //? if fabric {
        /*ItemColorProvider existingProvider = existingProviders.get(Item.getRawId(stack.getItem()));
        *///?} elif neoforge {
        ItemColorProvider existingProvider = existingProviders.get(stack.getItem());
        //?}
        if (existingProvider == null)
            return -1;

        return existingProvider.getColor(stack, tintIndex);
    }

    public Item[] getApplicableItems() {
        return Registries.ITEM.stream().filter(item -> {
            if(item instanceof Equipment equipment) {
                if(item instanceof AnimalArmorItem) return false;
                return equipment.getSlotType().isArmorSlot();
            }
            return false;
        }).toArray(Item[]::new);
    }
}