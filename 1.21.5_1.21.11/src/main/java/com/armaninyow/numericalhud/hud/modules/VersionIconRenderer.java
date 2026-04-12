package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * IconRenderer for Minecraft 1.21.5 – 1.21.11.
 * Uses the new DrawContext.drawTexture signature with RenderPipeline and ARGB color int.
 */
public class VersionIconRenderer implements IconRenderer {

	public static final VersionIconRenderer INSTANCE = new VersionIconRenderer();

	@Override
	public void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, float alpha) {
		int alphaInt = (int)(alpha * 255);
		int color = (alphaInt << 24) | 0xFFFFFF;
		context.drawTexture(
			RenderPipelines.GUI_TEXTURED,
			texture,
			x, y,
			0.0f, 0.0f,
			size, size,
			size, size,
			color
		);
	}
}
