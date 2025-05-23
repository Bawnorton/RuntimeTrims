package com.bawnorton.runtimetrims.client.mixin.render;

import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    @Shadow @Final private SpriteAtlasTexture armorTrimsAtlas;

    @Shadow public abstract void render(MatrixStack arg, VertexConsumerProvider arg2, int i, T arg3, float f, float g, float h, float j, float k, float l);

    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(
            //? if >1.21 && neoforge {
            method = "renderArmorPiece",
            //?} else {
            /*method = "renderArmor",
            *///?}
            at = @At(
                    value = "INVOKE",
                    //? if fabric {
                    /*target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderTrim(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/render/entity/model/BipedEntityModel;Z)V"
                    *///?} elif neoforge {
                    target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderTrim(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V"
                    //?}
            )
    )
    private void captureContext(CallbackInfo ci, @Local(argsOnly = true) T entity, @Local ArmorItem trimmed) {
        RuntimeTrimsClient.getTrimRenderer().setContext(entity, trimmed);
    }

    @ModifyExpressionValue(
            //? if fabric {
            /*method = "renderTrim",
            *///?} elif neoforge {
            method = "renderTrim(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            //?}
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/SpriteAtlasTexture;getSprite(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/texture/Sprite;"
            )
    )
    private Sprite captureSprite(Sprite original, @Share("sprite") LocalRef<Sprite> spriteLocalRef) {
        spriteLocalRef.set(original);
        return original;
    }

    @WrapOperation(
    //? if fabric {
            /*method = "renderTrim",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private void renderDynamicTrim(BipedEntityModel<T> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, Operation<Void> original,
    *///?} elif neoforge {
            method = "renderTrim(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private void renderDynamicTrim(Model instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, Operation<Void> original,
    //?}
            @Local(argsOnly = true) ArmorTrim trim,
            @Local(argsOnly = true) VertexConsumerProvider vertexConsumers,
            @Local(argsOnly = true) /*$ armour_material >>*/ RegistryEntry<ArmorMaterial> armourMaterial,
            @Local(argsOnly = true) boolean leggings,
            @Share("sprite") LocalRef<Sprite> spriteLocalRef) {
        RuntimeTrimsClient.getTrimRenderer().renderTrim(
                trim,
                armourMaterial,
                leggings,
                spriteLocalRef.get(),
                matrixStack,
                vertexConsumers,
                light,
                uv,
                -1,
                armorTrimsAtlas,
                instance::render
        );
    }
}