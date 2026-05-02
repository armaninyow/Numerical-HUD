package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

// 1.21.2_1.21.5
public class VersionIconRenderer implements IconRenderer {

	public static final VersionIconRenderer INSTANCE = new VersionIconRenderer();

	@Override
	public void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, float alpha) {
		int alphaInt = (int)(alpha * 255);
		int color = (alphaInt << 24) | 0xFFFFFF;
		context.drawTexture(RenderLayer::getGuiTextured, texture, x, y, 0.0f, 0.0f, size, size, size, size, color);
	}

	@Override
	public void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, int color) {
		context.drawTexture(RenderLayer::getGuiTextured, texture, x, y, 0.0f, 0.0f, size, size, size, size, color);
	}

	@Override
	public void drawVanillaSprite(DrawContext context, Identifier sprite, int x, int y, int size, int color) {
		Sprite s = MinecraftClient.getInstance().getGuiAtlasManager().getSprite(sprite);
		context.drawSpriteStretched(RenderLayer::getGuiTextured, s, x, y, size, size, color);
	}

	@Override
	public void drawVanillaXpBar(DrawContext context, Identifier sprite, int x, int y) {
		context.drawGuiTexture(RenderLayer::getGuiTextured, sprite, 182, 5, 0,   0, x,     y, 1, 5);
		context.drawGuiTexture(RenderLayer::getGuiTextured, sprite, 182, 5, 174, 0, x + 1, y, 8, 5);
	}

	@Override
	public void drawVanillaJumpBar(DrawContext context, Identifier sprite, int x, int y, boolean rightEnd) {
		if (rightEnd) {
			// Orange: first 1 col (left cap) + last 8 cols (right end)
			context.drawGuiTexture(RenderLayer::getGuiTextured, sprite, 182, 5, 0,   0, x,     y, 1, 5);
			context.drawGuiTexture(RenderLayer::getGuiTextured, sprite, 182, 5, 174, 0, x + 1, y, 8, 5);
		} else {
			// Blue / Background: first 8 cols (left body) + last 1 col (right cap)
			context.drawGuiTexture(RenderLayer::getGuiTextured, sprite, 182, 5, 0,   0, x,     y, 8, 5);
			context.drawGuiTexture(RenderLayer::getGuiTextured, sprite, 182, 5, 181, 0, x + 8, y, 1, 5);
		}
	}

	@Override
	public void flush(DrawContext context) {
		context.draw();
	}
}