package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.player.Player;

public class JumpModule extends BaseHudModule {

	private static final int COLOR_WHITE    = 0xFFFFFFFF;
	private static final int COLOR_BLUE     = 0xFF21497B;
	private static final int COLOR_ORANGE   = 0xFFEB7114;
	private static final int COLOR_COOLDOWN = 0xFFAA00AA;

	private static final int ORANGE_THRESHOLD = 90;

	private static final Identifier JUMP_BG       = Identifier.fromNamespaceAndPath("minecraft", "hud/jump_bar_background");
	private static final Identifier JUMP_FG       = Identifier.fromNamespaceAndPath("minecraft", "hud/jump_bar_progress");
	private static final Identifier JUMP_COOLDOWN = Identifier.fromNamespaceAndPath("minecraft", "hud/jump_bar_cooldown");

	private int lastChargePercent = 0;
	private boolean wasOnCooldown = false;

	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(GuiGraphicsExtractor context, Player player, int x, int y, float tickDelta) {
		LocalPlayer localPlayer = Minecraft.getInstance().player;
		if (localPlayer == null) return;

		PlayerRideableJumping jumpableVehicle = localPlayer.jumpableVehicle();
		if (jumpableVehicle == null) {
			wasOnCooldown = false;
			return;
		}

		drawVanillaJumpBar(context, JUMP_BG, x, y + 1, false);

		boolean isOnCooldown = jumpableVehicle.getJumpCooldown() > 0;

		if (isOnCooldown) {
			wasOnCooldown = true;

			drawVanillaJumpBar(context, JUMP_COOLDOWN, x, y + 1, false);
			drawText(context, lastChargePercent + "%", x + ICON_SIZE + ICON_TEXT_GAP, y, COLOR_COOLDOWN);
			return;
		}
		wasOnCooldown = false;

		float jumpStrength = localPlayer.getJumpRidingScale();
		boolean isCharging = jumpStrength > 0f;

		int textColor = COLOR_WHITE;

		if (isCharging) {
			if (jumpStrength * 100f < ORANGE_THRESHOLD) {
				drawVanillaJumpBar(context, JUMP_FG, x, y + 1, false);
				textColor = COLOR_BLUE;
			} else {
				drawVanillaJumpBar(context, JUMP_FG, x, y + 1, true);
				textColor = COLOR_ORANGE;
			}
		}

		int percent = isCharging ? Math.min((int) (jumpStrength * 100), 100) : 0;
		lastChargePercent = percent;
		drawText(context, percent + "%", x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
	}
}