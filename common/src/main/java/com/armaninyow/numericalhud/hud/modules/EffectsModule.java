package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EffectsModule extends BaseHudModule {

	private static final int EFFECT_SIZE   = 18;
	private static final int ROW_HEIGHT    = 22;
	private static final int TEXT_COLOR    = 0xFFFFFFFF;
	private static final int AMBIENT_COLOR = 0xFF55FFFF;
	private static final int MARGIN_RIGHT  = 3;

	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(GuiGraphicsExtractor context, Player player, int x, int y, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		Collection<MobEffectInstance> effects = player.getActiveEffects();
		if (effects.isEmpty()) return;

		List<MobEffectInstance> visible = new ArrayList<>();
		for (MobEffectInstance effect : effects) {
			if (effect.showIcon()) visible.add(effect);
		}
		if (visible.isEmpty()) return;

		int screenWidth = context.guiWidth();
		int iconX = screenWidth - EFFECT_SIZE - MARGIN_RIGHT;
		float tickRate = client.level != null ? client.level.tickRateManager().tickrate() : 20f;

		for (int i = 0; i < visible.size(); i++) {
			MobEffectInstance effect = visible.get(i);
			int rowY = 4 + i * ROW_HEIGHT;

			Identifier sprite = Gui.getMobEffectSprite(effect.getEffect());

			// Vanilla alpha blink formula
			float alpha = 1.0f;
			if (effect.endsWithin(200) && !effect.isInfiniteDuration()) {
				int duration = effect.getDuration();
				int remainingTicks = 10 - duration / 20;
				float alpha1 = Mth.clamp(duration / 10.0f / 5.0f * 0.5f, 0f, 0.5f);
				float alpha2 = Mth.clamp((float) Mth.cos(duration * Math.PI / 5.0) * (remainingTicks / 10.0f) * 0.25f, 0f, 0.25f);
				alpha = Mth.clamp(alpha1 + alpha2, 0f, 1f);
			}

			int iconColor = net.minecraft.util.ARGB.white(alpha);
			context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, iconX, rowY, EFFECT_SIZE, EFFECT_SIZE, iconColor);

			// Effect name with amplifier
			String name = effect.getEffect().value().getDisplayName().getString();
			if (effect.getAmplifier() > 0) {
				name = name + " " + toRomanNumeral(effect.getAmplifier() + 1);
			}

			// Timer
			String timer;
			if (effect.isInfiniteDuration()) {
				timer = "\u221e";
			} else {
				timer = StringUtil.formatTickDuration(effect.getDuration(), tickRate);
			}

			// Colors — ambient uses #77FDFD, timer always white
			int nameColor  = effect.isAmbient() ? AMBIENT_COLOR : TEXT_COLOR;
			int timerColor = TEXT_COLOR;

			boolean showNames = com.armaninyow.numericalhud.ModConfig.get().showEffectNames;

			if (showNames) {
				// Right-align text to left of icon
				int nameWidth  = client.font.width(name);
				int timerWidth = client.font.width(timer);
				int nameX  = iconX - nameWidth  - 4;
				int timerX = iconX - timerWidth - 4;

				int nameY  = rowY;
				int timerY = rowY + 9;

				drawText(context, name,  nameX,  nameY,  nameColor);
				drawText(context, timer, timerX, timerY, timerColor);
			} else {
				// Only timer, vertically centered in the 18px icon height
				int timerWidth = client.font.width(timer);
				int timerX = iconX - timerWidth - 4;
				int timerY = rowY + (EFFECT_SIZE / 2) - (client.font.lineHeight / 2);

				drawText(context, timer, timerX, timerY, timerColor);
			}
		}
	}

	private static String toRomanNumeral(int level) {
		return switch (level) {
			case 2  -> "II";
			case 3  -> "III";
			case 4  -> "IV";
			case 5  -> "V";
			case 6  -> "VI";
			case 7  -> "VII";
			case 8  -> "VIII";
			case 9  -> "IX";
			case 10 -> "X";
			default -> String.valueOf(level);
		};
	}
}