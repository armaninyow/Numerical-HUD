package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ArmorModule extends BaseHudModule {
    
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_RED = 0xFFFF0000;
    private static final int COLOR_GREEN = 0xFF00FF00;
    
    private int lastArmor = 0;
    
    @Override
    public void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta) {
        int armor = player.getArmor();
        
       // Update animation
        updateAnimation(armor, 0.1f);
        
        // Trigger blink on armor change
        if (armor != lastArmor) {
            shouldBlink = true;
            blinkTimer = 0;
            isIncreasing = armor > lastArmor;
        }
        lastArmor = armor;
        
        // Select textures
        Identifier containerTexture = getTexture("armor/armor_empty.png");
        Identifier foregroundTexture = shouldShowBlink() ? 
            getTexture("armor/armor_blinking.png") : 
            getTexture("armor/armor_full.png");
        
        // Render icon (moved 1px up)
        if (armor == 0) {
            drawIcon(context, containerTexture, x, y - 1);
        }
        if (armor > 0) {
            drawIcon(context, foregroundTexture, x, y - 1);
        }
        
        // Render text (no offset)
        int color = getAnimationColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
        String text = formatValue(currentDisplayValue, isAnimating);
        drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, color);
        
        updateBlinkTimer();
    }
}