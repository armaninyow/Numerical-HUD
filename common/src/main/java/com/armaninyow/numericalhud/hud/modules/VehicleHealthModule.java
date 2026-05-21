package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;

public class VehicleHealthModule extends BaseHudModule {
	
	private static final int COLOR_WHITE = 0xFFFFFFFF;
	private static final int COLOR_RED   = 0xFFFF0000;
	private static final int COLOR_GREEN = 0xFF00FF00;
	
	private float lastVehicleHealth = Float.MAX_VALUE;

	// Vanilla-style blink state
	private long vehicleBlinkTime = 0;
	private int lastVehicleHealthInt = -1;
	
	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(GuiGraphicsExtractor context, Player player, int x, int y, float tickDelta) {
		Entity vehicle = player.getVehicle();
		
		if (!(vehicle instanceof LivingEntity livingVehicle)) {
			lastVehicleHealthInt = -1;
			lastVehicleHealth = Float.MAX_VALUE;
			return;
		}
		
		float health = livingVehicle.getHealth();
		float maxHealth = livingVehicle.getMaxHealth();
		
		AnimationStyle style = ModConfig.get().animationStyle;
		
		if (style == AnimationStyle.DECIMAL) {
			updateAnimation(health, 0.1f);
		} else {
			tickStyleAnimations(health, lastVehicleHealth);
		}
		lastVehicleHealth = health;

		int tickCount = Minecraft.getInstance().gui.getGuiTicks();
		int healthInt = (int) Math.ceil(health);

		if (lastVehicleHealthInt >= 0) {
			if (healthInt < lastVehicleHealthInt) {
				vehicleBlinkTime = tickCount + 20;
			} else if (healthInt > lastVehicleHealthInt) {
				vehicleBlinkTime = tickCount + 10;
			}
		}
		lastVehicleHealthInt = healthInt;

		boolean blink = vehicleBlinkTime > tickCount
			&& ((vehicleBlinkTime - tickCount) / 3) % 2 == 1;

		Identifier containerTexture = Identifier.fromNamespaceAndPath("minecraft", "hud/heart/vehicle_container");
		Identifier foregroundTexture = Identifier.fromNamespaceAndPath("minecraft", "hud/heart/vehicle_full");

		if (blink) {
			drawVanillaSprite(context, containerTexture, x, y - 1);
		} else {
			drawVanillaSprite(context, containerTexture, x, y - 1);
			drawVanillaSprite(context, foregroundTexture, x, y - 1);
		}
		
		int color = getStyledColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		String text = getStyledText(health);
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, color);
		
		renderPopup(context, x, y);
	}
}