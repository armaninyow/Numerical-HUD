package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class HungerModule extends BaseHudModule {
    
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_RED = 0xFFFF0000;
    private static final int COLOR_GREEN = 0xFF00FF00;
    
    private int starvationTimer = 0;
    
    @Override
    public void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta) {
        int hunger = player.getHungerManager().getFoodLevel();
        float saturation = player.getHungerManager().getSaturationLevel();
        boolean hasHungerEffect = player.hasStatusEffect(StatusEffects.HUNGER);
        
        // Update animation
        updateAnimation(hunger, 0.1f);
        
        // Starvation animation when hunger is 0 (every 10 ticks)
        int yOffset = 0;
        int textColor;
        
        if (hunger == 0) {
            starvationTimer++;
            yOffset = ((starvationTimer / 10) % 2 == 0) ? 1 : -1; // Every 10 ticks
            textColor = COLOR_RED; // Always static red when hunger = 0
        } else {
            starvationTimer = 0;
            textColor = getAnimationColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
        }
        
        // Select textures
        Identifier containerTexture = hasHungerEffect ?
            getTexture("hunger/food_empty_hunger.png") :
            getTexture("hunger/food_empty.png");
        
        Identifier foregroundTexture = hasHungerEffect ?
            getTexture("hunger/food_full_hunger.png") :
            getTexture("hunger/food_full.png");
        
        Identifier saturationTexture = getTexture("hunger/food_saturation.png");
        
        // Render icon (moved 1px up)
        drawIcon(context, containerTexture, x, y + yOffset - 1);
        if (hunger > 0) {
            drawIcon(context, foregroundTexture, x, y + yOffset - 1);
        }
        
        // Render saturation overlay if present
        if (saturation > 0) {
            float saturationAlpha = Math.min(saturation / 20.0f, 1.0f);
            drawIcon(context, saturationTexture, x, y + yOffset - 1, saturationAlpha);
        }
        
        // Render text (no offset)
        String text = formatValue(currentDisplayValue, isAnimating);
        drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, textColor);
    }
}