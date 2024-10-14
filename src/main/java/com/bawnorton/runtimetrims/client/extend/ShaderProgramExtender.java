package com.bawnorton.runtimetrims.client.extend;

import net.minecraft.client.gl.GlUniform;

public interface ShaderProgramExtender {
    GlUniform runtimetrims$getTrimPalette();
    GlUniform runtimetrims$getDebug();
}
