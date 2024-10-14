package com.bawnorton.runtimetrims;

import com.bawnorton.runtimetrims.registry.TrimMaterialRegistryInjector;
import com.bawnorton.runtimetrims.tag.TrimMaterialTagInjector;
import com.bawnorton.runtimetrims.tag.TrimmableArmourTagInjector;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RuntimeTrims {
    public static final String MOD_ID = "runtimetrims";
    public static final String DYNAMIC = "dynamic";
    public static final float MATERIAL_MODEL_INDEX = 0.6632484f;
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final TrimMaterialRegistryInjector trimMaterialRegistryInjector = new TrimMaterialRegistryInjector();
    private static final TrimMaterialTagInjector trimMaterialTagInjector = new TrimMaterialTagInjector();
    private static final TrimmableArmourTagInjector trimmableArmourTagInjector = new TrimmableArmourTagInjector();

    public static void init() {
        LOGGER.debug("{} Initialized", MOD_ID);
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static TrimMaterialRegistryInjector getTrimMaterialRegistryInjector() {
        return trimMaterialRegistryInjector;
    }

    public static TrimMaterialTagInjector getTrimMaterialTagInjector() {
        return trimMaterialTagInjector;
    }

    public static TrimmableArmourTagInjector getTrimmableArmourTagInjector() {
        return trimmableArmourTagInjector;
    }
}
