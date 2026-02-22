package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class HealthModule extends BaseHudModule {
	
	private static final int COLOR_WHITE = 0xFFFFFFFF;
	private static final int COLOR_RED = 0xFFFF0000;
	private static final int COLOR_GREEN = 0xFF00FF00;
	private static final int PANIC_THRESHOLD = 4;
	
	private float lastHealth = Float.MAX_VALUE;
	
	@Override
	public void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta) {
		float health = player.getHealth();
		float absorption = player.getAbsorptionAmount();
		float maxHealth = player.getMaxHealth();
		
		float totalHealth = health + absorption;
		
		MinecraftClient client = MinecraftClient.getInstance();
		boolean isHardcore = client.world != null && client.world.getLevelProperties().isHardcore();
		
		boolean isPoisoned = player.hasStatusEffect(StatusEffects.POISON);
		boolean isWithered = player.hasStatusEffect(StatusEffects.WITHER);
		boolean isFrozen = player.isFrozen();
		
		
		AnimationStyle style = ModConfig.get().animationStyle;
		
		if (style == AnimationStyle.DECIMAL) {
			updateAnimation(totalHealth, 0.1f);
			triggerDamageBlink(totalHealth, lastHealth);
		} else {
			tickStyleAnimations(totalHealth, lastHealth);
			triggerDamageBlink(totalHealth, lastHealth);
		}
		lastHealth = totalHealth;
		
		updateBlinkTimer();
		updateRecurringBlink(totalHealth, maxHealth);
		
		// Determine text color
		int textColor;
		if (health <= PANIC_THRESHOLD) {
			textColor = COLOR_RED;
		} else {
			textColor = getStyledColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		}
		
		// Select textures
		Identifier containerTexture = getContainerTexture(isHardcore, shouldShowBlink());
		Identifier foregroundTexture = getForegroundTexture(
			absorption > 0, isPoisoned, isWithered, isFrozen, isHardcore, shouldShowBlink()
		);
		
		// Render icon (moved 1px up)
		drawIcon(context, containerTexture, x, y - 1);
		drawIcon(context, foregroundTexture, x, y - 1);
		
		// Render text
		String text = getStyledText(totalHealth);
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
		
		// Render popup if applicable
		renderPopup(context, x, y);
	}
	
	private Identifier getContainerTexture(boolean hardcore, boolean blink) {
		String prefix = "health/";
		String suffix = blink ? "_blinking.png" : ".png";
		
		if (hardcore) {
			return getTexture(prefix + "container_hardcore" + suffix);
		}
		return getTexture(prefix + "container" + suffix);
	}
	
	private Identifier getForegroundTexture(boolean absorption, boolean poisoned,
										   boolean withered, boolean frozen,
										   boolean hardcore, boolean blink) {
		String prefix = "health/";
		String suffix = blink ? "_blinking.png" : ".png";
		
		if (absorption) {
			return getTexture(prefix + (hardcore ? "absorbing_hardcore_full" : "absorbing_full") + suffix);
		}
		if (poisoned) {
			return getTexture(prefix + (hardcore ? "poisoned_hardcore_full" : "poisoned_full") + suffix);
		}
		if (withered) {
			return getTexture(prefix + (hardcore ? "withered_hardcore_full" : "withered_full") + suffix);
		}
		if (frozen) {
			return getTexture(prefix + (hardcore ? "frozen_hardcore_full" : "frozen_full") + suffix);
		}
		return getTexture(prefix + (hardcore ? "hardcore_full" : "full") + suffix);
	}
}