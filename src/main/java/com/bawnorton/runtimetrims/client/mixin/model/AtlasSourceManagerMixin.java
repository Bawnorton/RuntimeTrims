package com.bawnorton.runtimetrims.client.mixin.model;

import com.bawnorton.runtimetrims.RuntimeTrims;
import com.bawnorton.runtimetrims.client.model.item.GroupPermutationsAtlasSource;
import com.google.common.collect.BiMap;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AtlasSourceManager.class)
public abstract class AtlasSourceManagerMixin {
    @Shadow @Final private static BiMap<Identifier, AtlasSourceType> SOURCE_TYPE_BY_ID;

    static {
        GroupPermutationsAtlasSource.TYPE = new AtlasSourceType(GroupPermutationsAtlasSource.CODEC);
        SOURCE_TYPE_BY_ID.put(RuntimeTrims.id("group_permutations"), GroupPermutationsAtlasSource.TYPE);
    }
}
