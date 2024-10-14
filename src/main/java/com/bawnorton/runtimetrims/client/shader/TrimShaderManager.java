package com.bawnorton.runtimetrims.client.shader;

import com.bawnorton.runtimetrims.client.palette.TrimPalette;
import com.bawnorton.runtimetrims.client.shader.adapter.TrimRenderLayerAdpater;
import com.bawnorton.runtimetrims.util.ItemAdaptable;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.item.Item;

public final class TrimShaderManager extends ItemAdaptable<TrimRenderLayerAdpater> {
    public ShaderProgram renderTypeDynamicTrimProgram;

    private final RenderPhase.ShaderProgram DYNAMIC_TRIM_PROGRAM = new RenderPhase.ShaderProgram(() -> renderTypeDynamicTrimProgram);

    private int[] trimPalette = new int[8];

    public RenderLayer getTrimRenderLayer(Item trimmed, TrimPalette palette) {
        return getAdapter(trimmed).getRenderLayer(palette);
    }

    public void clearRenderLayerCaches() {
        getAdapters().forEach(TrimRenderLayerAdpater::clearCache);
    }

    public void setTrimPalette(int[] trimPalette) {
        RenderSystem.assertOnRenderThread();
        this.trimPalette = trimPalette;
    }

    public int[] getTrimPalette() {
        RenderSystem.assertOnRenderThread();
        return trimPalette;
    }

    public RenderPhase.ShaderProgram getProgram() {
        return DYNAMIC_TRIM_PROGRAM;
    }
}
