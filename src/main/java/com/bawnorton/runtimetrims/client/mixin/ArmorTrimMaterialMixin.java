package com.bawnorton.runtimetrims.client.mixin;

import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.palette.TrimPalette;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorTrimMaterial.class)
public abstract class ArmorTrimMaterialMixin {
    @Shadow @Final private RegistryEntry<Item> ingredient;

    @ModifyReturnValue(
            method = "description",
            at = @At("RETURN")
    )
    private Text colouriseDescription(Text original) {
        if(!(original instanceof MutableText mutableText)) return original;

        Style style = mutableText.getStyle();
        if(style == null) style = Style.EMPTY;

        TextColor colour = style.getColor();
        if(colour != null) return original;

        TrimPalette palette = RuntimeTrimsClient.getTrimPalettes().getPalette(ingredient.value());
        if(palette == null) return original;

        return mutableText.styled(s -> s.withColor(palette.getAverageColour()));
    }
}
