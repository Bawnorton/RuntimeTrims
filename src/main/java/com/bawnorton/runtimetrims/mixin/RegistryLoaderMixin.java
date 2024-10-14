package com.bawnorton.runtimetrims.mixin;

import com.bawnorton.runtimetrims.RuntimeTrims;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Lifecycle;
import net.minecraft.SharedConstants;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.entry.RegistryEntryInfo;

@Mixin(RegistryLoader.class)
public abstract class RegistryLoaderMixin {
    @SuppressWarnings("unchecked")
    @Inject(
            method = "loadFromResource(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V",
            at = @At("TAIL")
    )
    private static <E> void addAllTrimMaterialsToRegistry(ResourceManager resourceManager, RegistryOps.RegistryInfoGetter infoGetter, MutableRegistry<E> registry, Decoder<E> elementDecoder, Map<RegistryKey<?>, Exception> errors, CallbackInfo ci) {
        if (registry.getKey().equals(RegistryKeys.TRIM_MATERIAL)) {
            boolean valid = false;
            if(registry instanceof SimpleRegistry<?> simpleRegistry) {
                if (!simpleRegistry.isEmpty()) {
                    valid = simpleRegistry.iterator().next() instanceof ArmorTrimMaterial;
                }
            }
            if(!valid) {
                // Sanity check before unchecked cast. Should never happen
                RuntimeTrims.LOGGER.error("Could not add materials to registry. RuntimeTrims will not work, expected \"{} for {}\" but found \"{} for {}\".",
                        SimpleRegistry.class.getSimpleName(),
                        ArmorTrimMaterial.class.getSimpleName(),
                        registry.getClass().getSimpleName(),
                        registry.isEmpty() ? "<empty>" : registry.iterator().next().getClass()
                );
                return;
            }

            SimpleRegistry<ArmorTrimMaterial> trimMaterialRegistry = (SimpleRegistry<ArmorTrimMaterial>) registry;

            RegistryEntryInfo info = new RegistryEntryInfo(
                    Optional.of(new VersionedIdentifier(
                            RuntimeTrims.MOD_ID,
                            "runtime_trim_materials",
                            SharedConstants.getGameVersion().getId()
                    )),
                    Lifecycle.stable()
            );

            Map<Identifier, RegistryEntry<Item>> newMaterials = RuntimeTrims.getTrimMaterialRegistryInjector().getNewMaterials(trimMaterialRegistry);


            for (Map.Entry<Identifier, RegistryEntry<Item>> newMaterial : newMaterials.entrySet()) {
                RegistryKey<ArmorTrimMaterial> trimRegKey = RegistryKey.of(trimMaterialRegistry.getKey(), newMaterial.getKey());
                ArmorTrimMaterial itemMaterial = new ArmorTrimMaterial(
                        RuntimeTrims.DYNAMIC,
                        newMaterial.getValue(),
                        RuntimeTrims.MATERIAL_MODEL_INDEX,
                        Map.of(),
                        Text.translatable("runtimetrims.material", newMaterial.getValue().value().getName().getString())
                );
                trimMaterialRegistry.add(trimRegKey, itemMaterial, info);
            }

            RuntimeTrims.LOGGER.info("Added {} new trim materials!", newMaterials.size());
        }
    }
}
