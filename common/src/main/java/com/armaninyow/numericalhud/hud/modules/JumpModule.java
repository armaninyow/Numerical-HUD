package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class JumpModule extends BaseHudModule {

	private static final int COLOR_WHITE  = 0xFFFFFFFF;
	private static final int COLOR_BLUE   = 0xFF286AB9;
	private static final int COLOR_ORANGE = 0xFFBF7321;

	private static final Identifier JUMP_BG = Identifier.of("minecraft", "hud/jump_bar_background");
	private static final Identifier JUMP_FG = Identifier.of("minecraft", "hud/jump_bar_progress");

	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta) {
		Entity vehicle = player.getVehicle();
		if (!(vehicle instanceof AbstractHorseEntity)) {
			return;
		}

		ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
		if (clientPlayer == null) return;

		float jumpStrength = clientPlayer.getMountJumpStrength();
		boolean isCharging = jumpStrength > 0f;

		// Background — column 0 + columns 1-8 (left region), always visible
		drawVanillaJumpBar(context, JUMP_BG, x, y + 1, false);

		int textColor = COLOR_WHITE;

		if (isCharging) {
			if (jumpStrength < 1.0f) {
				// Blue phase: left region of progress bar
				drawVanillaJumpBar(context, JUMP_FG, x, y + 1, false);
				textColor = COLOR_BLUE;
			} else {
				// Orange phase: right region of progress bar (col 0 + cols 174-181)
				drawVanillaJumpBar(context, JUMP_FG, x, y + 1, true);
				textColor = COLOR_ORANGE;
			}
		}

		int percent = isCharging ? Math.min((int)(jumpStrength * 100), 100) : 0;
		drawText(context, percent + "%", x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
	}
}