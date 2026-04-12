package com.armaninyow.numericalhud.hud.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * IconRenderer for Minecraft 1.21 – 1.21.1.
 * Uses the legacy DrawContext.drawTexture signature (no RenderPipeline argument).
 * Alpha is applied via RenderSystem.setShaderColor.
 */
public class VersionIconRenderer implements IconRenderer {

	public static final VersionIconRenderer INSTANCE = new VersionIconRenderer();

	@Override
	public void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, float alpha) {
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		context.drawTexture(
			texture,
			x, y,
			0.0f, 0.0f,
			size, size,
			size, size
		);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
