package com.moepus.fakesight.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moepus.fakesight.Config;
import me.cortex.voxy.client.config.VoxyConfigScreenPages;
import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpact;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = VoxyConfigScreenPages.class, remap = false)
public abstract class VoxyConfigScreenPagesMixin {
    @WrapOperation(
            method = "page",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/gui/options/OptionGroup$Builder;add(Lnet/caffeinemc/mods/sodium/client/gui/options/Option;)Lnet/caffeinemc/mods/sodium/client/gui/options/OptionGroup$Builder;",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=voxy.config.general.renderDistance")
            )
    )
    private static OptionGroup.Builder wrapRenderDistanceAdd(
            OptionGroup.Builder instance, Option<?> option, Operation<OptionGroup.Builder> original
    ) {
        OptionGroup.Builder ret = instance.add(option);

        ret.add(OptionImpl.createBuilder(int.class, Config.CONFIG)
                .setName(Component.translatable("fakesight.config.requestDistance"))
                .setTooltip(Component.translatable("fakesight.config.requestDistance.tooltip"))
                .setControl(opt->new SliderControl(opt, 8, 127, 1, v->Component.literal(Integer.toString(v))))
                .setBinding((s, v)-> {
                    s.requestDistance = v;
                }, s -> s.requestDistance)
                .setImpact(OptionImpact.HIGH)
                .build()
        );

        return ret;
    }
}
