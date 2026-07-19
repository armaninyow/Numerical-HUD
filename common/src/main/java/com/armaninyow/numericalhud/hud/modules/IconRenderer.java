package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public interface IconRenderer {
	void drawIcon(GuiGraphicsExtractor context, Identifier texture, int x, int y, int size, float alpha);
	void drawIcon(GuiGraphicsExtractor context, Identifier texture, int x, int y, int size, int color);
	void drawVanillaSprite(GuiGraphicsExtractor context, Identifier sprite, int x, int y, int size, int color);

	void drawVanillaXpBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y);

	void drawVanillaJumpBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y, boolean rightEnd);

	void drawVanillaBossBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y);

	void flush(GuiGraphicsExtractor context);
}