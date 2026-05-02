package com.armaninyow.numericalhud.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.ExperienceBar;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 1.21.6_1.21.11
@Mixin(ExperienceBar.class)
public class ExperienceBarMixin {

	@Inject(method = "renderBar", at = @At("HEAD"), cancellable = true)
	private void cancelExperienceBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		ci.cancel();
	}
}