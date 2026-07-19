package com.armaninyow.numericalhud;

import com.armaninyow.numericalhud.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumericalHud implements ClientModInitializer {
	public static final String MOD_ID = "numericalhud";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Hello Fabric world!");

		ModConfig.load();

		HudElementRegistry.attachElementAfter(
			VanillaHudElements.HOTBAR,
			Identifier.fromNamespaceAndPath(MOD_ID, "hud"),
			new HudRenderer()
		);

		System.out.println("Numerical HUD initialized!");
	}
}