package com.armaninyow.numericalhud.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.JumpingMount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 1.21_1.21.1
@Mixin(InGameHud.class)
public class JumpBarMixin {

	@Inject(method = "renderMountJumpBar", at = @At("HEAD"), cancellable = true)
	private void cancelMountJumpBar(JumpingMount mount, DrawContext context, int x, CallbackInfo ci) {
		ci.cancel();
	}
}