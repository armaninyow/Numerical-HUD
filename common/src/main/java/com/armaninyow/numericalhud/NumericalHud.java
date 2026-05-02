package com.armaninyow.numericalhud;

import com.armaninyow.numericalhud.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumericalHud implements ClientModInitializer {
	public static final String MOD_ID = "numericalhud";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		ModConfig.register();

		HudRenderCallback.EVENT.register(new HudRenderer());

		System.out.println("Numerical HUD initialized!");
	}
}