package com.armaninyow.numericalhud.hud;

import com.armaninyow.numericalhud.hud.modules.*;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;

public class HudRenderer implements HudRenderCallback {
    
    private final HealthModule healthModule = new HealthModule();
    private final ArmorModule armorModule = new ArmorModule();
    private final HungerModule hungerModule = new HungerModule();
    private final XpModule xpModule = new XpModule();
    private final OxygenModule oxygenModule = new OxygenModule();
    private final VehicleHealthModule vehicleHealthModule = new VehicleHealthModule();
    
    @Override
    public void onHudRender(DrawContext context, RenderTickCounter renderTickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.player == null || client.options.hudHidden) {
            return;
        }
        
        PlayerEntity player = client.player;
        
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        
        // XP bar dimensions (same as vanilla)
        int xpBarWidth = 182;
        int centerX = screenWidth / 2;
        int xpBarLeft = centerX - (xpBarWidth / 2);
        int xpBarRight = xpBarLeft + xpBarWidth;
        
        // Position modules above the XP bar (at Y = screenHeight - 27 - 12)
        int moduleY = screenHeight - 39; // 39 = 27 (XP bar base) + 12 (offset above)
        
        int healthX = xpBarLeft;
        int armorX = xpBarLeft + (xpBarWidth / 4);
        int hungerX = xpBarLeft + (xpBarWidth / 2);
        int xpX = xpBarLeft + (3 * xpBarWidth / 4);
        
        // Render modules
        healthModule.render(context, player, healthX, moduleY, 0);
        armorModule.render(context, player, armorX, moduleY, 0);
        hungerModule.render(context, player, hungerX, moduleY, 0);
        xpModule.render(context, player, xpX, moduleY, 0);
        
        // Oxygen module (12 pixels above health module) - show when underwater OR when air < max
        if (player.isSubmergedInWater() || player.getAir() < 300) {
            oxygenModule.render(context, player, healthX, moduleY - 12, 0);
        }
        
        // Vehicle health module (12 pixels above XP module)
        if (player.hasVehicle() && player.getVehicle() != null) {
            vehicleHealthModule.render(context, player, xpX, moduleY - 12, 0);
        }
    }
}