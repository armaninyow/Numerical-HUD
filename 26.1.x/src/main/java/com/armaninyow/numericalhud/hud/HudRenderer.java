package com.armaninyow.numericalhud.hud;

import com.armaninyow.numericalhud.hud.modules.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.player.Player;

public class HudRenderer implements HudElement {

	private final HealthModule healthModule = new HealthModule();
	private final ArmorModule armorModule = new ArmorModule();
	private final HungerModule hungerModule = new HungerModule();
	private final XpModule xpModule = new XpModule();
	private final OxygenModule oxygenModule = new OxygenModule();
	private final VehicleHealthModule vehicleHealthModule = new VehicleHealthModule();
	private final JumpModule jumpModule = new JumpModule();
	private final BossBarModule bossBarModule = new BossBarModule();
	private final EffectsModule effectsModule = new EffectsModule();

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, DeltaTracker deltaTracker) {
		Minecraft client = Minecraft.getInstance();

		if (client.player == null || client.options.hideGui) {
			return;
		}

		Player player = client.player;

		bossBarModule.render(context, player, 0, 0, 0);
		effectsModule.render(context, player, 0, 0, 0);

		if (player.isCreative()) {
			return;
		}

		int screenWidth = context.guiWidth();
		int screenHeight = context.guiHeight();

		int xpBarWidth = 182;
		int centerX = screenWidth / 2;
		int xpBarLeft = centerX - (xpBarWidth / 2);
		int xpBarRight = xpBarLeft + xpBarWidth;

		int baseY = com.armaninyow.numericalhud.ModConfig.get().shiftModulesDown ? 32 : 38;
		int moduleY = screenHeight - baseY;

		int healthX = xpBarLeft;
		int armorX = xpBarLeft + (xpBarWidth / 4);
		int hungerX = xpBarLeft + (xpBarWidth / 2);
		int xpX = xpBarLeft + (3 * xpBarWidth / 4);

		healthModule.render(context, player, healthX, moduleY, 0);
		armorModule.render(context, player, armorX, moduleY, 0);
		hungerModule.render(context, player, hungerX, moduleY, 0);
		xpModule.render(context, player, xpX, moduleY, 0);

		if (player.isUnderWater() || player.getAirSupply() < 300) {
			oxygenModule.render(context, player, healthX, moduleY - 10, 0);
		}

		if (player.isPassenger() && player.getVehicle() != null) {
			vehicleHealthModule.render(context, player, xpX, moduleY - 10, 0);
			jumpModule.render(context, player, hungerX, moduleY - 10, 0);
		}

		healthModule.flush(context);
	}
}