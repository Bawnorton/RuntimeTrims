package com.bawnorton.runtimetrims.client.model.item;

import com.bawnorton.runtimetrims.client.debug.Debugger;
import com.bawnorton.runtimetrims.client.model.item.adapter.TrimModelLoaderAdapter;
import com.bawnorton.runtimetrims.client.model.item.json.BlockAtlas;
import com.bawnorton.runtimetrims.client.model.item.json.ModelOverride;
import com.bawnorton.runtimetrims.client.model.item.json.TextureLayers;
import com.bawnorton.runtimetrims.client.model.item.json.TrimmableItemModel;
import com.bawnorton.runtimetrims.client.render.LayerData;
import com.bawnorton.runtimetrims.util.ItemAdaptable;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ItemTrimModelLoader extends ItemAdaptable<TrimModelLoaderAdapter> {
    private static final Pattern itemIdPattern = Pattern.compile("^models/item/(.+)?(?=.json).json$");
    private final JsonParser jsonParser;
    private final LayerData layerData;

    public ItemTrimModelLoader(LayerData layerData) {
        this.layerData = layerData;
        this.jsonParser = new JsonParser();
    }

    public Map<Identifier, Resource> loadModels(Map<Identifier, Resource> loadedModels) {
        Map<Identifier, Resource> extendedModels = new HashMap<>(loadedModels);
        List<TrimmableResource> trimmableResources = findTrimmableResources(loadedModels);
        for(TrimmableResource trimmableResource : trimmableResources) {
            TrimmableItemModel itemModel = jsonParser.fromResource(trimmableResource.resource(), TrimmableItemModel.class);
            if(itemModel == null) continue;

            if(itemModel.isComplex()) {
                Item item = trimmableResource.item();
                if (!hasAdapter(item) || !getAdapter(item).canTrim(item)) {
                    continue;
                }
            }

            if(itemModel.textures == null) itemModel.textures = TextureLayers.empty();
            if(itemModel.overrides == null) itemModel.overrides = new ArrayList<>();

            Map<Identifier, TrimmableItemModel> suppliedOverrides = getAdapter(trimmableResource.item()).supplyOverrides(jsonParser, itemModel, trimmableResource, this::createModelOverride);

            itemModel.overrides.sort(Comparator.<ModelOverride, Float>comparing(override -> {
                JsonObject predicate = override.predicate;
                if(predicate.has("trim_type")) {
                    return predicate.get("trim_type").getAsFloat();
                } else if (predicate.has("minecraft:trim_type")) {
                    return predicate.get("minecraft:trim_type").getAsFloat();
                }
                return 0f;
            }).thenComparing(override -> {
                JsonObject predicate = override.predicate;
                if (predicate.has("runtimetrims:trim_pattern")) {
                    return predicate.get("runtimetrims:trim_pattern").getAsFloat();
                }
                return 0f;
            }));

            Resource newResource = jsonParser.toResource(trimmableResource.resource().getPack(), itemModel);
            extendedModels.put(trimmableResource.resourceId(), newResource);

            Debugger.createJson("resources/%s".formatted(trimmableResource.resourceId()), newResource);
            suppliedOverrides.forEach((modelId, override) -> {
                Resource overrideResource = jsonParser.toResource(trimmableResource.resource().getPack(), override);
                Identifier overrideResourceId = modelId.withPrefixedPath("models/").withSuffixedPath(".json");
                extendedModels.put(overrideResourceId, overrideResource);

                Debugger.createJson("resources/%s".formatted(overrideResourceId), overrideResource);
            });
        }
        return extendedModels;
    }

    public void loadModels(Identifier id, Resource resource, BiConsumer<Identifier, Resource> loadedModelConsumer) {
        loadModels(Map.of(id, resource)).forEach(loadedModelConsumer);
    }

    /**
     * Generates a list of for valid trimmable item
     */
    private List<TrimmableResource> findTrimmableResources(Map<Identifier, Resource> loadedModels) {
        List<TrimmableResource> trimmableResources = new ArrayList<>();
        for (Identifier resourceId : loadedModels.keySet()) {
            String resourcePath = resourceId.getPath();
            Matcher matcher = itemIdPattern.matcher(resourcePath);
            if (!matcher.matches()) continue;

            String itemPath = matcher.group(1);
            Identifier itemId = Identifier.of(resourceId.getNamespace(), itemPath);
            Item item = Registries.ITEM.get(itemId);
            if (item == Items.AIR) continue;
            if (!getAdapter(item).canTrim(item)) continue;

            trimmableResources.add(new TrimmableResource(item, resourceId, loadedModels.get(resourceId)));
        }
        return trimmableResources;
    }

    private TrimmableItemModel createModelOverride(TrimmableItemModel overridenModel, TrimmableResource trimmableResource) {
        Map<String, String> layers = new HashMap<>(overridenModel.textures.layers);

        int startLayer = layers.size();
        Item trimmable = trimmableResource.item();
        layerData.setTrimStartLayer(trimmable, startLayer);

        TrimModelLoaderAdapter adapter = getAdapter(trimmable);
        int layerCount = adapter.getLayerCount(trimmable);

        for(int i = 0; i < layerCount; i++) {
            layers.put("layer%s".formatted(i + startLayer), adapter.getLayerName(trimmable, i));
        }
        return TrimmableItemModel.builder()
                .parent(overridenModel.parent)
                .textures(TextureLayers.of(layers))
                .build();
    }

    public BufferedReader addGroupPermutationsToAtlasSources(BufferedReader original) {
        JsonObject atlasJson = jsonParser.fromReader(original, JsonObject.class);
        BlockAtlas atlas = jsonParser.fromJson(atlasJson, BlockAtlas.class);
        Optional<BlockAtlas.Source> palettedPermuationsSource = atlas.getPalettedPermutationsSource("trims/color_palettes/trim_palette");
        if (palettedPermuationsSource.isEmpty()) {
            return jsonParser.toReader(atlasJson);
        }

        atlas.addSource(palettedPermuationsSource.get()
                .copy()
                .withType("runtimetrims:group_permutations")
                .withDirectories(List.of(
                        "trims/items/helmet",
                        "trims/items/chestplate",
                        "trims/items/leggings",
                        "trims/items/boots"
                ))
                .withTextures(null)
        );

        return jsonParser.toReader(atlas);
    }
}
