package com.armaninyow.numericalhud.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.JumpableVehicleBarRenderer;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JumpableVehicleBarRenderer.class)
public class JumpBarMixin {

	@Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
	private void cancelJumpBar(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
	private void cancelJumpBarBackground(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
		ci.cancel();
	}
}