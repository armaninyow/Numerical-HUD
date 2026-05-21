package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
import com.armaninyow.numericalhud.NumericalHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;

public abstract class BaseHudModule {
	
	protected static final int ICON_SIZE = 9;
	protected static final int ICON_TEXT_GAP = 3;
	protected static final int ANIMATION_TICKS = 10;
	
	// --- Decimal animation state ---
	protected float currentDisplayValue = 0f;
	protected float targetValue = 0f;
	protected float animationStartValue = 0f;
	protected int animationTicks = 0;
	protected boolean isAnimating = false;
	protected boolean isIncreasing = false;
	protected int cooldownTicks = 0;
	
	// --- Blink state ---
	protected int blinkTimer = 0;
	protected boolean shouldBlink = false;
	protected int recurringBlinkTimer = 0;
	protected int currentBlinkInterval = 0;
	protected boolean recurringBlinkActive = false;
	protected int lastTickCount = -1;
	
	// --- Fade animation state (game-tick-accurate) ---
	private static final int FADE_DURATION = 20;
	private int fadeTicks = 0;
	private boolean isFading = false;
	private boolean fadeIncreasing = false;
	private int lastFadeGameTick = -1;
	
	// --- Popup animation state (game-tick-accurate) ---
	private static final int POPUP_DURATION = 20;
	private static final int POPUP_TRAVEL = 9;
	private int popupTicks = 0;
	private boolean isPopping = false;
	private int popupDelta = 0;
	private int lastPopupGameTick = -1;
	
	public abstract void render(GuiGraphicsExtractor context, Player player, int x, int y, float tickDelta);

	protected abstract IconRenderer getIconRenderer();

	protected Identifier getTexture(String path) {
		return Identifier.fromNamespaceAndPath(NumericalHud.MOD_ID, "textures/hud/" + path);
	}
	
	protected void drawIcon(GuiGraphicsExtractor context, Identifier texture, int x, int y) {
		drawIcon(context, texture, x, y, 1.0f);
	}
	
	protected void drawIcon(GuiGraphicsExtractor context, Identifier texture, int x, int y, float alpha) {
		getIconRenderer().drawIcon(context, texture, x, y, ICON_SIZE, alpha);
	}

	protected void drawIcon(GuiGraphicsExtractor context, Identifier texture, int x, int y, int color) {
		getIconRenderer().drawIcon(context, texture, x, y, ICON_SIZE, color);
	}

	protected void drawBlinkOverlay(GuiGraphicsExtractor context, Identifier texture, int x, int y) {
		getIconRenderer().drawIcon(context, texture, x, y, ICON_SIZE, 0xBFFFFFFF);
	}

	protected void drawVanillaSprite(GuiGraphicsExtractor context, Identifier sprite, int x, int y) {
		getIconRenderer().drawVanillaSprite(context, sprite, x, y, ICON_SIZE, 0xFFFFFFFF);
	}

