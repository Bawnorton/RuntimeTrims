package com.bawnorton.runtimetrims.client.shader.adapter;

import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.extend.RenderLayer$MultiPhaseParametersExtender;
import com.bawnorton.runtimetrims.client.palette.TrimPalette;
import com.bawnorton.runtimetrims.client.shader.RenderContext;
import com.bawnorton.runtimetrims.client.shader.TrimPalettePhase;
import com.bawnorton.runtimetrims.client.shader.TrimShaderManager;
import com.bawnorton.runtimetrims.util.MemoizedFunction;
import net.minecraft.client.render.RenderLayer;

public abstract class TrimRenderLayerAdpater {
    private final TrimShaderManager shaderManager = RuntimeTrimsClient.getShaderManager();

    protected abstract MemoizedFunction<TrimPalette, RenderLayer> getRenderLayer();

    protected abstract RenderLayer.MultiPhaseParameters.Builder getPhaseParametersBuilder();

    protected RenderLayer.MultiPhaseParameters getPhaseParameters(TrimPalette palette) {
        RenderLayer.MultiPhaseParameters.Builder builder = getPhaseParametersBuilder()
                .program(shaderManager.getProgram());
        RenderLayer.MultiPhaseParameters parameters = builder.build(true);
        ((RenderLayer$MultiPhaseParametersExtender) (Object) parameters).runtimetrims$attachTrimPalette(
                new TrimPalettePhase(
                        "trim_palette",
                        () -> shaderManager.setTrimPalette(getPaletteColours(palette)),
                        () -> {}
                )
        );
        return parameters;
    }

    protected RenderContext getContext() {
        return RuntimeTrimsClient.getTrimRenderer().getContext();
    }

    protected int[] getPaletteColours(TrimPalette palette) {
        return palette.getColourArr();
    }

    public RenderLayer getRenderLayer(TrimPalette trimPalette) {
        return getRenderLayer().apply(trimPalette);
    }

    public void clearCache() {
        getRenderLayer().clear();
    }
}
