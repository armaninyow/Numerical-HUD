package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;

public class JumpModule extends BaseHudModule {

	private static final int COLOR_WHITE  = 0xFFFFFFFF;
	private static final int COLOR_BLUE   = 0xFF286AB9;
	private static final int COLOR_ORANGE = 0xFFBF7321;

	private static final Identifier JUMP_BG = Identifier.fromNamespaceAndPath("minecraft", "hud/jump_bar_background");
	private static final Identifier JUMP_FG = Identifier.fromNamespaceAndPath("minecraft", "hud/jump_bar_progress");

	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(GuiGraphicsExtractor context, Player player, int x, int y, float tickDelta) {
		Entity vehicle = player.getVehicle();
		if (!(vehicle instanceof AbstractHorse)) {
			return;
		}

		LocalPlayer localPlayer = Minecraft.getInstance().player;
		if (localPlayer == null) return;

		float jumpStrength = localPlayer.getJumpRidingScale();
		boolean isCharging = jumpStrength > 0f;

		drawVanillaJumpBar(context, JUMP_BG, x, y + 1, false);

		int textColor = COLOR_WHITE;

		if (isCharging) {
			if (jumpStrength < 1.0f) {
				drawVanillaJumpBar(context, JUMP_FG, x, y + 1, false);
				textColor = COLOR_BLUE;
			} else {
				drawVanillaJumpBar(context, JUMP_FG, x, y + 1, true);
				textColor = COLOR_ORANGE;
			}
		}

		int percent = isCharging ? Math.min((int)(jumpStrength * 100), 100) : 0;
		drawText(context, percent + "%", x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
	}
}