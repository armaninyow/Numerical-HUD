package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

// 1.21.6_1.21.11
public class VersionIconRenderer implements IconRenderer {

	public static final VersionIconRenderer INSTANCE = new VersionIconRenderer();

	@Override
	public void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, float alpha) {
		int alphaInt = (int)(alpha * 255);
		int color = (alphaInt << 24) | 0xFFFFFF;
		context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0f, 0.0f, size, size, size, size, color);
	}

	@Override
	public void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, int color) {
		context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0f, 0.0f, size, size, size, size, color);
	}

	@Override
	public void drawVanillaSprite(DrawContext context, Identifier sprite, int x, int y, int size, int color) {
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, sprite, x, y, size, size, color);
	}

	@Override
	public void drawVanillaXpBar(DrawContext context, Identifier sprite, int x, int y) {
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 0,   0, x,     y, 1, 5, 0xFFFFFFFF);
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 174, 0, x + 1, y, 8, 5, 0xFFFFFFFF);
	}

	@Override
	public void drawVanillaJumpBar(DrawContext context, Identifier sprite, int x, int y, boolean rightEnd) {
		if (rightEnd) {
			// Orange: first 1 col (left cap) + last 8 cols (right end)
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 0,   0, x,     y, 1, 5, 0xFFFFFFFF);
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 174, 0, x + 1, y, 8, 5, 0xFFFFFFFF);
		} else {
			// Blue / Background: first 8 cols (left body) + last 1 col (right cap)
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 0,   0, x,     y, 8, 5, 0xFFFFFFFF);
			context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 181, 0, x + 8, y, 1, 5, 0xFFFFFFFF);
		}
	}

	@Override
	public void flush(DrawContext context) {
		// 1.21.6+ uses immediate rendering, no flush needed
	}
}