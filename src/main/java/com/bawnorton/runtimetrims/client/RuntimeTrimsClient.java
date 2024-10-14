package com.bawnorton.runtimetrims.client;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.OptionType;
import com.bawnorton.configurable.Yacl;
import com.bawnorton.runtimetrims.RuntimeTrims;
import com.bawnorton.runtimetrims.client.compat.Compat;
import com.bawnorton.runtimetrims.client.model.armour.ArmourTrimModelLoader;
import com.bawnorton.runtimetrims.client.model.item.ItemTrimModelLoader;
import com.bawnorton.runtimetrims.client.model.item.adapter.DefaultTrimModelLoaderAdapter;
import com.bawnorton.runtimetrims.client.palette.TrimPalette;
import com.bawnorton.runtimetrims.client.palette.TrimPalettes;
import com.bawnorton.runtimetrims.client.render.LayerData;
import com.bawnorton.runtimetrims.client.render.TrimRenderer;
import com.bawnorton.runtimetrims.client.render.adapter.DefaultTrimRendererAdapter;
import com.bawnorton.runtimetrims.client.render.adapter.ShowMeYourSkinTrimRendererAdapter;
import com.bawnorton.runtimetrims.client.shader.TrimShaderManager;
import com.bawnorton.runtimetrims.client.shader.adapter.DefaultTrimRenderLayerAdapter;
import com.bawnorton.runtimetrims.client.shader.adapter.ShowMeYourSkinTrimRenderLayerAdapter;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.text.Text;

public final class RuntimeTrimsClient {
    private static final LayerData layerData = new LayerData();
    private static final TrimPalettes trimPalettes = new TrimPalettes();
    private static final TrimRenderer trimRenderer = new TrimRenderer();
    private static final ItemTrimModelLoader itemModelLoader = new ItemTrimModelLoader(layerData);
    private static final ArmourTrimModelLoader armourModelLoader = new ArmourTrimModelLoader(layerData);
    private static final TrimShaderManager shaderManager = new TrimShaderManager();

    @Configurable(yacl = @Yacl(type = OptionType.ASSET_RELOAD))
    public static boolean overrideExisting = false;
    @Configurable
    public static boolean useLegacyRenderer = false;
    @Configurable
    public static boolean debug = false;
    @Configurable(yacl = @Yacl(listener = "clearPaletteCache", formatter = "enumFormatter"))
    public static PaletteSorting paletteSorting = PaletteSorting.COLOUR;
    @Configurable(yacl = @Yacl(listener = "clearAnimationCache"))
    public static boolean animate = false;
    @Configurable
    public static int ticksBetweenCycles = 75;

    public static void init() {
        itemModelLoader.setDefaultAdapter(new DefaultTrimModelLoaderAdapter());

        if(Compat.getShowMeYourSkinCompat().isPresent()) {
            trimRenderer.setDefaultAdapter(new ShowMeYourSkinTrimRendererAdapter());
            shaderManager.setDefaultAdapter(new ShowMeYourSkinTrimRenderLayerAdapter());
        } else {
            trimRenderer.setDefaultAdapter(new DefaultTrimRendererAdapter());
            shaderManager.setDefaultAdapter(new DefaultTrimRenderLayerAdapter());
        }
    }

    public static boolean isDynamic(ArmorTrim trim) {
        return trim.getMaterial().value().itemModelIndex() == RuntimeTrims.MATERIAL_MODEL_INDEX || overrideExisting;
    }

    public static TrimPalettes getTrimPalettes() {
        return trimPalettes;
    }

    public static TrimRenderer getTrimRenderer() {
        return trimRenderer;
    }

    /**
     * The inventory item models
     */
    public static ItemTrimModelLoader getItemModelLoader() {
        return itemModelLoader;
    }

    /**
     * The in-world armour models
     */
    public static ArmourTrimModelLoader getArmourModelLoader() {
        return armourModelLoader;
    }

    public static TrimShaderManager getShaderManager() {
        return shaderManager;
    }

    public static LayerData getLayerData() {
        return layerData;
    }

    public enum PaletteSorting {
        COLOUR, COLOUR_REVERSED,
        SATURATION, SATURATION_REVERSED,
        BRIGHTNESS, BRIGHTNESS_REVERSED;

        public boolean isReversed() {
            return this == COLOUR_REVERSED || this == SATURATION_REVERSED || this == BRIGHTNESS_REVERSED;
        }

        public boolean isColour() {
            return this == COLOUR || this == COLOUR_REVERSED;
        }

        public boolean isSaturation() {
            return this == SATURATION || this == SATURATION_REVERSED;
        }

        public boolean isBrightness() {
            return this == BRIGHTNESS || this == BRIGHTNESS_REVERSED;
        }
    }

    public static void clearPaletteCache(PaletteSorting paletteSorting) {
        getTrimPalettes().regenerate();
        getShaderManager().clearRenderLayerCaches();
    }

    public static void clearAnimationCache(boolean animate) {
        getShaderManager().clearRenderLayerCaches();
        if (!animate) {
            getTrimPalettes().forEach(TrimPalette::computeColourArr);
        }
    }

    public static Text enumFormatter(Enum<?> e) {
        return Text.translatable("configurable.runtimetrims.yacl.option.paletteSorting.%s".formatted(e.name().toLowerCase()));
    }
}
