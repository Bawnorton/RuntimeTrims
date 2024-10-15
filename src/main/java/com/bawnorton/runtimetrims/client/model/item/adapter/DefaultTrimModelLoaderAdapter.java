package com.bawnorton.runtimetrims.client.model.item.adapter;

import com.bawnorton.runtimetrims.RuntimeTrims;
import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.model.item.JsonParser;
import com.bawnorton.runtimetrims.client.model.item.TrimModelPredicate;
import com.bawnorton.runtimetrims.client.model.item.TrimmableResource;
import com.bawnorton.runtimetrims.client.model.item.json.ModelOverride;
import com.bawnorton.runtimetrims.client.model.item.json.TrimmableItemModel;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import java.util.Map;
import java.util.function.BiFunction;

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

    @Override
    public Map<Identifier, TrimmableItemModel> supplyOverrides(JsonParser jsonParser, TrimmableItemModel itemModel, TrimmableResource resource, BiFunction<TrimmableItemModel, TrimmableResource, TrimmableItemModel> overrideCreator) {
        Identifier modelId = resource.modelId().withSuffixedPath("_%s_trim".formatted(RuntimeTrims.DYNAMIC));

        if(RuntimeTrimsClient.overrideExisting) {
            itemModel.overrides.forEach(modelOverride -> modelOverride.model = modelId.toString());
        }

        itemModel.addOverride(ModelOverride.builder()
                .withModel(modelId.toString())
                .withPredicate(jsonParser.toJsonObject(TrimModelPredicate.of(RuntimeTrims.MATERIAL_MODEL_INDEX)))
                .build());

        return Map.of(modelId, overrideCreator.apply(itemModel, resource));
    }

    protected String getEquipmentType(Equipment equipment) {
        return switch (equipment.getSlotType()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> null;
        };
    }
}
