package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

/**
 * IconRenderer for Minecraft 1.21.2 – 1.21.4.
 * DrawContext.drawTexture requires a Function<Identifier, RenderLayer> as the
 * first argument. Alpha is encoded in the ARGB color int (11-arg overload).
 */
public class VersionIconRenderer implements IconRenderer {

	public static final VersionIconRenderer INSTANCE = new VersionIconRenderer();

	@Override
	public void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, float alpha) {
		int alphaInt = (int)(alpha * 255);
		int color = (alphaInt << 24) | 0xFFFFFF;
		context.drawTexture(
			RenderLayer::getGuiTextured,
			texture,
			x, y,
			0.0f, 0.0f,
			size, size,
			size, size,
			color
		);
	}
}
