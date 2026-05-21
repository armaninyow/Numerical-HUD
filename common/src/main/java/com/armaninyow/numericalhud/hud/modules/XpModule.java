package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.XpDataHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;

public class XpModule extends BaseHudModule {
	
	private static final int COLOR_GREEN  = 0xFF80C71F;
	private static final int COLOR_YELLOW = 0xFFFED83D;
	
	private boolean firstRender = true;
	private float lastLevelPercent = Float.MAX_VALUE;

	// Vanilla-style blink state
	private long xpBlinkTime = 0;
	
	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(GuiGraphicsExtractor context, Player player, int x, int y, float tickDelta) {
		int level = XpDataHolder.getRealXpLevel();
		float progress = XpDataHolder.getRealXpProgress();
		
		if (!XpDataHolder.isInitialized()) {
			return;
		}
		
		int currentXp = (int)(progress * getXpForLevel(level));
		int totalXpForLevel = getXpForLevel(level);
		float levelPercent = level + (currentXp / (float)totalXpForLevel);
		
		if (firstRender) {
			currentDisplayValue = levelPercent;
			firstRender = false;
		}
		
		updateAnimation(levelPercent, 0.01f);

		int tickCount = Minecraft.getInstance().gui.getGuiTicks();

		if (lastLevelPercent != Float.MAX_VALUE) {
			int prev = (int)(lastLevelPercent * 100);
			int curr = (int)(levelPercent * 100);
			if (curr != prev) {
				isIncreasing = levelPercent > lastLevelPercent;
				xpBlinkTime = tickCount + (isIncreasing ? 10 : 20);
			}
		}
		lastLevelPercent = levelPercent;

		boolean blink = xpBlinkTime > tickCount
			&& ((xpBlinkTime - tickCount) / 3) % 2 == 1;

		Identifier xpBarBg = Identifier.fromNamespaceAndPath("minecraft", "hud/experience_bar_background");
		Identifier xpBarFg = Identifier.fromNamespaceAndPath("minecraft", "hud/experience_bar_progress");

		drawVanillaXpBar(context, xpBarBg, x, y + 1);
		if (levelPercent > 0.01f && !blink) {
			drawVanillaXpBar(context, xpBarFg, x, y + 1);
		}

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