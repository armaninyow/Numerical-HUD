package com.armaninyow.numericalhud.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 1.21.2_1.21.5
@Mixin(InGameHud.class)
public class ExperienceBarMixin {

	@Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
	private void cancelExperienceBar(DrawContext context, int x, CallbackInfo ci) {
		ci.cancel();
	}
}