	protected void drawVanillaXpBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y) {
		getIconRenderer().drawVanillaXpBar(context, sprite, x, y);
	}

	protected void drawVanillaJumpBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y, boolean rightEnd) {
		getIconRenderer().drawVanillaJumpBar(context, sprite, x, y, rightEnd);
	}

	protected void drawVanillaBossBar(GuiGraphicsExtractor context, Identifier sprite, int x, int y) {
		getIconRenderer().drawVanillaBossBar(context, sprite, x, y);
	}

	public void flush(GuiGraphicsExtractor context) {
		getIconRenderer().flush(context);
	}
	
	protected void drawText(GuiGraphicsExtractor context, String text, int x, int y, int color) {
		Minecraft client = Minecraft.getInstance();
		if (ModConfig.get().textStyle == com.armaninyow.numericalhud.TextStyle.OUTLINE) {
			drawTextOutline(context, client, text, x, y, color);
		} else {
			context.text(client.font, text, x, y, color, true);
		}
	}

	private void drawTextOutline(GuiGraphicsExtractor context, Minecraft client, String text, int x, int y, int color) {
		int a  = (color >> 24) & 0xFF;
		int r  = ((color >> 16) & 0xFF) / 2;
		int g  = ((color >>  8) & 0xFF) / 2;
		int b  = (color & 0xFF) / 2;
		int outlineColor = (a << 24) | (r << 16) | (g << 8) | b;
		context.text(client.font, text, x - 1, y - 1, outlineColor, false);
		context.text(client.font, text, x,     y - 1, outlineColor, false);
		context.text(client.font, text, x + 1, y - 1, outlineColor, false);
		context.text(client.font, text, x - 1, y,     outlineColor, false);
		context.text(client.font, text, x + 1, y,     outlineColor, false);
		context.text(client.font, text, x - 1, y + 1, outlineColor, false);
		context.text(client.font, text, x,     y + 1, outlineColor, false);
		context.text(client.font, text, x + 1, y + 1, outlineColor, false);
		context.text(client.font, text, x, y, color, false);
	}

	protected void resetDisplayValue(float value) {
		currentDisplayValue = value;
		targetValue = value;
		isAnimating = false;
		animationTicks = 0;
		cooldownTicks = 0;
	}
	
	protected int getStyledColor(int normalColor, int damageColor, int healColor) {
		AnimationStyle style = ModConfig.get().animationStyle;
		switch (style) {
			case FADE -> {
				if (!isFading) return normalColor;
				float t = fadeTicks / (float)FADE_DURATION;
				float eased = t * t * t * t * t;
				int fromColor = fadeIncreasing ? healColor : damageColor;
				return lerpColor(fromColor, normalColor, eased);
			}
			case POPUP -> {
				return normalColor;
			}
			default -> {
				return getAnimationColor(normalColor, damageColor, healColor);
			}
		}
	}
	
	protected String getStyledText(float rawValue) {
		AnimationStyle style = ModConfig.get().animationStyle;
		switch (style) {
			case FADE, POPUP -> {
				return String.valueOf((int)Math.floor(rawValue));
			}
			default -> {
				return formatValue(currentDisplayValue, isAnimating);
			}
		}
	}
	
	protected void renderPopup(GuiGraphicsExtractor context, int x, int y) {
		if (!isPopping || ModConfig.get().animationStyle != AnimationStyle.POPUP) return;
		
		float progress = popupTicks / (float)POPUP_DURATION;
		float eased = (float)Math.sqrt(progress);
		
		int textX = x + ICON_SIZE + ICON_TEXT_GAP;
		int offsetY;
		int color;
		String sign;
		
		if (popupDelta > 0) {
			offsetY = (int)(-POPUP_TRAVEL * eased);
			color = 0xFF00FF00;
			sign = "+" + popupDelta;
		} else {
			offsetY = (int)(-POPUP_TRAVEL * eased);
			color = 0xFFFF0000;
			sign = String.valueOf(popupDelta);
		}
		
		Minecraft client = Minecraft.getInstance();
		int textWidth = client.font.width(getStyledText(currentDisplayValue));
		if (ModConfig.get().textStyle == com.armaninyow.numericalhud.TextStyle.OUTLINE) {
			drawTextOutline(context, client, sign, textX + textWidth + 2, y + offsetY, color);
		} else {
			context.text(client.font, sign, textX + textWidth + 2, y + offsetY, color, true);
		}
	}
	
	protected float tickStyleAnimations(float rawValue, float prevValue) {
		AnimationStyle style = ModConfig.get().animationStyle;
		
		Minecraft client = Minecraft.getInstance();
		int currentGameTick = (client.player != null && client.level != null)
			? (int)client.level.getGameTime()
			: -1;
		
		int intRaw  = (int)Math.floor(rawValue);
		int intPrev = prevValue == Float.MAX_VALUE ? intRaw : (int)Math.floor(prevValue);
		
		if (style == AnimationStyle.FADE) {
			if (prevValue != Float.MAX_VALUE && intRaw != intPrev) {
				fadeIncreasing = intRaw > intPrev;
				fadeTicks = 0;
				isFading = true;
				lastFadeGameTick = currentGameTick;
			}
			if (isFading && currentGameTick >= 0 && currentGameTick != lastFadeGameTick) {
				lastFadeGameTick = currentGameTick;
				fadeTicks++;
				if (fadeTicks >= FADE_DURATION) {
					isFading = false;
					fadeTicks = 0;
				}
			}
		} else if (style == AnimationStyle.POPUP) {
			if (prevValue != Float.MAX_VALUE) {
				int delta = intRaw - intPrev;
				if (delta != 0) {
					popupDelta = delta;
					popupTicks = 0;
					isPopping = true;
					lastPopupGameTick = currentGameTick;
				}
			}
			if (isPopping && currentGameTick >= 0 && currentGameTick != lastPopupGameTick) {
				lastPopupGameTick = currentGameTick;
				popupTicks++;
				if (popupTicks >= POPUP_DURATION) {
					isPopping = false;
					popupTicks = 0;
					popupDelta = 0;
				}
			}
		}
		
		if (style != AnimationStyle.DECIMAL) {
			currentDisplayValue = rawValue;
			targetValue = rawValue;
		}
		
		return rawValue;
	}
	
	protected void updateAnimation(float newTargetValue, float increment) {
		if (cooldownTicks > 0) {
			cooldownTicks--;
		}
		
		if (!isAnimating && cooldownTicks == 0) {
			int intNew     = (int)Math.floor(newTargetValue);
			int intCurrent = (int)Math.floor(currentDisplayValue);
			if (intNew != intCurrent) {
				animationStartValue = currentDisplayValue;
				targetValue = newTargetValue;
				isAnimating = true;
				animationTicks = 0;
				isIncreasing = targetValue > animationStartValue;
			} else {
				currentDisplayValue = newTargetValue;
				targetValue = newTargetValue;
			}
		}
		
		if (!isAnimating && cooldownTicks > 0) {
			int intNew     = (int)Math.floor(newTargetValue);
			int intCurrent = (int)Math.floor(currentDisplayValue);
			boolean wouldBeOpposite = (intNew > intCurrent && !isIncreasing)
				|| (intNew < intCurrent && isIncreasing);
			if (intNew != intCurrent && wouldBeOpposite) {
				cooldownTicks = 0;
				animationStartValue = currentDisplayValue;
				targetValue = newTargetValue;
				isAnimating = true;
				animationTicks = 0;
				isIncreasing = targetValue > animationStartValue;
			}
		}
		
		if (isAnimating) {
			animationTicks++;
			float progress = animationTicks / (float)ANIMATION_TICKS;
			if (isIncreasing) {
				float decimalEnd = (float)Math.floor(targetValue) + 0.9f;
				currentDisplayValue = animationStartValue + (decimalEnd - animationStartValue) * progress;
			} else {
				float decimalEnd = (float)Math.ceil(targetValue) + 0.1f;
				currentDisplayValue = animationStartValue + (decimalEnd - animationStartValue) * progress;
			}
			if (animationTicks >= ANIMATION_TICKS) {
				currentDisplayValue = targetValue;
				isAnimating = false;
				cooldownTicks = 5;
			}
		}
	}
	
	protected int getAnimationColor(int normalColor, int damageColor, int healColor) {
		if (!isAnimating) return normalColor;
		return isIncreasing ? healColor : damageColor;
	}
	
	protected String formatValue(float value, boolean showDecimal) {
		if (showDecimal && isAnimating) {
			return String.format("%.1f", value);
		}
		return String.valueOf((int)Math.floor(value));
	}
	
	protected void updateBlinkTimer() {
		if (shouldBlink) {
			blinkTimer++;
			if (blinkTimer >= 10) {
				shouldBlink = false;
				blinkTimer = 0;
			}
		}
	}
	
	protected boolean shouldShowBlink() {
		return shouldBlink && (blinkTimer % 4 < 2);
	}
	
	protected void updateRecurringBlink(float currentValue, float maxValue) {
		int currentIntValue = (int)Math.floor(currentValue);
		
		if (currentValue >= maxValue) {
			recurringBlinkActive = false;
			recurringBlinkTimer = 0;
			return;
		}
		
		Minecraft client = Minecraft.getInstance();
		int currentTick = client.player != null && client.level != null ?
			(int)client.level.getGameTime() : 0;
		
		int interval = Math.max(currentIntValue, 1) * 20;
		
		if (recurringBlinkActive) {
			if (currentTick != lastTickCount) {
				lastTickCount = currentTick;
				recurringBlinkTimer++;
			}
			if (recurringBlinkTimer >= interval) {
				shouldBlink = true;
				blinkTimer = 0;
				recurringBlinkTimer = 0;
			}
		} else {
			lastTickCount = currentTick;
		}
	}
	
	protected void triggerDamageBlink(float currentValue, float previousValue) {
		if (currentValue < previousValue) {
			shouldBlink = true;
			blinkTimer = 0;
			recurringBlinkActive = true;
			recurringBlinkTimer = 0;
			currentBlinkInterval = Math.max((int)Math.floor(currentValue), 1) * 20;
		}
	}
	
	private static int lerpColor(int colorA, int colorB, float t) {
		int aA = (colorA >> 24) & 0xFF;
		int rA = (colorA >> 16) & 0xFF;
		int gA = (colorA >> 8) & 0xFF;
		int bA = colorA & 0xFF;
		
		int aB = (colorB >> 24) & 0xFF;
		int rB = (colorB >> 16) & 0xFF;
		int gB = (colorB >> 8) & 0xFF;
		int bB = colorB & 0xFF;
		
		int a = (int)(aA + (aB - aA) * t);
		int r = (int)(rA + (rB - rA) * t);
		int g = (int)(gA + (gB - gA) * t);
		int b = (int)(bA + (bB - bA) * t);
		
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
}