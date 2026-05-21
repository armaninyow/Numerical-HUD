package com.armaninyow.numericalhud;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> YetAnotherConfigLib.createBuilder()
				.title(Component.translatable("text.autoconfig.numericalhud.title"))
				.category(ConfigCategory.createBuilder()
						.name(Component.literal("General"))

						.option(Option.<AnimationStyle>createBuilder()
								.name(Component.translatable("text.autoconfig.numericalhud.option.animationStyle"))
								.description(OptionDescription.of(Component.translatable("text.autoconfig.numericalhud.option.animationStyle.@Tooltip")))
								.binding(AnimationStyle.DECIMAL, () -> ModConfig.get().animationStyle, val -> ModConfig.get().animationStyle = val)
								.controller(opt -> EnumControllerBuilder.create(opt).enumClass(AnimationStyle.class))
								.build())

						.option(Option.<TextStyle>createBuilder()
								.name(Component.translatable("text.autoconfig.numericalhud.option.textStyle"))
								.description(OptionDescription.of(Component.translatable("text.autoconfig.numericalhud.option.textStyle.@Tooltip")))
								.binding(TextStyle.SHADOW, () -> ModConfig.get().textStyle, val -> ModConfig.get().textStyle = val)
								.controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextStyle.class))
								.build())

						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("text.autoconfig.numericalhud.option.showEffectNames"))
								.description(OptionDescription.of(Component.translatable("text.autoconfig.numericalhud.option.showEffectNames.@Tooltip")))
								.binding(true, () -> ModConfig.get().showEffectNames, val -> ModConfig.get().showEffectNames = val)
								.controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter())
								.build())

						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("text.autoconfig.numericalhud.option.showArmorWhenZero"))
								.description(OptionDescription.of(Component.translatable("text.autoconfig.numericalhud.option.showArmorWhenZero.@Tooltip")))
								.binding(true, () -> ModConfig.get().showArmorWhenZero, val -> ModConfig.get().showArmorWhenZero = val)
								.controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter())
								.build())

						.option(Option.<Boolean>createBuilder()
								.name(Component.translatable("text.autoconfig.numericalhud.option.shiftModulesDown"))
								.description(OptionDescription.of(Component.translatable("text.autoconfig.numericalhud.option.shiftModulesDown.@Tooltip")))
								.binding(false, () -> ModConfig.get().shiftModulesDown, val -> ModConfig.get().shiftModulesDown = val)
								.controller(opt -> BooleanControllerBuilder.create(opt).yesNoFormatter())
								.build())

						.build())
				.save(ModConfig::save)
				.build()
				.generateScreen(parent);
	}
}