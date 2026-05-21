package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;

public class HungerModule extends BaseHudModule {
	
	private static final int COLOR_WHITE    = 0xFFFFFFFF;
	private static final int COLOR_RED      = 0xFFFF0000;
	private static final int COLOR_GREEN    = 0xFF00FF00;
	private static final int COLOR_SATURATED = 0xFFAD6D3E;
	
	private float lastHunger = Float.MAX_VALUE;

	// Vanilla-style blink state
	private long hungerBlinkTime = 0;
	private int lastHungerInt = -1;

	// Recurring blink state (tickCount-based)
	private long nextRecurringBlink = -1;

	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(GuiGraphicsExtractor context, Player player, int x, int y, float tickDelta) {
		int hunger = player.getFoodData().getFoodLevel();
		float saturation = player.getFoodData().getSaturationLevel();
		boolean hasHungerEffect = player.hasEffect(MobEffects.HUNGER);
		
		AnimationStyle style = ModConfig.get().animationStyle;
		
		if (style == AnimationStyle.DECIMAL) {
			updateAnimation(hunger, 0.1f);
		} else {
			tickStyleAnimations(hunger, lastHunger);
		}
		lastHunger = hunger;

		int tickCount = Minecraft.getInstance().gui.getGuiTicks();
		int hungerInt = hunger;

		// Vanilla-style damage/heal blink
		if (lastHungerInt >= 0) {
			if (hungerInt < lastHungerInt) {
				hungerBlinkTime = tickCount + 20;
				if (nextRecurringBlink < 0) nextRecurringBlink = tickCount + Math.max(hungerInt, 1) * 20;
			} else if (hungerInt > lastHungerInt) {
				hungerBlinkTime = tickCount + 10;
			}
		} else {
			// First render — initialize recurring blink if below max hunger
			if (hunger < 20) {
				nextRecurringBlink = tickCount + Math.max(hunger, 1) * 20;
			}
		}
		lastHungerInt = hungerInt;

		// Recurring blink — 20-tick window for multiple flashes
		if (hunger < 20 && nextRecurringBlink >= 0) {
			if (tickCount >= nextRecurringBlink) {
				hungerBlinkTime = tickCount + 20;
				nextRecurringBlink = tickCount + Math.max(hunger, 1) * 20;
			}
		} else if (hunger >= 20) {
			nextRecurringBlink = -1;
		}

		boolean blink = hungerBlinkTime > tickCount
			&& ((hungerBlinkTime - tickCount) / 3) % 2 == 1;

		// Text color
		int textColor;
		if (hunger == 0) {
			textColor = COLOR_RED;
		} else if (saturation > 0) {
			textColor = COLOR_SATURATED;
		} else {
			textColor = getStyledColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		}
		
		Identifier containerTexture = hasHungerEffect ?
			Identifier.fromNamespaceAndPath("minecraft", "hud/food_empty_hunger") :
			Identifier.fromNamespaceAndPath("minecraft", "hud/food_empty");
		Identifier foregroundTexture = hasHungerEffect ?
			Identifier.fromNamespaceAndPath("minecraft", "hud/food_full_hunger") :
			Identifier.fromNamespaceAndPath("minecraft", "hud/food_full");

		if (blink) {
			drawVanillaSprite(context, containerTexture, x, y - 1);
		} else {
			drawVanillaSprite(context, containerTexture, x, y - 1);
			if (hunger > 0) {
				drawVanillaSprite(context, foregroundTexture, x, y - 1);
			}
		}

		String text = getStyledText(hunger);
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
		
		renderPopup(context, x, y);
	}
}