package com.armaninyow.numericalhud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.armaninyow.numericalhud.ModConfig;

@Mixin(Hud.class)
public class EffectsMixin {

	@Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
	private void cancelEffects(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (ModConfig.get().replaceEffects) {
			ci.cancel();
		}
	}
}