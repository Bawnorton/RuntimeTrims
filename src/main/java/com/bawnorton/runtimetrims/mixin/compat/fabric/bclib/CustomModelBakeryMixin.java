package com.bawnorton.runtimetrims.mixin.compat.fabric.bclib;

//? if fabric {

import com.bawnorton.runtimetrims.RuntimeTrims;
import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.mixin.accessor.JsonUnbakedModelAccessor;
import com.bawnorton.runtimetrims.client.model.item.JsonParser;
import com.bawnorton.runtimetrims.client.model.item.adapter.TrimModelLoaderAdapter;
import com.bawnorton.runtimetrims.client.model.item.json.TextureLayers;
import com.bawnorton.runtimetrims.client.model.item.json.TrimmableItemModel;
import com.bawnorton.runtimetrims.util.mixin.ConditionalMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.betterx.bclib.client.models.CustomModelBakery;
import org.betterx.bclib.interfaces.ItemModelProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// If only there was a mod that needed a dedicated compat from every other mod
// BCLib:
@SuppressWarnings("UnusedMixin")
@Mixin(CustomModelBakery.class)
@ConditionalMixin("bclib")
public abstract class CustomModelBakeryMixin {
    @Unique
    private final JsonParser parser = new JsonParser();

    @WrapOperation(
            method = "addItemModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/betterx/bclib/interfaces/ItemModelProvider;getItemModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;"
            )
    )
    private JsonUnbakedModel addTrimsToBCLibModel(ItemModelProvider instance, Identifier id, Operation<JsonUnbakedModel> original) {
        JsonUnbakedModel model = original.call(instance, id);
        Item item = Registries.ITEM.get(id);
        TrimModelLoaderAdapter trimModelLoaderAdapter = RuntimeTrimsClient.getItemModelLoader().getDefaultAdapter();
        if(!trimModelLoaderAdapter.canTrim(item)) return model;

        model.getOverrides().add(
                new ModelOverride(id, List.of(
                        new ModelOverride.Condition(
                                Identifier.ofVanilla("trim_type"),
                                RuntimeTrims.MATERIAL_MODEL_INDEX
                        )
                ))
        );

        JsonUnbakedModelAccessor accessor = (JsonUnbakedModelAccessor) model;
        Map<String, Either<SpriteIdentifier, String>> textures = new HashMap<>(accessor.getTextureMap());
        int startLayer = textures.size();
        RuntimeTrimsClient.getLayerData().setTrimStartLayer(item, startLayer);

        Map<String, String> layers = new HashMap<>();
        for (Map.Entry<String, Either<SpriteIdentifier, String>> entry : textures.entrySet()) {
            String layer = entry.getKey();
            Either<SpriteIdentifier, String> location = entry.getValue();

            String layerLocation = location.map(spriteId -> spriteId.getTextureId().toString(), Function.identity());
            layers.put(layer, layerLocation);
        }

        int layerCount = trimModelLoaderAdapter.getLayerCount(item);
        Map<String, String> existingAndTrims = new HashMap<>(layers);
        for(int i = 0; i < layerCount; i++) {
            existingAndTrims.put("layer%s".formatted(i + startLayer), trimModelLoaderAdapter.getLayerName(item, i));
        }

        TrimmableItemModel itemModel = TrimmableItemModel.builder()
                .parent(accessor.getParentId())
                .textures(TextureLayers.of(existingAndTrims))
                .build();

        return JsonUnbakedModel.deserialize(parser.toJson(itemModel));
    }
}

//?}