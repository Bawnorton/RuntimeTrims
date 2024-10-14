package com.bawnorton.runtimetrims.client.compat.iris;

import net.irisshaders.iris.Iris;

public final class IrisCompat {
    public boolean isUsingShader() {
        return Iris.getCurrentPack().isPresent();
    }
}
