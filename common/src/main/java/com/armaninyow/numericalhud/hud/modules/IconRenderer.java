package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

/**
 * Version-specific texture drawing contract.
 * Each version subproject supplies its own implementation.
 */
public interface IconRenderer {
	void drawIcon(GuiGraphicsExtractor context, Identifier texture, int x, int y, int size, float alpha);
	void drawIcon(GuiGraphicsExtractor context, Identifier texture, int x, int y, int size, int color);
	void drawVanillaSprite(GuiGraphicsExtractor context, Identifier sprite, int x, int y, int size, int color);

	/**
	 * Draws a 9x5 slice of the XP bar texture (182x5):
	 * column 0 (1px) + columns 174-181 (8px) = 9px wide, 5px tall.
	 */
	void drawVanillaXpBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y);

	/**
	 * Draws a 9x5 slice of a jump bar texture (182x5).
	 * LEFT_END  — column 0 (1px) + columns   1–8   (8px): blue / charging region.
	 * RIGHT_END — column 0 (1px) + columns 174–181 (8px): orange / full-charge region.
	 * Pass LEFT_END for the background (always) and for the progress while < 100%.
	 * Pass RIGHT_END for the progress when fully charged (orange cap).
	 */
	void drawVanillaJumpBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y, boolean rightEnd);

	/**
	 * Draws a 9x5 slice of a boss bar texture (182x5):
	 * column 0 (1px) + columns 174-181 (8px) = 9px wide, 5px tall.
	 */
	void drawVanillaBossBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y);

	void flush(GuiGraphicsExtractor context);
}