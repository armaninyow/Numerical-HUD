package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class VehicleHealthModule extends BaseHudModule {
	
	private static final int COLOR_WHITE = 0xFFFFFFFF;
	private static final int COLOR_RED = 0xFFFF0000;
	private static final int COLOR_GREEN = 0xFF00FF00;
	
	private float lastVehicleHealth = Float.MAX_VALUE;
	
	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta) {
		Entity vehicle = player.getVehicle();
		
		if (!(vehicle instanceof LivingEntity livingVehicle)) {
			return;
		}
		
		float health = livingVehicle.getHealth();
		float maxHealth = livingVehicle.getMaxHealth();
		
		AnimationStyle style = ModConfig.get().animationStyle;
		
		if (style == AnimationStyle.DECIMAL) {
			updateAnimation(health, 0.1f);
			triggerDamageBlink(health, lastVehicleHealth);
		} else {
			tickStyleAnimations(health, lastVehicleHealth);
			triggerDamageBlink(health, lastVehicleHealth);
		}
		lastVehicleHealth = health;
		
		updateBlinkTimer();
		updateRecurringBlink(health, maxHealth);
		
		// Select textures
		Identifier containerTexture = Identifier.of("minecraft", "hud/heart/vehicle_container");
		Identifier foregroundTexture = Identifier.of("minecraft", "hud/heart/vehicle_full");

		// Render icon
		if (shouldShowBlink()) {
			drawVanillaSprite(context, containerTexture, x, y - 1);
		} else {
			drawVanillaSprite(context, containerTexture, x, y - 1);
			drawVanillaSprite(context, foregroundTexture, x, y - 1);
		}
		
		// Render text
		int color = getStyledColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		String text = getStyledText(health);
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, color);
		
		// Render popup if applicable
		renderPopup(context, x, y);
		
		// Render jump bar if applicable
		if (vehicle instanceof AbstractHorseEntity horse) {
			renderJumpBar(context, horse, x, y - 12);
		}
	}
	
	private void renderJumpBar(DrawContext context, AbstractHorseEntity horse, int x, int y) {
		// Jump bar rendering can be implemented here if needed
		// This would show the horse's jump charge
	}
}