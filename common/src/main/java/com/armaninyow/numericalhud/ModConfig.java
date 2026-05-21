package com.armaninyow.numericalhud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance()
			.getConfigDir().resolve("numericalhud.json");

	private static ModConfig INSTANCE = new ModConfig();

	public AnimationStyle animationStyle = AnimationStyle.DECIMAL;
	public boolean showArmorWhenZero = true;
	public boolean shiftModulesDown = false;
	public TextStyle textStyle = TextStyle.SHADOW;

	public boolean showEffectNames = true;

	public static ModConfig get() {
		return INSTANCE;
	}

	public static void load() {
		if (Files.exists(CONFIG_PATH)) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
				ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
				if (loaded != null) {
					INSTANCE = loaded;
				}
			} catch (IOException e) {
				NumericalHud.LOGGER.error("Failed to load config", e);
			}
		}
	}

	public static void save() {
		try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
			GSON.toJson(INSTANCE, writer);
		} catch (IOException e) {
			NumericalHud.LOGGER.error("Failed to save config", e);
		}
	}
}