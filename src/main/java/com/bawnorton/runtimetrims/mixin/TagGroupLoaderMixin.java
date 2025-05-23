package com.bawnorton.runtimetrims.mixin;

import com.bawnorton.runtimetrims.RuntimeTrims;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(TagGroupLoader.class)
public abstract class TagGroupLoaderMixin {
    @Unique
    private static final ThreadLocal<Identifier> runtimetrims$TAG_ID = ThreadLocal.withInitial(() -> null);

    @ModifyVariable(
            //? if fabric {
            /*method = "method_51476",
            *///?} elif neoforge {
            method = "lambda$build$6",
            //?}
            at = @At("HEAD"),
            argsOnly = true
    )
    private Identifier captureTagId(Identifier id) {
        runtimetrims$TAG_ID.set(id);
        return id;
    }

    @ModifyVariable(method = "resolveAll", at = @At("HEAD"), argsOnly = true)
    private <T> List<TagGroupLoader.TrackedEntry> addToTagEntries(List<TagGroupLoader.TrackedEntry> entries, TagEntry.ValueGetter<T> valueGetter) {
        Identifier id = runtimetrims$TAG_ID.get();
        if (id.equals(ItemTags.TRIM_MATERIALS.id())) {
            entries.addAll(RuntimeTrims.getTrimTagInjector()
                    .getTrimMaterials()
                    .stream()
                    .map(Registries.ITEM::getId)
                    .map(itemId -> new TagGroupLoader.TrackedEntry(TagEntry.create(itemId), RuntimeTrims.MOD_ID))
                    .collect(Collectors.toSet()));
        } else if (id.equals(ItemTags.TRIMMABLE_ARMOR.id())) {
            entries.addAll(RuntimeTrims.getTrimTagInjector()
                    .getTrimmableArmour()
                    .stream()
                    .map(Registries.ITEM::getId)
                    .map(itemId -> new TagGroupLoader.TrackedEntry(TagEntry.create(itemId), RuntimeTrims.MOD_ID))
                    .collect(Collectors.toSet()));
        }

        return entries;
    }
}
