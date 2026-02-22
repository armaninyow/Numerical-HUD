package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.AnimationStyle;
import com.armaninyow.numericalhud.ModConfig;
import com.armaninyow.numericalhud.NumericalHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

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
	private static final int FADE_DURATION = 20; // 20 game ticks = 1 second
	private int fadeTicks = 0;
	private boolean isFading = false;
	private boolean fadeIncreasing = false;
	private int lastFadeGameTick = -1;
	
	// --- Popup animation state (game-tick-accurate) ---
	private static final int POPUP_DURATION = 20; // 20 game ticks = 1 second
	private static final int POPUP_TRAVEL = 9;
	private int popupTicks = 0;
	private boolean isPopping = false;
	private int popupDelta = 0; // positive = heal, negative = damage
	private int lastPopupGameTick = -1;
	
	public abstract void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta);
	
	protected Identifier getTexture(String path) {
		return Identifier.of(NumericalHud.MOD_ID, "textures/hud/" + path);
	}
	
	protected void drawIcon(DrawContext context, Identifier texture, int x, int y) {
		drawIcon(context, texture, x, y, 1.0f);
	}
	
	protected void drawIcon(DrawContext context, Identifier texture, int x, int y, float alpha) {
		int alphaInt = (int)(alpha * 255);
		int color = (alphaInt << 24) | 0xFFFFFF;
		context.drawTexture(
			net.minecraft.client.gl.RenderPipelines.GUI_TEXTURED,
			texture,
			x, y,
			0.0f, 0.0f,
			ICON_SIZE, ICON_SIZE,
			ICON_SIZE, ICON_SIZE,
			color
		);
	}
	
	protected void drawText(DrawContext context, String text, int x, int y, int color) {
		MinecraftClient client = MinecraftClient.getInstance();
		context.drawTextWithShadow(client.textRenderer, text, x, y, color);
	}

	
	/**
	 * Resets the Decimal animation display value to a known state.
	 * Call this when a module is hidden mid-animation so that when it
	 * reappears the animation starts from the correct baseline.
	 */
	protected void resetDisplayValue(float value) {
		currentDisplayValue = value;
		targetValue = value;
		isAnimating = false;
		animationTicks = 0;
		cooldownTicks = 0;
	}
	
	// -------------------------------------------------------------------------
	// Style-aware helpers — call these from subclass render() methods
	// -------------------------------------------------------------------------
	
	/**
	 * Returns the text color for the current frame.
	 * Must be called AFTER tickStyleAnimations().
	 */
	protected int getStyledColor(int normalColor, int damageColor, int healColor) {
		AnimationStyle style = ModConfig.get().animationStyle;
		switch (style) {
			case FADE -> {
				if (!isFading) return normalColor;
				// Quint ease-in: slow start, fast finish — color lingers then snaps to normal
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
	
	/**
	 * Returns the formatted text value for the current frame.
	 */
	protected String getStyledText(float rawValue) {
		AnimationStyle style = ModConfig.get().animationStyle;
		switch (style) {
			case FADE, POPUP -> {
				return String.valueOf((int)Math.floor(rawValue));
			}
			default -> { // DECIMAL
				return formatValue(currentDisplayValue, isAnimating);
			}
		}
	}
	
	/**
	 * Renders the popup delta text if active. Call after the main text draw.
	 * x, y should be the same position passed to the module's render().
	 */
	protected void renderPopup(DrawContext context, int x, int y) {
		if (!isPopping || ModConfig.get().animationStyle != AnimationStyle.POPUP) return;
		
		float progress = popupTicks / (float)POPUP_DURATION;
		// Ease-out: square root curve
		float eased = (float)Math.sqrt(progress);
		
		int textX = x + ICON_SIZE + ICON_TEXT_GAP;
		int offsetY;
		int color;
		String sign;
		
		if (popupDelta > 0) {
			// Healing: moves UP 9 pixels, green
			offsetY = (int)(-POPUP_TRAVEL * eased);
			color = 0xFF00FF00;
			sign = "+" + popupDelta;
		} else {
			// Damage: moves DOWN 9 pixels, red
			offsetY = (int)(POPUP_TRAVEL * eased);
			color = 0xFFFF0000;
			sign = String.valueOf(popupDelta);
		}
		
		// Draw popup slightly to the right of the regular text — no fade-out, disappears instantly
		MinecraftClient client = MinecraftClient.getInstance();
		int textWidth = client.textRenderer.getWidth(getStyledText(currentDisplayValue));
		context.drawTextWithShadow(client.textRenderer, sign, textX + textWidth + 2, y + offsetY, color);
	}
	
	/**
	 * Must be called every frame to tick the Fade/Popup animation state.
	 * Uses game tick time so the counter advances exactly once per game tick
	 * (20 ticks/second) regardless of frame rate.
	 *
	 * @param rawValue  The actual game value this frame.
	 * @param prevValue The value stored from the last frame (caller's field).
	 * @return rawValue, for convenience when assigning back to the caller's field.
	 */
	protected float tickStyleAnimations(float rawValue, float prevValue) {
		AnimationStyle style = ModConfig.get().animationStyle;
		
		MinecraftClient client = MinecraftClient.getInstance();
		int currentGameTick = (client.player != null && client.world != null)
			? (int)client.world.getTime()
			: -1;
		
		// Both Fade and Popup compare integer values only, so sub-integer
		// regen ticks (e.g. food saturation) never trigger a false animation.
		int intRaw  = (int)Math.floor(rawValue);
		int intPrev = prevValue == Float.MAX_VALUE ? intRaw : (int)Math.floor(prevValue);
		
		if (style == AnimationStyle.FADE) {
			// Trigger only when the displayed integer changes
			if (prevValue != Float.MAX_VALUE && intRaw != intPrev) {
				fadeIncreasing = intRaw > intPrev;
				fadeTicks = 0;
				isFading = true;
				lastFadeGameTick = currentGameTick;
			}
			// Advance only once per game tick
			if (isFading && currentGameTick >= 0 && currentGameTick != lastFadeGameTick) {
				lastFadeGameTick = currentGameTick;
				fadeTicks++;
				if (fadeTicks >= FADE_DURATION) {
					isFading = false;
					fadeTicks = 0;
				}
			}
		} else if (style == AnimationStyle.POPUP) {
			// Trigger on integer-value change only
			if (prevValue != Float.MAX_VALUE) {
				int delta = intRaw - intPrev;
				if (delta != 0) {
					// Interrupt any running popup immediately
					popupDelta = delta;
					popupTicks = 0;
					isPopping = true;
					lastPopupGameTick = currentGameTick;
				}
			}
			// Advance only once per game tick
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
		
		// Keep currentDisplayValue in sync for non-decimal styles
		if (style != AnimationStyle.DECIMAL) {
			currentDisplayValue = rawValue;
			targetValue = rawValue;
		}
		
		return rawValue;
	}
	
	// -------------------------------------------------------------------------
	// Existing decimal animation (unchanged)
	// -------------------------------------------------------------------------
	
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
		
		// If a new change arrives in the opposite direction while on cooldown,
		// cancel the cooldown immediately so the animation isn't lost.
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
		if (!isAnimating) {
			return normalColor;
		}
		return isIncreasing ? healColor : damageColor;
	}
	
	protected String formatValue(float value, boolean showDecimal) {
		if (showDecimal && isAnimating) {
			return String.format("%.1f", value);
		}
		return String.valueOf((int)Math.floor(value));
	}
	
	// -------------------------------------------------------------------------
	// Blink helpers (unchanged)
	// -------------------------------------------------------------------------
	
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
		
		MinecraftClient client = MinecraftClient.getInstance();
		int currentTick = client.player != null && client.world != null ?
			(int)client.world.getTime() : 0;
		
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
	
	// -------------------------------------------------------------------------
	// Utility
	// -------------------------------------------------------------------------
	
	/**
	 * Linear interpolation between two ARGB colors.
	 * t=0 returns colorA, t=1 returns colorB.
	 */
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