package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ArmorModule extends BaseHudModule {
	
	private static final int COLOR_WHITE = 0xFFFFFFFF;
	private static final int COLOR_RED = 0xFFFF0000;
	private static final int COLOR_GREEN = 0xFF00FF00;
	
	private int lastArmor = 0;
	
	@Override
	public void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta) {
		int armor = player.getArmor();
		int prevArmor = lastArmor;
		
		// Always keep lastArmor current, even when hidden, so animation state
		// is never stale when the module becomes visible again.
		lastArmor = armor;
		
		// Hide module when armor is zero if configured.
		// Also reset currentDisplayValue to 0 so Decimal animation correctly
		// animates from 0→N when the module reappears, not from the last N.
		if (armor == 0 && !ModConfig.get().showArmorWhenZero) {
			resetDisplayValue(0f);
			return;
		}
		
		AnimationStyle style = ModConfig.get().animationStyle;
		
		if (style == AnimationStyle.DECIMAL) {
			updateAnimation(armor, 0.1f);
		} else {
			tickStyleAnimations(armor, prevArmor);
		}
		
		// Trigger blink on armor change
		if (armor != prevArmor) {
			shouldBlink = true;
			blinkTimer = 0;
			isIncreasing = armor > prevArmor;
		}
		
		// Select textures
		Identifier containerTexture = getTexture("armor/armor_empty.png");
		Identifier foregroundTexture = shouldShowBlink() ?
			getTexture("armor/armor_blinking.png") :
			getTexture("armor/armor_full.png");
		
		// Render icon (moved 1px up)
		if (armor == 0) {
			drawIcon(context, containerTexture, x, y - 1);
		}
		if (armor > 0) {
			drawIcon(context, foregroundTexture, x, y - 1);
		}
		
		// Render text (no offset)
		int color = getStyledColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		String text = getStyledText(armor);
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, color);
		
		// Render popup if applicable
		renderPopup(context, x, y);
		
		updateBlinkTimer();
	}
}