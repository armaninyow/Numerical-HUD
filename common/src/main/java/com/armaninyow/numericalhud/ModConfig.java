package com.armaninyow.numericalhud;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = NumericalHud.MOD_ID)
public class ModConfig implements ConfigData {

	public AnimationStyle animationStyle = AnimationStyle.DECIMAL;

	public boolean showArmorWhenZero = true;

	public static ModConfig get() {
		return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}

	public static void register() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
	}
}
