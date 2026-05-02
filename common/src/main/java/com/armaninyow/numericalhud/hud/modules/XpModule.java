package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.XpDataHolder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class XpModule extends BaseHudModule {
	
	private static final int COLOR_GREEN = 0xFF80C71F;
	private static final int COLOR_YELLOW = 0xFFFED83D;
	
	private boolean firstRender = true;
	private float lastLevelPercent = Float.MAX_VALUE;
	
	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta) {
		// Always use the holder data (it's set before player level is hidden)
		int level = XpDataHolder.getRealXpLevel();
		float progress = XpDataHolder.getRealXpProgress();
		
		// Skip rendering if we don't have valid data yet (still at initialization values)
		if (!XpDataHolder.isInitialized()) {
			return;
		}
		
		int currentXp = (int)(progress * getXpForLevel(level));
		int totalXpForLevel = getXpForLevel(level);
		
		// Calculate level.percent
		float levelPercent = level + (currentXp / (float)totalXpForLevel);
		
		// On first render, initialize the animation value to current value (no animation)
		if (firstRender) {
			currentDisplayValue = levelPercent;
			firstRender = false;
		}
		
		// Update animation with faster increment (0.01 steps)
		updateAnimation(levelPercent, 0.01f);
		
		// Vanilla XP bar sprites (182x5) — we slice column 0 + columns 174-181 to get 9x5
		Identifier xpBarBg = Identifier.of("minecraft", "hud/experience_bar_background");
		Identifier xpBarFg = Identifier.of("minecraft", "hud/experience_bar_progress");

		// Trigger blink on XP change
		if (lastLevelPercent != Float.MAX_VALUE && (int)(levelPercent * 100) != (int)(lastLevelPercent * 100)) {
			shouldBlink = true;
			blinkTimer = 0;
			isIncreasing = levelPercent > lastLevelPercent;
		}
		lastLevelPercent = levelPercent;
		updateBlinkTimer();

		// Render icon — background always, foreground only if XP > 0
		// Blink: show only background during blink frames
		// Centered vertically in the 9x9 icon area (offset 2px down to center 5px bar)
		drawVanillaXpBar(context, xpBarBg, x, y + 1);
		if (levelPercent > 0.01f && !shouldShowBlink()) {
			drawVanillaXpBar(context, xpBarFg, x, y + 1);
		}

		// Render text (no offset)
		int color = isAnimating ? COLOR_YELLOW : COLOR_GREEN;
		String text = String.format("%.2f", currentDisplayValue);
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, color);
	}
	
	private int getXpForLevel(int level) {
		if (level <= 15) {
			return 2 * level + 7;
		} else if (level <= 30) {
			return 5 * level - 38;
		} else {
			return 9 * level - 158;
		}
	}
}