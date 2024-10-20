package com.bawnorton.runtimetrims.client.platform;

import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;

//? if fabric {
/*import net.fabricmc.api.ClientModInitializer;

public final class RuntimeTrimsClientWrapper implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RuntimeTrimsClient.init();
    }
}
*///?} elif neoforge {
import com.bawnorton.runtimetrims.RuntimeTrims;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = RuntimeTrims.MOD_ID, dist = Dist.CLIENT)
public final class RuntimeTrimsClientWrapper {
    public RuntimeTrimsClientWrapper() {
        RuntimeTrimsClient.init();
    }
}
//?}
