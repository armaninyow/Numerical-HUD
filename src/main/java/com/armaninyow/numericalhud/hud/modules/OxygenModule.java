package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
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
	private float lastAirSecondsFloat = Float.MAX_VALUE;
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
		
		AnimationStyle style = ModConfig.get().animationStyle;
		
		if (style == AnimationStyle.DECIMAL) {
			updateAnimation(airSeconds, 0.1f);
		} else {
			tickStyleAnimations(airSeconds, lastAirSecondsFloat);
		}
		lastAirSecondsFloat = airSeconds;
		
		// Determine if we should animate the icon
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
			drawIcon(context, getTexture("oxygen/air_empty.png"), x, y - 1, 1.0f);
		} else if (isPopping) {
			popAnimationTick++;
			
			if (popAnimationTick <= 5) {
				float t = popAnimationTick / 5.0f;
				float alpha = 1.0f - (t * t);
				drawIcon(context, getTexture("oxygen/air_bursting.png"), x, y - 1, alpha);
			} else {
				float t = (popAnimationTick - 5) / 15.0f;
				float alpha = t * t;
				drawIcon(context, getTexture("oxygen/air.png"), x, y - 1, alpha);
			}
			
			if (popAnimationTick >= 20) {
				isPopping = false;
				popAnimationTick = 0;
			}
		} else if (isPushing) {
			pushAnimationTick++;
			float progress = pushAnimationTick / 10.0f;
			
			context.enableScissor(x, y - 1, x + ICON_SIZE, y - 1 + ICON_SIZE);
			
			int outgoingOffset = (int)(-9 * progress);
			drawIcon(context, getTexture("oxygen/air.png"), x, y - 1 + outgoingOffset, 1.0f);
			
			int incomingOffset = (int)(9 - (9 * progress));
			drawIcon(context, getTexture("oxygen/air.png"), x, y - 1 + incomingOffset, 1.0f);
			
			context.disableScissor();
			
			if (pushAnimationTick >= 10) {
				isPushing = false;
				pushAnimationTick = 0;
			}
		} else {
			drawIcon(context, getTexture("oxygen/air.png"), x, y - 1, 1.0f);
		}
		
		// Render text (no offset, no "s")
		int textColor;
		if (air <= 0) {
			textColor = COLOR_RED;
		} else {
			textColor = getStyledColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		}
		
		String text;
		if (hasWaterBreathing) {
			text = "∞";
		} else {
			text = getStyledText(airSeconds);
		}
		
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
		
		// Render popup if applicable
		renderPopup(context, x, y);
	}
}