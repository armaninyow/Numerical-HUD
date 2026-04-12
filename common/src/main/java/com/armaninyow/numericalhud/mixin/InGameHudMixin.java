package com.armaninyow.numericalhud.mixin;

import com.armaninyow.numericalhud.XpDataHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	
	@Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
	private void cancelHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
		ci.cancel();
	}
	
	@Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
	private void cancelStatusBars(DrawContext context, CallbackInfo ci) {
		ci.cancel();
	}
	
	@Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
	private void cancelMountHealth(DrawContext context, CallbackInfo ci) {
		ci.cancel();
	}
	
	// Hide XP level only during vanilla InGameHud rendering
	@Inject(method = "render", at = @At("HEAD"))
	private void beforeVanillaRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter, CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			// Only store if the level is not already hidden (>= 0)
			if (client.player.experienceLevel >= 0) {
				XpDataHolder.setRealXpData(client.player.experienceLevel, client.player.experienceProgress);
			}
			// Hide the level
			client.player.experienceLevel = -1;
		}
	}
	
	// Restore XP level immediately after vanilla rendering
	@Inject(method = "render", at = @At("TAIL"))
	private void afterVanillaRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter, CallbackInfo ci) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			client.player.experienceLevel = XpDataHolder.getRealXpLevel();
		}
	}
}