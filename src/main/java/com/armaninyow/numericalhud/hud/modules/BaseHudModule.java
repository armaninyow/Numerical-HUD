package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.NumericalHudClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public abstract class BaseHudModule {
    
    protected static final int ICON_SIZE = 9;
    protected static final int ICON_TEXT_GAP = 3;
    protected static final int ANIMATION_TICKS = 10; // Changed from 5 to 10
    
    protected float currentDisplayValue = 0f;
    protected float targetValue = 0f;
    protected float animationStartValue = 0f;
    protected int animationTicks = 0;
    protected boolean isAnimating = false;
    protected boolean isIncreasing = false;
    protected int cooldownTicks = 0; // Prevent animation restart during cooldown
    
    protected int blinkTimer = 0;
    protected boolean shouldBlink = false;
    
    public abstract void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta);
    
    protected Identifier getTexture(String path) {
        return Identifier.of(NumericalHudClient.MOD_ID, "textures/hud/" + path);
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
    
    protected void updateAnimation(float newTargetValue, float increment) {
        // Decrease cooldown
        if (cooldownTicks > 0) {
            cooldownTicks--;
        }
        
        // Check if we should start a new animation
        if (!isAnimating && cooldownTicks == 0) {
            if (Math.abs(newTargetValue - currentDisplayValue) > 0.5f) {
                // Start new animation
                animationStartValue = currentDisplayValue;
                targetValue = newTargetValue;
                isAnimating = true;
                animationTicks = 0;
                isIncreasing = targetValue > animationStartValue;
            } else {
                // Small change, just update directly
                currentDisplayValue = newTargetValue;
                targetValue = newTargetValue;
            }
        }
        
        // Update ongoing animation
        if (isAnimating) {
            animationTicks++;
            
            // Linear interpolation through decimal values
            float progress = animationTicks / (float)ANIMATION_TICKS;
            
            if (isIncreasing) {
                // Going up: e.g., 7 → 7.9 → 8
                float decimalEnd = (float)Math.floor(targetValue) + 0.9f;
                currentDisplayValue = animationStartValue + (decimalEnd - animationStartValue) * progress;
            } else {
                // Going down: e.g., 8 → 7.1 → 7
                float decimalEnd = (float)Math.ceil(targetValue) + 0.1f;
                currentDisplayValue = animationStartValue + (decimalEnd - animationStartValue) * progress;
            }
            
            // Finish animation after 10 ticks
            if (animationTicks >= ANIMATION_TICKS) {
                currentDisplayValue = targetValue;
                isAnimating = false;
                cooldownTicks = 5; // 5 tick cooldown before next animation can start
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
        return String.valueOf((int)Math.ceil(value));
    }
    
    protected void updateBlinkTimer() {
        if (shouldBlink) {
            blinkTimer++;
            if (blinkTimer >= 10) { // Blink for 10 ticks
                shouldBlink = false;
                blinkTimer = 0;
            }
        }
    }
    
    protected boolean shouldShowBlink() {
        return shouldBlink && (blinkTimer % 4 < 2); // Flash every 2 ticks
    }
}