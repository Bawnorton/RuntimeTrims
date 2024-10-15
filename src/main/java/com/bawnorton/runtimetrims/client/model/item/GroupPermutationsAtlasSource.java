package com.bawnorton.runtimetrims.client.model.item;

import com.bawnorton.runtimetrims.RuntimeTrims;
import com.bawnorton.runtimetrims.client.mixin.accessor.PalettedPermutationsAtlasSourceAccessor;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupPermutationsAtlasSource extends PalettedPermutationsAtlasSource {
    public static final MapCodec<GroupPermutationsAtlasSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.list(Identifier.CODEC)
                            .fieldOf("directories")
                            .forGetter(source -> ((PalettedPermutationsAtlasSourceAccessor) source).getTextures()),
                    Identifier.CODEC.fieldOf("palette_key")
                            .forGetter(source -> ((PalettedPermutationsAtlasSourceAccessor) source).getPaletteKey()),
                    Codec.unboundedMap(Codec.STRING, Identifier.CODEC)
                            .fieldOf("permutations")
                            .forGetter(source -> ((PalettedPermutationsAtlasSourceAccessor) source).getPermutations())
            ).apply(instance, GroupPermutationsAtlasSource::new));

    public static AtlasSourceType TYPE;

    private GroupPermutationsAtlasSource(List<Identifier> directories, Identifier paletteKey, Map<String, Identifier> permutations) {
        super(new ArrayList<>(directories), paletteKey, addBlankPermutation(permutations));
    }

    public static void init() {
        // no-op
    }

    private static Map<String, Identifier> addBlankPermutation(Map<String, Identifier> permutations) {
        return ImmutableMap.<String, Identifier>builder()
                .putAll(permutations)
                .put(RuntimeTrims.DYNAMIC, Identifier.ofVanilla("trims/color_palettes/%s".formatted(RuntimeTrims.DYNAMIC)))
                .build();
    }

    @Override
    public void load(ResourceManager resourceManager, SpriteRegions regions) {
        List<Identifier> combinedTextures = new ArrayList<>();
        List<Identifier> originalTextures = ((PalettedPermutationsAtlasSourceAccessor) this).getTextures();
        for (Identifier dir : originalTextures) {
            ResourceFinder resourceFinder = new ResourceFinder("textures/" + dir.getPath(), ".png");
            resourceFinder.findResources(resourceManager)
                    .forEach((identifier, resource) -> {
                        Identifier id = resourceFinder.toResourceId(identifier).withPrefixedPath(dir.getPath() + "/");
                        combinedTextures.add(id);
                        for (int i = 0; i < 8; i++) {
                            combinedTextures.add(id.withSuffixedPath("_" + i));
                        }
                    });
        }
        originalTextures.clear();
        originalTextures.addAll(combinedTextures);
        super.load(resourceManager, regions);
    }

    @Override
    public AtlasSourceType getType() {
        return TYPE;
    }
}
