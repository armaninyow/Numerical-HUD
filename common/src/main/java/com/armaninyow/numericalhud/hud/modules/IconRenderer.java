package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * Version-specific texture drawing contract.
 * Each version subproject supplies its own implementation.
 */
public interface IconRenderer {
	void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, float alpha);
	void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, int color);
	void drawVanillaSprite(DrawContext context, Identifier sprite, int x, int y, int size, int color);

	/**
	 * Draws a 9x5 slice of the XP bar texture (182x5):
	 * column 0 (1px) + columns 174-181 (8px) = 9px wide, 5px tall.
	 */
	void drawVanillaXpBar(DrawContext context, Identifier sprite, int x, int y);

	/**
	 * Draws a 9x5 slice of a jump bar texture (182x5).
	 * LEFT_END  — column 0 (1px) + columns   1–8   (8px): blue / charging region.
	 * RIGHT_END — column 0 (1px) + columns 174–181 (8px): orange / full-charge region.
	 * Pass LEFT_END for the background (always) and for the progress while < 100%.
	 * Pass RIGHT_END for the progress when fully charged (orange cap).
	 */
	void drawVanillaJumpBar(DrawContext context, Identifier sprite, int x, int y, boolean rightEnd);

	/**
	 * Returns the sprite to use when the player has no air remaining.
	 * Defaults to hud/air_empty (added in 1.21.2).
	 * Override in 1.21/1.21.1 to return hud/air_bursting instead.
	 */
	default Identifier getAirEmptySprite() {
		return Identifier.of("minecraft", "hud/air_empty");
	}

	void flush(DrawContext context);
}