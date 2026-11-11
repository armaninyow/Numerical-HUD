package com.armaninyow.numericalhud.mixin;

import com.armaninyow.numericalhud.XpDataHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class InGameHudMixin {

	@Inject(method = "extractPlayerHealth", at = @At("HEAD"), cancellable = true)
	private void cancelPlayerHealth(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "extractVehicleHealth", at = @At("HEAD"), cancellable = true)
	private void cancelVehicleHealth(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "extractRenderState", at = @At("HEAD"))
	private void beforeExtractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		Minecraft client = Minecraft.getInstance();
		if (client.player != null) {
			if (client.player.experienceLevel >= 0) {
				XpDataHolder.setRealXpData(client.player.experienceLevel, client.player.experienceProgress);
			}
			client.player.experienceLevel = -1;
		}
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void afterExtractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		Minecraft client = Minecraft.getInstance();
		if (client.player != null) {
			client.player.experienceLevel = XpDataHolder.getRealXpLevel();
		}
	}
}