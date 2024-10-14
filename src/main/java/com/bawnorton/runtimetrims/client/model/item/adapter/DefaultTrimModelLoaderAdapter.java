package com.bawnorton.runtimetrims.client.model.item.adapter;

import com.bawnorton.runtimetrims.RuntimeTrims;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;

public class DefaultTrimModelLoaderAdapter extends TrimModelLoaderAdapter {
    @Override
    public boolean canTrim(Item item) {
        return item instanceof Equipment equipment && !(equipment instanceof AnimalArmorItem) && equipment.getSlotType().isArmorSlot();
    }

    @Override
    public Integer getLayerCount(Item item) {
        return 4;
    }

    @Override
    public String getLayerName(Item item, int layerIndex) {
        return "minecraft:trims/items/%s_trim_%d_%s".formatted(
                getEquipmentType((Equipment) item),
                layerIndex,
                RuntimeTrims.DYNAMIC
        );
    }

    private String getEquipmentType(Equipment equipment) {
        return switch (equipment.getSlotType()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> null;
        };
    }
}
