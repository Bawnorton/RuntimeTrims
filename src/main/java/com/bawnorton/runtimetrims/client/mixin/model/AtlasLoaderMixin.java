package com.bawnorton.runtimetrims.client.mixin.model;

import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.io.BufferedReader;

@Mixin(AtlasLoader.class)
public abstract class AtlasLoaderMixin {
    @ModifyExpressionValue(
            method = "of",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resource/Resource;getReader()Ljava/io/BufferedReader;"
            )
    )
    private static BufferedReader addGroupPermutationsToAtlasSources(BufferedReader original, ResourceManager manager, Identifier id) {
        if (!id.equals(Identifier.ofVanilla("blocks"))) return original;

        return RuntimeTrimsClient.getItemModelLoader().addGroupPermutationsToAtlasSources(original);
    }
}
