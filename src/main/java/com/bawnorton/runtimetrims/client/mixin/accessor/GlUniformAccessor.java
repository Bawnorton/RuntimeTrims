package com.bawnorton.runtimetrims.client.mixin.accessor;

import net.minecraft.client.gl.GlUniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GlUniform.class)
public interface GlUniformAccessor {
    @Invoker
    void callMarkStateDirty();
}
