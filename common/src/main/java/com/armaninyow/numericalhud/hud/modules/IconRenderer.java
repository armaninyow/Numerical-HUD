package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * Version-specific texture drawing contract.
 * Each version subproject supplies its own implementation.
 */
public interface IconRenderer {
	void drawIcon(DrawContext context, Identifier texture, int x, int y, int size, float alpha);
}
