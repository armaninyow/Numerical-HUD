package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class HungerModule extends BaseHudModule {
	
	private static final int COLOR_WHITE = 0xFFFFFFFF;
	private static final int COLOR_RED = 0xFFFF0000;
	private static final int COLOR_GREEN = 0xFF00FF00;
	
	private float lastHunger = Float.MAX_VALUE;
	
	@Override
	public void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta) {
		int hunger = player.getHungerManager().getFoodLevel();
		float saturation = player.getHungerManager().getSaturationLevel();
		boolean hasHungerEffect = player.hasStatusEffect(StatusEffects.HUNGER);
		
		AnimationStyle style = ModConfig.get().animationStyle;
		
		if (style == AnimationStyle.DECIMAL) {
			updateAnimation(hunger, 0.1f);
			triggerDamageBlink(hunger, lastHunger);
		} else {
			tickStyleAnimations(hunger, lastHunger);
			triggerDamageBlink(hunger, lastHunger);
		}
		lastHunger = hunger;
		
		updateBlinkTimer();
		updateRecurringBlink(hunger, 20);
		
		// Determine text color
		int textColor;
		if (hunger == 0) {
			textColor = COLOR_RED;
		} else {
			textColor = getStyledColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		}
		
		// Select textures based on blink state and hunger effect
		Identifier containerTexture;
		Identifier foregroundTexture;
		if (shouldShowBlink()) {
			containerTexture = hasHungerEffect ?
				getTexture("hunger/container_hunger_blinking.png") :
				getTexture("hunger/container_blinking.png");
			foregroundTexture = hasHungerEffect ?
				getTexture("hunger/food_full_hunger_blinking.png") :
				getTexture("hunger/food_full_blinking.png");
		} else {
			containerTexture = hasHungerEffect ?
				getTexture("hunger/food_empty_hunger.png") :
				getTexture("hunger/food_empty.png");
			foregroundTexture = hasHungerEffect ?
				getTexture("hunger/food_full_hunger.png") :
				getTexture("hunger/food_full.png");
		}
		
		Identifier saturationTexture = getTexture("hunger/food_saturation.png");
		
		// Render icon (moved 1px up)
		drawIcon(context, containerTexture, x, y - 1);
		if (hunger > 0) {
			drawIcon(context, foregroundTexture, x, y - 1);
		}
		
		// Render saturation overlay if present
		if (saturation > 0) {
			float saturationAlpha = Math.min(saturation / 20.0f, 1.0f);
			drawIcon(context, saturationTexture, x, y - 1, saturationAlpha);
		}
		
		// Render text (no offset)
		String text = getStyledText(hunger);
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
		
		// Render popup if applicable
		renderPopup(context, x, y);
	}
}