package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class OxygenModule extends BaseHudModule {
	
	private static final int COLOR_WHITE = 0xFFFFFFFF;
	private static final int COLOR_RED = 0xFFFF0000;
	private static final int COLOR_GREEN = 0xFF00FF00;
	private static final int MAX_AIR = 300;
	
	private int lastAirSeconds = 15;
	private int popAnimationTick = 0;
	private int pushAnimationTick = 0;
	private boolean isPopping = false;
	private boolean isPushing = false;
	
	@Override
	public void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta) {
		int air = player.getAir();
		int airSeconds = air / 20;
		boolean hasWaterBreathing = player.hasStatusEffect(StatusEffects.WATER_BREATHING) || 
								   player.hasStatusEffect(StatusEffects.CONDUIT_POWER);
		
		// Update animation (with decimals like other modules)
		updateAnimation(airSeconds, 0.1f);
		
		// Determine if we should animate
		if (airSeconds < lastAirSeconds && !isPopping && air > 0) {
			isPopping = true;
			popAnimationTick = 0;
		} else if (airSeconds > lastAirSeconds && !isPushing) {
			isPushing = true;
			pushAnimationTick = 0;
		}
		lastAirSeconds = airSeconds;
		
		// Render icon (moved 1px up)
		if (air <= 0) {
			// Show empty texture when no air
			drawIcon(context, getTexture("oxygen/air_empty.png"), x, y - 1, 1.0f);
		} else if (isPopping) {
			// Pop animation (20 ticks total)
			popAnimationTick++;
			
			if (popAnimationTick <= 5) {
				// Burst phase (ticks 1-5): fading burst
				float t = popAnimationTick / 5.0f;
				float alpha = 1.0f - (t * t); // Quadratic ease-out
				drawIcon(context, getTexture("oxygen/air_bursting.png"), x, y - 1, alpha);
			} else {
				// Recovery phase (ticks 6-20): fading in bubble
				float t = (popAnimationTick - 5) / 15.0f;
				float alpha = t * t; // Quadratic ease-out fade-in
				drawIcon(context, getTexture("oxygen/air.png"), x, y - 1, alpha);
			}
			
			if (popAnimationTick >= 20) {
				isPopping = false;
				popAnimationTick = 0;
			}
		} else if (isPushing) {
			// Push animation (10 ticks) with clipping
			pushAnimationTick++;
			float progress = pushAnimationTick / 10.0f;
			
			// Enable scissor clipping to 9x9 box
			context.enableScissor(x, y - 1, x + ICON_SIZE, y - 1 + ICON_SIZE);
			
			// Outgoing bubble (moves up and out: 0 → -9)
			int outgoingOffset = (int)(-9 * progress);
			drawIcon(context, getTexture("oxygen/air.png"), x, y - 1 + outgoingOffset, 1.0f);
			
			// Incoming bubble (moves up into view: 9 → 0)
			int incomingOffset = (int)(9 - (9 * progress));
			drawIcon(context, getTexture("oxygen/air.png"), x, y - 1 + incomingOffset, 1.0f);
			
			context.disableScissor();
			
			if (pushAnimationTick >= 10) {
				isPushing = false;
				pushAnimationTick = 0;
			}
		} else {
			// Normal rendering
			drawIcon(context, getTexture("oxygen/air.png"), x, y - 1, 1.0f);
		}
		
		// Render text (no offset, no "s")
		// Determine text color
		int textColor;
		if (air <= 0) {
			textColor = COLOR_RED; // Always red when drowning
		} else {
			textColor = getAnimationColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		}
		
		// Format text
		String text;
		if (hasWaterBreathing) {
			text = "∞";
		} else {
			text = formatValue(currentDisplayValue, isAnimating);
		}
		
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
	}
}