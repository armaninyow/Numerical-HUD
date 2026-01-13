package com.armaninyow.numericalhud;

import com.armaninyow.numericalhud.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class NumericalHudClient implements ClientModInitializer {
    public static final String MOD_ID = "numericalhud";
    
    @Override
    public void onInitializeClient() {
        // Register HUD renderer
        HudRenderCallback.EVENT.register(new HudRenderer());
        
        System.out.println("Numerical HUD initialized!");
    }
}