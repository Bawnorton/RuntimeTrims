package com.bawnorton.runtimetrims.client.mixin.model;

import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin {
    @Shadow @Final private SpriteAtlasManager atlasManager;

    @Shadow private int mipmapLevels;

    @ModifyExpressionValue(
            method = "method_45895",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"
            )
    )
    private static Map<Identifier, Resource> addDynamicTrimModels(Map<Identifier, Resource> original) {
        return RuntimeTrimsClient.getItemModelLoader().loadModels(original);
    }

    @Inject(
            method = "reload",
            at = @At("HEAD")
    )
    private void preLoadAtlases(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        atlasManager.reload(manager, mipmapLevels, prepareExecutor).values().forEach(CompletableFuture::join);
    }
}
