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
	private static final int COLOR_SATURATED = 0xFFAD6D3E;
	
	private float lastHunger = Float.MAX_VALUE;
	
	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

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
		} else if (saturation > 0) {
			textColor = COLOR_SATURATED;
		} else {
			textColor = getStyledColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		}
		
		// Select textures based on hunger effect
		Identifier containerTexture = hasHungerEffect ?
			Identifier.of("minecraft", "hud/food_empty_hunger") :
			Identifier.of("minecraft", "hud/food_empty");
		Identifier foregroundTexture = hasHungerEffect ?
			Identifier.of("minecraft", "hud/food_full_hunger") :
			Identifier.of("minecraft", "hud/food_full");

		// Render icon (moved 1px up)
		if (shouldShowBlink()) {
			drawVanillaSprite(context, containerTexture, x, y - 1);
		} else {
			drawVanillaSprite(context, containerTexture, x, y - 1);
			if (hunger > 0) {
				drawVanillaSprite(context, foregroundTexture, x, y - 1);
			}
		}

		
		// Render text (no offset)
		String text = getStyledText(hunger);
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
		
		// Render popup if applicable
		renderPopup(context, x, y);
	}
}