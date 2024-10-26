package com.bawnorton.runtimetrims.client.compat.configurable;

import com.bawnorton.configurable.api.ConfigurableApi;
import com.bawnorton.runtimetrims.RuntimeTrims;

public class RuntimeTrimsConfigurableApiImpl implements ConfigurableApi {
    @Override
    public boolean clientOnly() {
        return true;
    }

    @Override
    public boolean serverEnforces() {
        return false;
    }

    //? if neoforge {
    @Override
    public String getConfigName() {
        return RuntimeTrims.MOD_ID;
    }
    //?}
}
