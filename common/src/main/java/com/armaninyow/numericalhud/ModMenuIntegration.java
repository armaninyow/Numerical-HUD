package com.armaninyow.numericalhud;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create()
					.setParentScreen(parent)
					.setTitle(Text.translatable("text.autoconfig.numericalhud.title"));

			ModConfig config = ModConfig.get();
			ConfigEntryBuilder entryBuilder = builder.entryBuilder();
			ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));

			// Animation Style — enum selector
			general.addEntry(entryBuilder.startEnumSelector(
					Text.translatable("text.autoconfig.numericalhud.option.animationStyle"),
					AnimationStyle.class,
					config.animationStyle)
					.setDefaultValue(AnimationStyle.DECIMAL)
					.setTooltip(Text.translatable("text.autoconfig.numericalhud.option.animationStyle.@Tooltip"))
					.setSaveConsumer(newValue -> config.animationStyle = newValue)
					.build());

			// Show Armor When Zero — boolean toggle
			general.addEntry(entryBuilder.startBooleanToggle(
					Text.translatable("text.autoconfig.numericalhud.option.showArmorWhenZero"),
					config.showArmorWhenZero)
					.setDefaultValue(true)
					.setTooltip(Text.translatable("text.autoconfig.numericalhud.option.showArmorWhenZero.@Tooltip"))
					.setSaveConsumer(newValue -> config.showArmorWhenZero = newValue)
					.build());

			builder.setSavingRunnable(() -> AutoConfig.getConfigHolder(ModConfig.class).save());

			return builder.build();
		};
	}
}
