package com.armaninyow.numericalhud.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContextualBar.class)
public interface ContextualBarRendererMixin {

	@Inject(method = "extractExperienceLevel", at = @At("HEAD"), cancellable = true)
	private static void cancelExperienceLevel(GuiGraphicsExtractor graphics, Font font, int experienceLevel, CallbackInfo ci) {
		ci.cancel();
	}
}