package com.armaninyow.numericalhud.mixin;

import com.armaninyow.numericalhud.XpDataHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {

	// Cancel vanilla health, armor, food, air bars
	@Inject(method = "extractPlayerHealth", at = @At("HEAD"), cancellable = true)
	private void cancelPlayerHealth(GuiGraphicsExtractor context, CallbackInfo ci) {
		ci.cancel();
	}

	// Cancel vanilla vehicle health bar
	@Inject(method = "extractVehicleHealth", at = @At("HEAD"), cancellable = true)
	private void cancelVehicleHealth(GuiGraphicsExtractor context, CallbackInfo ci) {
		ci.cancel();
	}

	// Capture real XP data before vanilla hides it, then restore after
	@Inject(method = "extractRenderState", at = @At("HEAD"))
	private void beforeExtractRenderState(GuiGraphicsExtractor context, DeltaTracker deltaTracker, CallbackInfo ci) {
		Minecraft client = Minecraft.getInstance();
		if (client.player != null) {
			if (client.player.experienceLevel >= 0) {
				XpDataHolder.setRealXpData(client.player.experienceLevel, client.player.experienceProgress);
			}
			client.player.experienceLevel = -1;
		}
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void afterExtractRenderState(GuiGraphicsExtractor context, DeltaTracker deltaTracker, CallbackInfo ci) {
		Minecraft client = Minecraft.getInstance();
		if (client.player != null) {
			client.player.experienceLevel = XpDataHolder.getRealXpLevel();
		}
	}
}