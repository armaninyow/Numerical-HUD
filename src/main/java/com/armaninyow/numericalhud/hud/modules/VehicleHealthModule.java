package com.armaninyow.numericalhud.hud.modules;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class VehicleHealthModule extends BaseHudModule {
    
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_RED = 0xFFFF0000;
    private static final int COLOR_GREEN = 0xFF00FF00;
    
    private float lastVehicleHealth = 0f;
    
    @Override
    public void render(DrawContext context, PlayerEntity player, int x, int y, float tickDelta) {
        Entity vehicle = player.getVehicle();
        
        if (!(vehicle instanceof LivingEntity livingVehicle)) {
            return;
        }
        
        float health = livingVehicle.getHealth();
        float maxHealth = livingVehicle.getMaxHealth();
        
        // Update animation
        updateAnimation(health, 0.1f);
        
        // Trigger blink on damage
        if (health < lastVehicleHealth) {
            shouldBlink = true;
            blinkTimer = 0;
        }
        lastVehicleHealth = health;
        
        // Select textures
        Identifier containerTexture = getTexture("health/vehicle_container.png");
        Identifier foregroundTexture = shouldShowBlink() ?
            getTexture("health/vehicle_full.png") : // Could add blinking variant
            getTexture("health/vehicle_full.png");
        
        // Render icon
        drawIcon(context, containerTexture, x, y - 1);
        drawIcon(context, foregroundTexture, x, y - 1);
        
        // Render text
        int color = getAnimationColor(COLOR_WHITE, COLOR_RED, COLOR_GREEN);
        String text = formatValue(currentDisplayValue, isAnimating);
        drawText(context, text, x + ICON_SIZE + ICON_TEXT_GAP, y, color);
        
        updateBlinkTimer();
        
        // Render jump bar if applicable
        if (vehicle instanceof AbstractHorseEntity horse) {
            renderJumpBar(context, horse, x, y - 12);
        }
    }
    
    private void renderJumpBar(DrawContext context, AbstractHorseEntity horse, int x, int y) {
        // Jump bar rendering can be implemented here if needed
        // This would show the horse's jump charge
    }
}