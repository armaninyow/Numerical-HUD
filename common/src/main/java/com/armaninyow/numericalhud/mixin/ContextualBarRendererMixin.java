package com.armaninyow.numericalhud.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContextualBarRenderer.class)
public interface ContextualBarRendererMixin {

	@Inject(method = "extractExperienceLevel", at = @At("HEAD"), cancellable = true)
	private static void cancelExperienceLevel(GuiGraphicsExtractor context, Font font, int level, CallbackInfo ci) {
		ci.cancel();
	}
}