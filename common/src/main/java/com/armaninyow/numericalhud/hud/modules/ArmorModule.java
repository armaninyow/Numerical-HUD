package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;

public class ArmorModule extends BaseHudModule {
	
	private static final int COLOR_WHITE = 0xFFFFFFFF;
	private static final int COLOR_RED   = 0xFFFF0000;
	private static final int COLOR_GREEN = 0xFF00FF00;
	
	private int lastArmor = 0;

	// Vanilla-style blink state
	private long armorBlinkTime = 0;
	private int lastArmorInt = -1;
	
	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(GuiGraphicsExtractor context, Player player, int x, int y, float tickDelta) {
		int armor = player.getArmorValue();
		int prevArmor = lastArmor;
		lastArmor = armor;
		
		if (armor == 0 && !ModConfig.get().showArmorWhenZero) {
			resetDisplayValue(0f);
			lastArmorInt = -1;
			return;
		}
		
		AnimationStyle style = ModConfig.get().animationStyle;
		
		if (style == AnimationStyle.DECIMAL) {
			updateAnimation(armor, 0.1f);
		} else {
			tickStyleAnimations(armor, prevArmor);
		}

		int tickCount = Minecraft.getInstance().gui.getGuiTicks();

		if (lastArmorInt >= 0 && armor != lastArmorInt) {
			if (armor < lastArmorInt) {
				armorBlinkTime = tickCount + 20;
			} else {
				armorBlinkTime = tickCount + 10;
			}
			isIncreasing = armor > lastArmorInt;
		}
		lastArmorInt = armor;

		boolean blink = armorBlinkTime > tickCount
			&& ((armorBlinkTime - tickCount) / 3) % 2 == 1;

		Identifier containerTexture = Identifier.fromNamespaceAndPath("minecraft", "hud/armor_empty");
		Identifier foregroundTexture = Identifier.fromNamespaceAndPath("minecraft", "hud/armor_full");

		if (armor == 0) {
			drawVanillaSprite(context, containerTexture, x, y - 1);
		} else if (blink) {
			drawVanillaSprite(context, containerTexture, x, y - 1);
		} else {
			drawVanillaSprite(context, foregroundTexture, x, y - 1);
		}
		
		int color = getStyledColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		String text = getStyledText(armor);
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, color);
		
		renderPopup(context, x, y);
	}
}