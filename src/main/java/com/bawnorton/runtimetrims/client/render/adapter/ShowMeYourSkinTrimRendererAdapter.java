package com.bawnorton.runtimetrims.client.render.adapter;

import com.bawnorton.runtimetrims.client.compat.Compat;
import com.bawnorton.runtimetrims.client.compat.showmeyourskin.ShowMeYourSkinCompat;
import com.bawnorton.runtimetrims.client.render.TrimRenderer;
import com.bawnorton.runtimetrims.client.shader.RenderContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.trim.ArmorTrim;

public final class ShowMeYourSkinTrimRendererAdapter extends TrimRendererAdapter {
    @Override
    public RenderLayer getLegacyRenderLayer(ArmorTrim trim) {
        return Compat.getShowMeYourSkinCompat()
                .map(ShowMeYourSkinCompat::getRenderLayer)
                .orElseThrow();
    }

    @Override
    public int getAlpha(RenderContext context) {
        return Compat.getShowMeYourSkinCompat()
                .map(compat -> compat.getAlpha(context.entity(), context.trimmed()))
                .orElse(255);
    }

    @Override
    public void render(RenderContext context, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int colour, TrimRenderer.RenderCallback callback) {
        callback.render(matrices, vertexConsumer, light, overlay, colour);
    }
}
