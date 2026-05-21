package com.armaninyow.numericalhud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class EffectsMixin {

	@Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
	private void cancelEffects(GuiGraphicsExtractor context, DeltaTracker deltaTracker, CallbackInfo ci) {
		ci.cancel();
	}
}