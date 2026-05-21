package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;

public class OxygenModule extends BaseHudModule {
	
	private static final int COLOR_WHITE = 0xFFFFFFFF;
	private static final int COLOR_RED = 0xFFFF0000;
	private static final int COLOR_GREEN = 0xFF00FF00;
	private static final int MAX_AIR = 300;
	
	private int lastAirSeconds = 15;
	private float lastAirSecondsFloat = Float.MAX_VALUE;
	private int popAnimationTick = 0;
	private int pushAnimationTick = 0;
	private boolean isPopping = false;
	private boolean isPushing = false;
	
	private static final Identifier AIR_SPRITE         = Identifier.fromNamespaceAndPath("minecraft", "hud/air");
	private static final Identifier AIR_BURSTING_SPRITE = Identifier.fromNamespaceAndPath("minecraft", "hud/air_bursting");
	private static final Identifier AIR_EMPTY_SPRITE   = Identifier.fromNamespaceAndPath("minecraft", "hud/air_empty");

	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(GuiGraphicsExtractor context, Player player, int x, int y, float tickDelta) {
		int air = player.getAirSupply();
		int airSeconds = air / 20;
		boolean hasWaterBreathing = player.hasEffect(MobEffects.WATER_BREATHING) ||
								   player.hasEffect(MobEffects.CONDUIT_POWER);
		
		AnimationStyle style = ModConfig.get().animationStyle;
		
		if (style == AnimationStyle.DECIMAL) {
			updateAnimation(airSeconds, 0.1f);
		} else {
			tickStyleAnimations(airSeconds, lastAirSecondsFloat);
		}
		lastAirSecondsFloat = airSeconds;
		
		if (airSeconds < lastAirSeconds && !isPopping && air > 0) {
			isPopping = true;
			popAnimationTick = 0;
		} else if (airSeconds > lastAirSeconds && !isPushing) {
			isPushing = true;
			pushAnimationTick = 0;
		}
		lastAirSeconds = airSeconds;
		
		if (air <= 0) {
			drawVanillaSprite(context, AIR_EMPTY_SPRITE, x, y - 1);
		} else if (isPopping) {
			popAnimationTick++;
			if (popAnimationTick <= 5) {
				float t = popAnimationTick / 5.0f;
				float alpha = 1.0f - (t * t);
				int color = ((int)(alpha * 255) << 24) | 0xFFFFFF;
				getIconRenderer().drawVanillaSprite(context, AIR_BURSTING_SPRITE, x, y - 1, ICON_SIZE, color);
			} else {
				float t = (popAnimationTick - 5) / 15.0f;
				float alpha = t * t;
				int color = ((int)(alpha * 255) << 24) | 0xFFFFFF;
				getIconRenderer().drawVanillaSprite(context, AIR_SPRITE, x, y - 1, ICON_SIZE, color);
			}
			if (popAnimationTick >= 20) {
				isPopping = false;
				popAnimationTick = 0;
			}
		} else if (isPushing) {
			pushAnimationTick++;
			context.enableScissor(x, y - 1, x + ICON_SIZE, y - 1 + ICON_SIZE);
			float progress = pushAnimationTick / 10.0f;
			int outgoingOffset = (int)(-9 * progress);
			int incomingOffset = (int)(9 - (9 * progress));
			drawVanillaSprite(context, AIR_SPRITE, x, y - 1 + outgoingOffset);
			drawVanillaSprite(context, AIR_SPRITE, x, y - 1 + incomingOffset);
			context.disableScissor();
			if (pushAnimationTick >= 10) {
				isPushing = false;
				pushAnimationTick = 0;
			}
		} else {
			drawVanillaSprite(context, AIR_SPRITE, x, y - 1);
		}
		
		int textColor;
		if (air <= 0) {
			textColor = COLOR_RED;
		} else {
			textColor = getStyledColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
		}
		
		String text;
		if (hasWaterBreathing) {
			text = "∞";
		} else {
			text = getStyledText(airSeconds);
		}
		
		drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
		
		renderPopup(context, x, y);
	}
}