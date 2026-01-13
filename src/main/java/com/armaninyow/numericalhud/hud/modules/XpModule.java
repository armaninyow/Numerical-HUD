package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.XpDataHolder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class XpModule extends BaseHudModule {
    
    private static final int COLOR_GREEN = 0xFF80C71F;
    private static final int COLOR_YELLOW = 0xFFFED83D;
    
    private boolean firstRender = true;
    
    @Override
    public void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta) {
        // Always use the holder data (it's set before player level is hidden)
        int level = XpDataHolder.getRealXpLevel();
        float progress = XpDataHolder.getRealXpProgress();
        
        // Skip rendering if we don't have valid data yet (still at initialization values)
        if (!XpDataHolder.isInitialized()) {
            return;
        }
        
        int currentXp = (int)(progress * getXpForLevel(level));
        int totalXpForLevel = getXpForLevel(level);
        
        // Calculate level.percent
        float levelPercent = level + (currentXp / (float)totalXpForLevel);
        
        // On first render, initialize the animation value to current value (no animation)
        if (firstRender) {
            currentDisplayValue = levelPercent;
            firstRender = false;
        }
        
        // Update animation with faster increment (0.01 steps)
        updateAnimation(levelPercent, 0.01f);
        
        // Select textures
        Identifier containerTexture = getTexture("xp/xp_orb_container.png");
        Identifier foregroundTexture = isAnimating ? 
            getTexture("xp/xp_orb_yellow.png") : 
            getTexture("xp/xp_orb_green.png");
        
        // Render icon (moved 1px up)
        drawIcon(context, containerTexture, x, y - 1);
        
        // Only show green orb if level > 0.00
        if (levelPercent > 0.01f) {
            drawIcon(context, foregroundTexture, x, y - 1);
        }
        
        // Render text (no offset)
        int color = isAnimating ? COLOR_YELLOW : COLOR_GREEN;
        String text = String.format("%.2f", currentDisplayValue);
        drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, color);
    }
    
    private int getXpForLevel(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }
}