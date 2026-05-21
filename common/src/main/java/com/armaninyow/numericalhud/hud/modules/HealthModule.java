package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;

public class HealthModule extends BaseHudModule {
	
	private static final int COLOR_WHITE = 0xFFFFFFFF;
	private static final int COLOR_RED = 0xFFFF0000;
	private static final int COLOR_GREEN = 0xFF00FF00;
	private static final int PANIC_THRESHOLD = 4;
	
	private float lastHealth = Float.MAX_VALUE;

	// Vanilla-style blink state
	private long healthBlinkTime = 0;
	private int lastHealthInt = -1;

	// Recurring blink state (tickCount-based)
	private long nextRecurringBlink = -1;
	
	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(GuiGraphicsExtractor context, Player player, int x, int y, float tickDelta) {
		float health = player.getHealth();
		float absorption = player.getAbsorptionAmount();
		float maxHealth = player.getMaxHealth();
		
		float totalHealth = health + absorption;

		Minecraft client = Minecraft.getInstance();
		boolean isHardcore = client.level != null && client.level.getLevelData().isHardcore();
		
		boolean isPoisoned = player.hasEffect(MobEffects.POISON);
		boolean isWithered = player.hasEffect(MobEffects.WITHER);
		boolean isFrozen = player.isFullyFrozen();
		
		AnimationStyle style = ModConfig.get().animationStyle;
		
		if (style == AnimationStyle.DECIMAL) {
			updateAnimation(totalHealth, 0.1f);
		} else {
			tickStyleAnimations(totalHealth, lastHealth);
		}
		lastHealth = totalHealth;

		// Vanilla heart blink logic
		int tickCount = client.gui.getGuiTicks();
		int healthInt = (int) Math.ceil(health);

		if (lastHealthInt >= 0) {
			if (healthInt < lastHealthInt && player.invulnerableTime > 0) {
				healthBlinkTime = tickCount + 20;
				if (nextRecurringBlink < 0) nextRecurringBlink = tickCount + Math.max(healthInt, 1) * 20;
			} else if (healthInt > lastHealthInt && player.invulnerableTime > 0) {
				healthBlinkTime = tickCount + 10;
			}
		} else {
			// First render — initialize recurring blink if below max health
			if (totalHealth < maxHealth) {
				nextRecurringBlink = tickCount + Math.max((int) totalHealth, 1) * 20;
			}
		}
		lastHealthInt = healthInt;

		// Recurring blink — fires periodically while health is below max
		// Uses a 20-tick window so the vanilla formula produces multiple flashes
		if (totalHealth < maxHealth && nextRecurringBlink >= 0) {
			if (tickCount >= nextRecurringBlink) {
				healthBlinkTime = tickCount + 20;
				nextRecurringBlink = tickCount + Math.max((int) totalHealth, 1) * 20;
			}
		} else if (totalHealth >= maxHealth) {
			nextRecurringBlink = -1;
		}

		boolean blink = healthBlinkTime > tickCount
			&& ((healthBlinkTime - tickCount) / 3) % 2 == 1;

		int textColor;
		if (health <= PANIC_THRESHOLD) {
			textColor = COLOR_RED;
		} else {
			textColor = getStyledColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		}
		
		Identifier containerTexture = getContainerTexture(isHardcore, blink);
		Identifier foregroundTexture = getForegroundTexture(
			absorption > 0, isPoisoned, isWithered, isFrozen, isHardcore, blink
		);
		
		drawVanillaSprite(context, containerTexture, x, y - 1);
		drawVanillaSprite(context, foregroundTexture, x, y - 1);
		
		String text = getStyledText(totalHealth);
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
		
		renderPopup(context, x, y);
	}
	
	private Identifier getContainerTexture(boolean hardcore, boolean blink) {
		if (blink) {
			return hardcore ?
				Identifier.fromNamespaceAndPath("minecraft", "hud/heart/container_hardcore_blinking") :
				Identifier.fromNamespaceAndPath("minecraft", "hud/heart/container_blinking");
		}
		return hardcore ?
			Identifier.fromNamespaceAndPath("minecraft", "hud/heart/container_hardcore") :
			Identifier.fromNamespaceAndPath("minecraft", "hud/heart/container");
	}
	
	private Identifier getForegroundTexture(boolean absorption, boolean poisoned,
										   boolean withered, boolean frozen,
										   boolean hardcore, boolean blink) {
		String suffix = blink ? "_blinking" : "";
		if (absorption) {
			return Identifier.fromNamespaceAndPath("minecraft", "hud/heart/" + (hardcore ? "absorbing_hardcore_full" : "absorbing_full") + suffix);
		}
		if (poisoned) {
			return Identifier.fromNamespaceAndPath("minecraft", "hud/heart/" + (hardcore ? "poisoned_hardcore_full" : "poisoned_full") + suffix);
		}
		if (withered) {
			return Identifier.fromNamespaceAndPath("minecraft", "hud/heart/" + (hardcore ? "withered_hardcore_full" : "withered_full") + suffix);
		}
		if (frozen) {
			return Identifier.fromNamespaceAndPath("minecraft", "hud/heart/" + (hardcore ? "frozen_hardcore_full" : "frozen_full") + suffix);
		}
		return Identifier.fromNamespaceAndPath("minecraft", "hud/heart/" + (hardcore ? "hardcore_full" : "full") + suffix);
	}
}