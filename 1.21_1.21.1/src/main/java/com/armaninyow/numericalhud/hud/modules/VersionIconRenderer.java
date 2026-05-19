package com.armaninyow.numericalhud.hud.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

// 1.21_1.21.1
public class VersionIconRenderer implements IconRenderer {

	public static final VersionIconRenderer INSTANCE = new VersionIconRenderer();

	@Override
	public void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, float alpha) {
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		context.drawTexture(texture, x, y, 0.0f, 0.0f, size, size, size, size);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, int color) {
		float a = ((color >> 24) & 0xFF) / 255.0f;
		float r = ((color >> 16) & 0xFF) / 255.0f;
		float g = ((color >>  8) & 0xFF) / 255.0f;
		float b = ( color        & 0xFF) / 255.0f;
		RenderSystem.setShaderColor(r, g, b, a);
		context.drawTexture(texture, x, y, 0.0f, 0.0f, size, size, size, size);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void drawVanillaSprite(DrawContext context, Identifier sprite, int x, int y, int size, int color) {
		Sprite s = MinecraftClient.getInstance().getGuiAtlasManager().getSprite(sprite);
		float a = ((color >> 24) & 0xFF) / 255.0f;
		float r = ((color >> 16) & 0xFF) / 255.0f;
		float g = ((color >>  8) & 0xFF) / 255.0f;
		float b = ( color        & 0xFF) / 255.0f;
		context.drawSprite(x, y, 0, size, size, s, r, g, b, a);
	}

	@Override
	public void drawVanillaXpBar(DrawContext context, Identifier sprite, int x, int y) {
		context.drawGuiTexture(sprite, 182, 5, 0,   0, x,     y, 1, 5);
		context.drawGuiTexture(sprite, 182, 5, 174, 0, x + 1, y, 8, 5);
	}

	@Override
	public void drawVanillaJumpBar(DrawContext context, Identifier sprite, int x, int y, boolean rightEnd) {
		if (rightEnd) {
			context.drawGuiTexture(sprite, 182, 5, 0,   0, x,     y, 1, 5);
			context.drawGuiTexture(sprite, 182, 5, 174, 0, x + 1, y, 8, 5);
		} else {
			context.drawGuiTexture(sprite, 182, 5, 0,   0, x,     y, 8, 5);
			context.drawGuiTexture(sprite, 182, 5, 181, 0, x + 8, y, 1, 5);
		}
	}

	@Override
	public Identifier getAirEmptySprite() {
		return Identifier.of("minecraft", "hud/air_bursting");
	}

	@Override
	public void flush(DrawContext context) {
		context.draw();
	}
}