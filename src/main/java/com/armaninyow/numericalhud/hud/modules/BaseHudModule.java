package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.NumericalHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public abstract class BaseHudModule {
	
	protected static final int ICON_SIZE = 9;
	protected static final int ICON_TEXT_GAP = 3;
	protected static final int ANIMATION_TICKS = 10;
	
	protected float currentDisplayValue = 0f;
	protected float targetValue = 0f;
	protected float animationStartValue = 0f;
	protected int animationTicks = 0;
	protected boolean isAnimating = false;
	protected boolean isIncreasing = false;
	protected int cooldownTicks = 0;
	
	protected int blinkTimer = 0;
	protected boolean shouldBlink = false;
	protected int recurringBlinkTimer = 0;
	protected int currentBlinkInterval = 0;
	protected boolean recurringBlinkActive = false;
	protected int lastTickCount = -1;
	
	public abstract void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta);
	
	protected Identifier getTexture(String path) {
		return Identifier.of(NumericalHud.MOD_ID, "textures/hud/" + path);
	}
	
	protected void drawIcon(DrawContext context, Identifier texture, int x, int y) {
		drawIcon(context, texture, x, y, 1.0f);
	}
	
	protected void drawIcon(DrawContext context, Identifier texture, int x, int y, float alpha) {
		int alphaInt = (int)(alpha * 255);
		int color = (alphaInt << 24) | 0xFFFFFF;
		context.drawTexture(
			net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED, 
			texture, 
			x, y, 
			0.0f, 0.0f, 
			ICON_SIZE, ICON_SIZE, 
			ICON_SIZE, ICON_SIZE,
			color
		);
	}
	
	protected void drawText(DrawContext context, String text, int x, int y, int color) {
		MinecraftClient client = MinecraftClient.getInstance();
		context.drawTextWithShadow(client.textRenderer, text, x, y, color);
	}
	
	protected void updateAnimation(float newTargetValue, float increment) {
		if (cooldownTicks > 0) {
			cooldownTicks--;
		}
		
		if (!isAnimating && cooldownTicks == 0) {
			if (Math.abs(newTargetValue - currentDisplayValue) > 0.5f) {
				animationStartValue = currentDisplayValue;
				targetValue = newTargetValue;
				isAnimating = true;
				animationTicks = 0;
				isIncreasing = targetValue > animationStartValue;
			} else {
				currentDisplayValue = newTargetValue;
				targetValue = newTargetValue;
			}
		}
		
		if (isAnimating) {
			animationTicks++;
			
			float progress = animationTicks / (float)ANIMATION_TICKS;
			
			if (isIncreasing) {
				float decimalEnd = (float)Math.floor(targetValue) + 0.9f;
				currentDisplayValue = animationStartValue + (decimalEnd - animationStartValue) * progress;
			} else {
				float decimalEnd = (float)Math.ceil(targetValue) + 0.1f;
				currentDisplayValue = animationStartValue + (decimalEnd - animationStartValue) * progress;
			}
			
			if (animationTicks >= ANIMATION_TICKS) {
				currentDisplayValue = targetValue;
				isAnimating = false;
				cooldownTicks = 5;
			}
		}
	}
	
	protected int getAnimationColor(int normalColor, int damageColor, int healColor) {
		if (!isAnimating) {
			return normalColor;
		}
		return isIncreasing ? healColor : damageColor;
	}
	
	protected String formatValue(float value, boolean showDecimal) {
		if (showDecimal && isAnimating) {
			return String.format("%.1f", value);
		}
		return String.valueOf((int)Math.floor(value));
	}
	
	protected void updateBlinkTimer() {
		if (shouldBlink) {
			blinkTimer++;
			if (blinkTimer >= 10) {
				shouldBlink = false;
				blinkTimer = 0;
			}
		}
	}
	
	protected boolean shouldShowBlink() {
		return shouldBlink && (blinkTimer % 4 < 2);
	}
	
	protected void updateRecurringBlink(float currentValue, float maxValue) {
		int currentIntValue = (int)Math.floor(currentValue);
		
		// Stop recurring blink if at max value
		if (currentValue >= maxValue) {
			recurringBlinkActive = false;
			recurringBlinkTimer = 0;
			return;
		}
		
		// Get current tick count
		MinecraftClient client = MinecraftClient.getInstance();
		int currentTick = client.player != null && client.world != null ? 
			(int)client.world.getTime() : 0;
		
		// Calculate interval: N seconds = N * 20 ticks (minimum 1 second = 20 ticks)
		int interval = Math.max(currentIntValue, 1) * 20;
		
		// Continue recurring blink if active
		if (recurringBlinkActive) {
			// Only increment once per tick (prevent multiple increments per tick due to multiple renders)
			if (currentTick != lastTickCount) {
				lastTickCount = currentTick;
				recurringBlinkTimer++;
			}
			
			// Check if we should trigger next blink
			if (recurringBlinkTimer >= interval) {
				shouldBlink = true;
				blinkTimer = 0;
				recurringBlinkTimer = 0; // Reset to 0, will increment to 1 on next tick
			}
		} else {
			// Initialize tick tracking when starting recurring blink
			lastTickCount = currentTick;
		}
	}
	
	protected void triggerDamageBlink(float currentValue, float previousValue) {
		// Trigger immediate blink on damage
		if (currentValue < previousValue) {
			shouldBlink = true;
			blinkTimer = 0;
			recurringBlinkActive = true;
			recurringBlinkTimer = 0; // Always reset timer on damage
			
			// Update interval based on new value
			currentBlinkInterval = Math.max((int)Math.floor(currentValue), 1) * 20;
		}
	}
}