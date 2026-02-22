package com.armaninyow.numericalhud;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = NumericalHud.MOD_ID)
public class ModConfig implements ConfigData {
	
	@ConfigEntry.Gui.Tooltip
	public AnimationStyle animationStyle = AnimationStyle.DECIMAL;
	
	@ConfigEntry.Gui.Tooltip
	public boolean showArmorWhenZero = true;
	
	public static ModConfig get() {
		return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
	}
	
	public static void register() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
	}
}