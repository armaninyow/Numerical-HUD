package com.armaninyow.numericalhud.hud;

import com.armaninyow.numericalhud.hud.modules.*;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;

public class HudRenderer implements HudRenderCallback {

	private final HealthModule healthModule = new HealthModule();
	private final ArmorModule armorModule = new ArmorModule();
	private final HungerModule hungerModule = new HungerModule();
	private final XpModule xpModule = new XpModule();
	private final OxygenModule oxygenModule = new OxygenModule();
	private final VehicleHealthModule vehicleHealthModule = new VehicleHealthModule();
	private final JumpModule jumpModule = new JumpModule();

	@Override
	public void onHudRender(DrawContext context, RenderTickCounter renderTickCounter) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.player == null || client.options.hudHidden) {
			return;
		}

		PlayerEntity player = client.player;

		int screenWidth = context.getScaledWindowWidth();
		int screenHeight = context.getScaledWindowHeight();

		// XP bar dimensions (same as vanilla)
		int xpBarWidth = 182;
		int centerX = screenWidth / 2;
		int xpBarLeft = centerX - (xpBarWidth / 2);
		int xpBarRight = xpBarLeft + xpBarWidth;

		// Position modules above the XP bar area.
		// If shiftModulesDown is enabled, shift 6px down since the vanilla XP bar is hidden.
		int baseY = com.armaninyow.numericalhud.ModConfig.get().shiftModulesDown ? 32 : 38;
		int moduleY = screenHeight - baseY;

		int healthX = xpBarLeft;
		int armorX = xpBarLeft + (xpBarWidth / 4);
		int hungerX = xpBarLeft + (xpBarWidth / 2);
		int xpX = xpBarLeft + (3 * xpBarWidth / 4);

		// Render main row
		healthModule.render(context, player, healthX, moduleY, 0);
		armorModule.render(context, player, armorX, moduleY, 0);
		hungerModule.render(context, player, hungerX, moduleY, 0);
		xpModule.render(context, player, xpX, moduleY, 0);

		// Oxygen module (12 pixels above health module) - show when underwater OR when air < max
		if (player.isSubmergedInWater() || player.getAir() < 300) {
			oxygenModule.render(context, player, healthX, moduleY - 10, 0);
		}

		// Second row: vehicle health (above XP slot) and jump bar (above hunger slot).
		// Both are only relevant when riding a vehicle.
		if (player.hasVehicle() && player.getVehicle() != null) {
			vehicleHealthModule.render(context, player, xpX, moduleY - 10, 0);
			jumpModule.render(context, player, hungerX, moduleY - 10, 0);
		}

		// Flush buffered GUI atlas sprite draws
		healthModule.flush(context);
	}
}