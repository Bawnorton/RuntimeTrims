package com.bawnorton.runtimetrims.platform;

import com.bawnorton.runtimetrims.RuntimeTrims;

//? if fabric {
import net.fabricmc.api.ModInitializer;

public final class RuntimeTrimsWrapper implements ModInitializer {
    @Override
    public void onInitialize() {
        RuntimeTrims.init();
    }
}
//?} elif neoforge {
/*import net.neoforged.fml.common.Mod;

@Mod(RuntimeTrims.MOD_ID)
public final class RuntimeTrimsWrapper {
    public RuntimeTrimsWrapper() {
        RuntimeTrims.init();
    }
}
*///?}
