package com.armaninyow.numericalhud.hud.modules;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class VersionIconRenderer implements IconRenderer {

	public static final VersionIconRenderer INSTANCE = new VersionIconRenderer();

	@Override
	public void drawIcon(GuiGraphicsExtractor context, Identifier texture, int x, int y, int size, float alpha) {
		int alphaInt = (int)(alpha * 255);
		int color = (alphaInt << 24) | 0xFFFFFF;
		context.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0f, 0.0f, size, size, size, size, color);
	}

	@Override
	public void drawIcon(GuiGraphicsExtractor context, Identifier texture, int x, int y, int size, int color) {
		context.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0f, 0.0f, size, size, size, size, color);
	}

	@Override
	public void drawVanillaSprite(GuiGraphicsExtractor context, Identifier sprite, int x, int y, int size, int color) {
		context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, size, size, color);
	}

	@Override
	public void drawVanillaXpBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y) {
		context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 0,   0, x,     y, 1, 5, 0xFFFFFFFF);
		context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 174, 0, x + 1, y, 8, 5, 0xFFFFFFFF);
	}

	@Override
	public void drawVanillaJumpBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y, boolean rightEnd) {
		if (rightEnd) {
			context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 0,   0, x,     y, 1, 5, 0xFFFFFFFF);
			context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 174, 0, x + 1, y, 8, 5, 0xFFFFFFFF);
		} else {
			context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 0,   0, x,     y, 8, 5, 0xFFFFFFFF);
			context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 181, 0, x + 8, y, 1, 5, 0xFFFFFFFF);
		}
	}

	@Override
	public void drawVanillaBossBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y) {
		context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 0,   0, x,     y, 1, 5, 0xFFFFFFFF);
		context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 182, 5, 174, 0, x + 1, y, 8, 5, 0xFFFFFFFF);
	}

	@Override
	public void flush(GuiGraphicsExtractor context) {
		// 26.1 uses immediate rendering, no flush needed
	}
}