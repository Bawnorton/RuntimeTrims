package com.bawnorton.runtimetrims.client.mixin.shader;

import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.extend.ShaderProgramExtender;
import com.bawnorton.runtimetrims.client.mixin.accessor.GlUniformAccessor;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.nio.IntBuffer;

@Mixin(ShaderProgram.class)
public abstract class ShaderProgramMixin implements ShaderProgramExtender {
    @Shadow @Nullable
    public abstract GlUniform getUniform(String name);

    @Unique
    private GlUniform runtimetrims$trimPalette;

    @Unique
    private GlUniform runtimetrims$debug;

    @Inject(
            //? if fabric {
            /*method = "<init>",
            *///?} elif neoforge {
            method = "<init>(Lnet/minecraft/resource/ResourceFactory;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/VertexFormat;)V",
            //?}
            at = @At("TAIL")
    )
    private void initAdditionalUniforms(CallbackInfo ci) {
        runtimetrims$trimPalette = getUniform("runtimetrims_TrimPalette");
        runtimetrims$debug = getUniform("runtimetrims_Debug");
    }

    @Override
    public GlUniform runtimetrims$getTrimPalette() {
        return runtimetrims$trimPalette;
    }

    @Override
    public GlUniform runtimetrims$getDebug() {
        return runtimetrims$debug;
    }

    @Inject(
            method = "initializeUniforms",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/gl/ShaderProgram;)V"
            )
    )
    private void setAdditionalUniforms(CallbackInfo ci) {
        if(runtimetrims$trimPalette != null) {
            int[] trimPalette = RuntimeTrimsClient.getShaderManager().getTrimPalette();
            IntBuffer intData = runtimetrims$trimPalette.getIntData();
            intData.position(0);
            for (int i = 0; i < trimPalette.length; i++) {
                intData.put(i, trimPalette[i]);
            }
            ((GlUniformAccessor) runtimetrims$trimPalette).callMarkStateDirty();
        }

        if(runtimetrims$debug != null) {
            runtimetrims$debug.set(RuntimeTrimsClient.debug ? 1 : 0);
        }
    }
}
